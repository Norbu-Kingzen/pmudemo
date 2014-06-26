package com.example.pmudemo.activity;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import com.example.pmudemo.R;
import com.example.pmudemo.R.layout;
import com.example.pmudemo.util.Const;
import com.example.pmudemo.bean.MessageBean;
import com.example.pmudemo.helper.UploadHelper;
import com.example.pmudemo.mp3recvoice.RecMicToMp3;
import com.example.pmudemo.util.Util;
import com.example.pmudemo.view.ChatListView;
import com.example.pmudemo.view.ChatListView.OnErrorClickListener;
import com.example.pmudemo.view.ErrorViewHolder;
import com.example.pmudemo.view.VoiceDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author weishijie
 *
 */
//with this tag, inner class Handler will show warning as follow :
//This Handler class should be static or leaks might occur
@SuppressLint("HandlerLeak")
public class MessageActivity extends Activity {
	/**
	 * Activity button
	 */
    private Button voiceSendBtn;
    /**
     * Message button
     */
    private Button photoSendBtn;
    
    /**
     * sample rate 8000 is supported, resource origin is mic
     */
    private RecMicToMp3 mRecMicToMp3;
    
    private Handler vHandler;
    /**
     * voice dialog
     */
    private VoiceDialog vDialog;
    /**
     * Chat listview
     */
    ChatListView clv;
    /**
     * Temporary head photo for me
     */
    private final int HEADPHOTO_URL_ME = R.drawable.default_head_photo;
    /**
     * Activity id
     */
    private final int ACTIVITY_ID = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		
		init();
		
