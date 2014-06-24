/**
 * 
 */
package com.example.pmudemo.util;

import android.os.Environment;

/**
 * constants class
 * @author weishijie
 *
 */
public class Const {
	
	/**
	 * voice store path
	 */
	public static final String VOICEPATH = Environment
			.getExternalStorageDirectory().getPath() + "/pmudemo/voice/";
	/**
	 * Time format used in mp3 file name
	 */
	public static final String TIME_FORMAT = "yyyyMMdd_hhmmss";
	/**
	 * Suffix for mp3 file
	 */
	public static final String RECORD_TYPE = ".mp3";
	/**
	 * Sample rate
	 */
	public static final int SAMPLE_RATE = 8000;
	
	/**
	 * Record start
	 */
	public static final int MSG_REC_STARTED = 0;
	/**
	 * Record end
	 */
	public static final int MSG_REC_STOPPED = 1;
	/**
	 * Get buffer size error. set Sample rate may not be supported by device
	 */
	public static final int MSG_ERROR_GET_MIN_BUFFERSIZE = 2;
	/**
	 * Create file error
	 */
	public static final int MSG_ERROR_CREATE_FILE = 3;
	/**
	 * Start record error
	 */
	public static final int MSG_ERROR_REC_START = 4;
	/**
	 * Can't record(occurred only during recording)
	 */
	public static final int MSG_ERROR_AUDIO_RECORD = 5;
	/**
	 * Encoding error(occurred only during recording)
	 */
	public static final int MSG_ERROR_AUDIO_ENCODE = 6;
	/**
	 * Write file error(occurred only during recording)
	 */
	public static final int MSG_ERROR_WRITE_FILE = 7;
	/**
	 * Close file error(occurred only during recording)
	 */
	public static final int MSG_ERROR_CLOSE_FILE = 8;
	/**
	 * Volume
	 */
	public static final int MSG_REC_VOLUME = 9;
	
	/**
	 * Upload url
	 */
	public static final String UPLOAD_FILE_URL = "http://172.26.176.83/pickmeup/audio/upload";
	
}
