/* 
 * Copyright (c) 2011-2012 Yuichi Hirano
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.example.pmudemo.mp3recvoice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

import com.example.pmudemo.util.Const;
import com.uraroji.garage.android.lame.SimpleLame;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;

/**
 * Record mp3 class
 * Record voice through mic and convert to mp3 file in an individual thread
 * @author weishijie
 *
 */
public class RecMicToMp3 {

	/**
	 * Load mp3 encode lib file(wrote by c language) through JNI(Java Native Interface)
	 */
	static {
		System.loadLibrary("mp3lame");
	}
	
	/**
	 * mp3 restore path
	 */
	private String mFilePath;

	/**
	 * Sample rate (采样率)
	 */
	private int mSampleRate;

	/**
	 * isRecording flag
	 */
	private boolean mIsRecording = false;
	/**
	 * 通知录音状态变化的handler
	 * 
	 * @see Const#MSG_REC_STARTED
	 * @see Const#MSG_REC_STOPPED
	 * @see Const#MSG_ERROR_GET_MIN_BUFFERSIZE
	 * @see Const#MSG_ERROR_CREATE_FILE
	 * @see Const#MSG_ERROR_REC_START
	 * @see Const#MSG_ERROR_AUDIO_RECORD
	 * @see Const#MSG_ERROR_AUDIO_ENCODE
	 * @see Const#MSG_ERROR_WRITE_FILE
	 * @see Const#MSG_ERROR_CLOSE_FILE
	 */
	private Handler mHandler;
	
	/**
	 * Constructor method
	 * 
	 * @param filePath file record path
	 * @param sampleRate Sample rate（Hz）
	 */
	public RecMicToMp3(String filePath, int sampleRate) {
		if (sampleRate <= 0) {
			throw new InvalidParameterException(
					"Invalid sample rate specified.");
		}
		this.mFilePath = filePath;
		this.mSampleRate = sampleRate;
	}
	