		// get message container
		LinearLayout clvContainer = (LinearLayout) findViewById(R.id.msg_container);
		clv = new ChatListView(this, new OnErrorClickListener() {
			@Override
			public void onErrorClick(ErrorViewHolder holder, MessageBean mb)
			{
				if (Util.isNetworkConnected(MessageActivity.this))
				{
					if(mb.getType() == MessageBean.MESSAGE_IMG_SEND){
						sendObject(mb.getImgUrl(), true, null, null, -1);
					}else if(mb.getType() == MessageBean.MESSAGE_VOICE_SEND){
						sendObject(mb.getVoiceUrl(), true, null, null, mb.getVoiceLength());
					}
					clv.updateMessage(mb);
				}
			}
		});
		clvContainer.addView(clv);
		
	}
	
	
	/**
	 * Get page widgets, and then add event listeners for them if necessary
	 */
	private void init() {
		voiceSendBtn = (Button)this.findViewById(R.id.voice_send_btn);
		photoSendBtn = (Button)this.findViewById(R.id.photo_send_btn);
		
		// Register long click listener for voice send button
		voiceSendBtn.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// Check if the SDCard is available
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					Util.showShortToast(MessageActivity.this,
							getResString(R.string.sdcard_not_available));
					return false;
				}
				else 
				{
					// construct voice dialog
					vDialog = new VoiceDialog(MessageActivity.this);
					vDialog.show();
					voiceSendBtn.setText(getResString(
							R.string.voice_holded_ctn));
					// prepare for recording
					File file = new File(Const.VOICEPATH);
					file.mkdirs();
					
					// Recorded mp3 file path
					String mFilePath = Const.VOICEPATH
							+ DateFormat.format(Const.TIME_FORMAT,
									Calendar.getInstance(Locale.CHINA))
							+ Const.RECORD_TYPE;
					// Initialize Mp3 recorder
					mRecMicToMp3 = new RecMicToMp3(mFilePath, Const.SAMPLE_RATE);
					if (mRecMicToMp3 != null)
					{
						mRecMicToMp3.setHandle(new Handler() {
							@Override
							public void handleMessage(Message msg)
							{
								switch (msg.what)
								{
								case Const.MSG_REC_STARTED:
									Log.i("Handler", "MSG_REC_STARTED");
									break;
								case Const.MSG_REC_STOPPED:
									dismissVDialog();
									Log.i("Handler", "MSG_REC_STOPPED");
									break;
								case Const.MSG_ERROR_GET_MIN_BUFFERSIZE:
									dismissVDialog();
									Util.showShortToast(
											MessageActivity.this,
											getResString(R.string.error_unsupport_record));
									break;
								case Const.MSG_ERROR_CREATE_FILE:
									dismissVDialog();
									Util.showShortToast(
											MessageActivity.this,
											getResString(R.string.error_create_file));
									break;
								case Const.MSG_ERROR_REC_START:
									dismissVDialog();
									Util.showShortToast(
											MessageActivity.this,
											getResString(R.string.error_initial_record));
									break;
								case Const.MSG_ERROR_AUDIO_RECORD:
									dismissVDialog();
									Util.showShortToast(
											MessageActivity.this,
											getResString(R.string.error_recording));
									break;
								case Const.MSG_ERROR_AUDIO_ENCODE:
									dismissVDialog();
									Util.showShortToast(
											MessageActivity.this,
											getResString(R.string.error_encoding));
									break;
								case Const.MSG_ERROR_WRITE_FILE:
									dismissVDialog();
									Util.showShortToast(
											MessageActivity.this,
											getResString(R.string.error_writing_file));
									break;
								case Const.MSG_ERROR_CLOSE_FILE:
									dismissVDialog();
									Util.showShortToast(
											MessageActivity.this,
											getResString(R.string.error_closing_file));
									break;
								case Const.MSG_REC_VOLUME:
									int volum = msg.arg1;
									Message message = new Message();
									message.what = Const.MSG_REC_VOLUME;
									message.arg1 = volum;
									vHandler.sendMessage(message);
									break;
								default:
									break;
								}
							}
						});
					}
					mRecMicToMp3.start();
					return false;
				}
			}
		});
		
		// Register touch listener for voice send button
		voiceSendBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				// 手指离开触屏
				case MotionEvent.ACTION_UP:
					dismissVDialog();
					voiceSendBtn.setText(getResources().getString(
							R.string.voice_not_hold_ctn));
					// Check if the SDCard is available
					if (!Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)){
					}else{
						if (mRecMicToMp3 != null){
							mRecMicToMp3.stop();
							if (event.getY() > 0){
								Uri playUri = Uri.parse(mRecMicToMp3.getmFilePath());
								MediaPlayer mediaPlayer = MediaPlayer.create(MessageActivity.this, playUri);
								if (Util.isNetworkConnected(MessageActivity.this)){
									// Upload message
									sendObject(mRecMicToMp3.getmFilePath(), false, null, null, mediaPlayer.getDuration() / 1000);
								}else{
									Util.showShortToast(
											MessageActivity.this,
											getResString(R.string.network_not_available));
								}
								mRecMicToMp3 = null;
							}else{
								new Thread(new Runnable() {
									@Override
									public void run()
									{
										File f = new File(mRecMicToMp3
												.getmFilePath());
										if (f.exists())
										{
											f.delete();
										}
										mRecMicToMp3 = null;
									}
								}).start();
							}
						}
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (vDialog != null)
					{
						// finger move up
						if (event.getY() < 0)
						{
							// change text to "Release to cancel"
							vDialog.changeVoiceDialogText(getResources().getString(
									R.string.release_to_cancel));
							// change button background to "darkred"
							vDialog.changeCoiceDialogBg(R.drawable.round_button_voice_dialog_red);
						}
						// finger move down
						else
						{
							// change text to "Move up to cancel"
							vDialog.changeVoiceDialogText(getResources().getString(
									R.string.slide_to_cancel));
							// change button background to "black"
							vDialog.changeCoiceDialogBg(R.drawable.round_button_voice_dialog);
						}
					}
					break;
				}
				return false;
			}
		});
	}
	
	public void setHandle(Handler handler)
	{
		this.vHandler = handler;
	}
	
	/**
	 * Dismiss VoiceDialog
	 */
	private void dismissVDialog()
	{
		if (vDialog != null)
		{
			vDialog.dismiss();
			vDialog = null;
		}
	}
	
	/**
	 * Upload voice or photo messages
	 * 
	 * @param filePath
	 * @param isReSend
	 * @param holder
	 * @param mb
	 * @param length
	 */
	private void sendObject(final String filePath, final boolean isReSend,
			final ErrorViewHolder holder, final MessageBean mb, final long length)
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				File dir = new File(filePath);
				if(dir.exists()){
					// TODO 由于服务器无法响应，临时注释
//					final String response = UploadHelper.uploadFile(dir, Const.UPLOAD_FILE_URL);
					final String response = "OK \"filename\"";
					Log.i("upload", "response="+response);
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// Upload successful
							if(response != null && response.contains("\"filename\"")){
								// Uploaded data is a voice file
								if(length != -1){
									// Not resend
									if(!isReSend){
										// TODO onCreate时需要得到ACTIVITY_ID
										MessageBean mb = new MessageBean(
												0, ACTIVITY_ID, HEADPHOTO_URL_ME, null, null,
												filePath, length,
												MessageBean.MESSAGE_VOICE_SEND,
												System.currentTimeMillis(),
												false);
										clv.addMessage(mb);
									}
								}else{
									// Uploaded data is a image file
									if(!isReSend){
										MessageBean mb = new MessageBean(0, ACTIVITY_ID, HEADPHOTO_URL_ME,
												null, filePath, null, 0,
												MessageBean.MESSAGE_IMG_SEND,
												System.currentTimeMillis(), false);
										clv.addMessage(mb);
									}
								}
								
								if(holder != null){
									holder.error.setVisibility(View.GONE);
								}
								
								if(mb!= null){
									mb.setError(false);
								}
								
							}else{
								if(length != -1){
									if(!isReSend){
										MessageBean mb = new MessageBean(
												0, ACTIVITY_ID, HEADPHOTO_URL_ME, null, null,
												filePath, length,
												MessageBean.MESSAGE_VOICE_SEND,
												System.currentTimeMillis(),
												true);
										clv.addMessage(mb);
									}
								}else{
									if(!isReSend){
										MessageBean mb = new MessageBean(0, ACTIVITY_ID, HEADPHOTO_URL_ME,
												null, filePath, null, 0,
												MessageBean.MESSAGE_IMG_SEND,
												System.currentTimeMillis(), true);
										clv.addMessage(mb);
									}
								}
							}
						}
					});
				}else{
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Util.showShortToast(MessageActivity.this, getResString(R.string.error_file_not_exist));
						}
					});
				}
			}
		}).start();
	}
	
	/**
	 * Get the resource string
	 * @param id
	 * @return
	 */
	private String getResString(int id) {
		return getResources().getString(id);
	}
}
