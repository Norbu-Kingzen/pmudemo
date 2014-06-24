/**
 * 
 */
package com.example.pmudemo.view;

import com.example.pmudemo.R;
import com.example.pmudemo.activity.MessageActivity;
import com.example.pmudemo.util.Const;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author weishijie
 *
 */
// with this tag, inner class Handler will show warning as follow :
// This Handler class should be static or leaks might occur
@SuppressLint("HandlerLeak")
public class VoiceDialog extends AlertDialog {

	/**
	 * voice volume image
	 */
	private ImageView img;
	/**
	 * context
	 */
	private Context context;
	/**
	 * slide to cancel textview (not used yet)
	 */
	private Button tv;
	
	/**
	 * @param context
	 */
	public VoiceDialog(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * @param context
	 * @param theme
	 */
	public VoiceDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	/**
	 * @param context
	 * @param cancelable
	 * @param cancelListener
	 */
	public VoiceDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.voice_dialog);
		// The dialog won't be canceled when touched outside the window
		this.setCanceledOnTouchOutside(false);
		
		img = (ImageView)findViewById(R.id.voice_dialog_volum);
		tv = (Button)findViewById(R.id.voice_dialog_tv);
		
		((MessageActivity)context).setHandle(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Const.MSG_REC_VOLUME:
					if(msg.arg1 < 14){
						img.setImageResource(R.drawable.amp1);
					}else if(msg.arg1 >= 14 && msg.arg1 < 28){
						img.setImageResource(R.drawable.amp2);
					}else if(msg.arg1 >= 28 && msg.arg1 < 43){
						img.setImageResource(R.drawable.amp3);
					}else if(msg.arg1 >= 43 && msg.arg1 < 57){
						img.setImageResource(R.drawable.amp4);
					}else if(msg.arg1 >= 57 && msg.arg1 < 72){
						img.setImageResource(R.drawable.amp5);
					}else if(msg.arg1 >= 72 && msg.arg1 < 86){
						img.setImageResource(R.drawable.amp6);
					}else if(msg.arg1 >= 86 && msg.arg1 <= 100){
						img.setImageResource(R.drawable.amp7);
					}else{
						img.setImageResource(R.drawable.amp7);
					}
					break;
				default : 
					break;
				}
				super.handleMessage(msg);
			}
		});
	}
	
	/**
	 * Change the text of voice dialog
	 * @param text
	 */
	public void changeVoiceDialogText(String text){
		tv.setText(text);
	}
	
	/**
	 * Change the background of the button in voice dialog
	 * @param resId resource id
	 */
	public void changeCoiceDialogBg(int resId) {
		tv.setBackgroundResource(resId);
	}

}