	/**
	 * Record start
	 */
	public void start() {
		// If is during recording, then do nothing
		if (mIsRecording) {
			return;
		}
		// Create a new thread to record
		new Thread() {
			@Override
			public void run() {
				// Set os process priority
				android.os.Process
						.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
				// Get min buffer size
				final int minBufferSize = AudioRecord.getMinBufferSize(
						mSampleRate, AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT);
				// Can't get min buffer size. set Sample rate may not be supported by device).
				if (minBufferSize < 0) {
					if (mHandler != null) {
						mHandler.sendEmptyMessage(Const.MSG_ERROR_GET_MIN_BUFFERSIZE);
					}
					return;
				}
				// Create an audio record instance
				AudioRecord audioRecord = new AudioRecord(
						// the recording source
						MediaRecorder.AudioSource.MIC, 
						// the sample rate expressed in Hertz. 44100Hz is currently the only rate that is guaranteed to work on all devices
						// lower value such as 4000, 8000 is Ok if the voice quality request is not very high.
						mSampleRate, 
						// the configuration of the audio channels(声道). AudioFormat.CHANNEL_IN_MONO(单声道) is guaranteed to work on all devices.
						AudioFormat.CHANNEL_IN_MONO, 
						// the format in which the audio data is represented.
						// 编码制式和采样大小：采集来的数据当然使用PCM编码(PCM通过抽样、量化、编码三个步骤将连续变化的模拟信号转换为数字编码)
						// 目前主流的采样大小是16bit
						AudioFormat.ENCODING_PCM_16BIT, 
						// min buffer size. I choose [getMinBufferSize() * 2] for this parameter
						minBufferSize * 2);
				
				// PCM buffer size (5sec)
				// 录音编码主要有两种：8位pcm和16位pcm。8位pcm用一个字节表示语音的一个点，
				// 16位pcm用两个字节，也就是一个short来表示语音的一个点。需特别注意的是，如果
				// 采用16位pcm编码，而取录音数据却用byte的话，需要自己将两个byte转换成一个short。
				// 这种转换有小端和大端两种，一般默认都是小端，但有的开源库，比如lamemp3需要的就
				// 是大端，这要根据不同情况进行不同处理
				short[] buffer = new short[mSampleRate * (16 / 8) * 1 * 5]; // SampleRate[Hz] * 16bit * Mono * 5sec
				byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

				FileOutputStream output = null;
				try {
					output = new FileOutputStream(new File(mFilePath));
				} catch (FileNotFoundException e) {
					// create file error
					if (mHandler != null) {
						mHandler.sendEmptyMessage(Const.MSG_ERROR_CREATE_FILE);
					}
					return;
				}
				
				// Lame init
				SimpleLame.init(mSampleRate, 1, mSampleRate, 32);
				// Set recording start flag to true
				mIsRecording = true;
				try {
					try {
						// Start recording
						audioRecord.startRecording();
					} catch (IllegalStateException e) {
						// Start record error
						if (mHandler != null) {
							mHandler.sendEmptyMessage(Const.MSG_ERROR_REC_START);
						}
						return;
					}

					try {
						// Record start
						if (mHandler != null) {
							mHandler.sendEmptyMessage(Const.MSG_REC_STARTED);
						}

						int readSize = 0;
						while (mIsRecording) {
							readSize = audioRecord.read(buffer, 0, minBufferSize);
							
							// Calculate volume
							Message msg = new Message();
							msg.arg1 = calVolume(buffer, readSize);
							msg.what = Const.MSG_REC_VOLUME;
							// Store volume info into handler
							if (mHandler != null) {
								mHandler.sendMessage(msg);
							}
							
							if (readSize < 0) {
								// Can't record(occurred only during recording)
								if (mHandler != null) {
									mHandler.sendEmptyMessage(Const.MSG_ERROR_AUDIO_RECORD);
								}
								break;
							}
							// Do nothing when data can not be read
							else if (readSize == 0) {
								;
							}
							// Recorded data exists
							else {
								// Encode buffer to mp3
								int encResult = SimpleLame.encode(buffer,
										buffer, readSize, mp3buffer);
								// encoding error
								if (encResult < 0) {
									if (mHandler != null) {
										mHandler.sendEmptyMessage(Const.MSG_ERROR_AUDIO_ENCODE);
									}
									break;
								}
								if (encResult != 0) {
									try {
										// Write mp3 data to file
										output.write(mp3buffer, 0, encResult);
									} catch (IOException e) {
										// Write file error
										if (mHandler != null) {
											mHandler.sendEmptyMessage(Const.MSG_ERROR_WRITE_FILE);
										}
										break;
									}
								}
							}
						}

						// Flush LAME buffer
						int flushResult = SimpleLame.flush(mp3buffer);
						if (flushResult < 0) {
							// Encoding error
							if (mHandler != null) {
								mHandler.sendEmptyMessage(Const.MSG_ERROR_AUDIO_ENCODE);
							}
						}
						if (flushResult != 0) {
							try {
								output.write(mp3buffer, 0, flushResult);
							} catch (IOException e) {
								// Write file error
								if (mHandler != null) {
									mHandler.sendEmptyMessage(Const.MSG_ERROR_WRITE_FILE);
								}
							}
						}

						try {
							output.close();
						} catch (IOException e) {
							// Close file error
							if (mHandler != null) {
								mHandler.sendEmptyMessage(Const.MSG_ERROR_CLOSE_FILE);
							}
						}
					} finally {
						// record end
						audioRecord.stop();
						audioRecord.release();
					}
				} finally {
					SimpleLame.close();
					// Set isRecording flag to false
					mIsRecording = false; 
				}

				// Record end
				if (mHandler != null) {
					mHandler.sendEmptyMessage(Const.MSG_REC_STOPPED);
				}
			}
		}.start();
	}
	
	/**
	 * Record stop
	 */
	public void stop() {
		mIsRecording = false;
	}
	
	/**
	 * If recording is running
	 * 
	 * @return 在录制过程中，返回true，否则为false
	 */
	public boolean isRecording() {
		return mIsRecording;
	}
	
	/**
	 * 设置通知录音状态变化的handler
	 * @param handler 通知录音状态变化的handler
	 * 
	 * @see Const#MSG_REC_STARTED
	 * @see Const#MSG_REC_STOPPED
	 * @see Const#MSG_ERROR_GET_MIN_BUFFERSIZE
	 * @see Const#MSG_ERROR_CREATE_FILE
	 * @see Const#MSG_ERROR_REC_START
	 * @see Const#MSG_ERROR_AUDIO_RECORD
	 * @see Const#MSG_ERROR_AUDIO_ENCODE
	 * @see Const#MSG_ERROR_WRITE_FILE
	 * @see Const#MSG_ERROR_CLOSE_FILE
	 */
	public void setHandle(Handler handler) {
		this.mHandler = handler;
	}

	/**
	 * Get mp3 file restore path
	 * 
	 * @return
	 */
	public String getmFilePath() {
		return mFilePath;
	}
	
	/**
	 * Calculate volume 
	 * 
	 * @param buffer
	 * @param readSize 
	 * @return
	 */
	private int calVolume(short[] buffer, int readSize) {
		int volum = 0; 
		// 将 buffer 内容取出，进行平方和运算 
		for (int i = 0; i < buffer.length; i++) { 
			volum += buffer[i] * buffer[i];
		} 
		// 平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。 
		// 如果想利用这个数值进行操作，建议用 sendMessage 将其抛出，在 Handler 里进行处理。 
//		Log.e("spl", String.valueOf(volum / (float) readSize)); 
		return (int) (Math.abs((int)(volum /(float)readSize)/10000) >> 1);
	}
	
}
