package com.example.pmudemo.adapter;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import com.example.pmudemo.R;
import com.example.pmudemo.bean.MessageBean;
import com.example.pmudemo.db.MsgDao;
import com.example.pmudemo.util.ImageManager;
import com.example.pmudemo.view.ChatListView.OnErrorClickListener;
import com.example.pmudemo.view.ErrorViewHolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author weishijie
 *
 */
public class LiveChatAdapter extends BaseAdapter {
	
	private List<MessageBean> ml;
	private MsgDao md;
	private LayoutInflater inflater;
	private MediaPlayer mp;
	private AnimationDrawable lastAnim;
	private VoiceViewHolder lastVH;
	private boolean lastIsLeft;
	private int nowPlayingPosition = -1;
	private ImageManager im;
	private OnErrorClickListener onErrorClickListener;
	private ImageView iv;

	public LiveChatAdapter(Context context,
			OnErrorClickListener onErrorClickListener)
	{
		this.onErrorClickListener = onErrorClickListener;
		md = new MsgDao(context);
		im = new ImageManager(context);
		// TODO 通过SharedPreferences得到ACTIVITY_ID或者，由上层调用传参数过来
		ml = md.selectLast500(0);
		for (MessageBean m : ml)
		{
//			final String f = m.getFaceUrl();
			final int f = m.getFaceUrl();
			if (f > 0)
			{
				new Thread(new Runnable() {
					@Override
					public void run()
					{
						im.getBitmap(f);
					}
				}).start();
				break;
			}
		}
		inflater = LayoutInflater.from(context);
	}
	
	public int addMsg(MessageBean mb)
	{
		ml.add(mb);
		int id = md.insert(mb);
		this.notifyDataSetChanged();
		return id;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return ml.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getItemViewType(int position)
	{
		MessageBean mb = ml.get(position);
		if (mb != null)
		{
			return mb.getType();
		}
		else
		{
			return 0;
		}
	}
	
	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@Override
	public int getViewTypeCount() {
		return 7;
	}

	static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			ImageBean ib = (ImageBean) msg.obj;
			ib.iv.setImageBitmap(ib.b);
		}
	};
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ImgViewHolder irh = null;
		ImgViewHolder ish = null;
		VoiceViewHolder vrh = null;
		VoiceViewHolder vsh = null;
		// VoiceViewHolder vh = null;
		int type = getItemViewType(position);

		// The old view can't be reuse
		if (convertView == null)
		{
			switch (type)
			{
			case MessageBean.MESSAGE_IMG_RECEIVE:
				convertView = inflater.inflate(R.layout.message_img_receive,
						parent, false);
				irh = new ImgViewHolder();
				irh.img = (ImageView) convertView
						.findViewById(R.id.img_receive_img);
				irh.pic = (ImageView) convertView
						.findViewById(R.id.img_receive_pic);
				irh.time = (TextView) convertView
						.findViewById(R.id.img_receive_time);
				convertView.setTag(irh);
				break;
			case MessageBean.MESSAGE_IMG_SEND:
				convertView = inflater.inflate(R.layout.message_img_send,
						parent, false);
				ish = new ImgViewHolder();
				ish.img = (ImageView) convertView
						.findViewById(R.id.img_send_img);
				ish.pic = (ImageView) convertView
						.findViewById(R.id.img_send_pic);
				ish.time = (TextView) convertView
						.findViewById(R.id.img_send_time);
				ish.error = (ImageView) convertView
						.findViewById(R.id.img_send_error);
				convertView.setTag(ish);
				break;
			case MessageBean.MESSAGE_VOICE_RECEIVE:
				convertView = inflater.inflate(R.layout.message_voice_receive,
						parent, false);
				vrh = new VoiceViewHolder();
				vrh.img = (ImageView) convertView
						.findViewById(R.id.voice_receive_img);
				vrh.len = (TextView) convertView
						.findViewById(R.id.voice_receive_length);
				vrh.time = (TextView) convertView
						.findViewById(R.id.voice_receive_time);
				vrh.content = convertView
						.findViewById(R.id.voice_receive_content);
				vrh.anim = (ImageView) convertView
						.findViewById(R.id.voice_receive_anim);
				convertView.setTag(vrh);
				break;
			case MessageBean.MESSAGE_VOICE_SEND:
				convertView = inflater.inflate(R.layout.message_voice_send,
						parent, false);
				vsh = new VoiceViewHolder();
				vsh.img = (ImageView) convertView
						.findViewById(R.id.voice_send_img);
				vsh.len = (TextView) convertView
						.findViewById(R.id.voice_send_length);
				vsh.time = (TextView) convertView
						.findViewById(R.id.voice_send_time);
				vsh.content = convertView.findViewById(R.id.voice_send_content);
				vsh.anim = (ImageView) convertView
						.findViewById(R.id.voice_send_anim);
				vsh.error = (ImageView) convertView
						.findViewById(R.id.voice_send_error);
				convertView.setTag(vsh);
				break;
			}
		}
		else
		{
			switch (type)
			{
			case MessageBean.MESSAGE_IMG_RECEIVE:
				irh = (ImgViewHolder) convertView.getTag();
				break;
			case MessageBean.MESSAGE_IMG_SEND:
				ish = (ImgViewHolder) convertView.getTag();
				break;
			case MessageBean.MESSAGE_VOICE_RECEIVE:
				vrh = (VoiceViewHolder) convertView.getTag();
				break;
			case MessageBean.MESSAGE_VOICE_SEND:
				vsh = (VoiceViewHolder) convertView.getTag();
				break;
			}
		}

		// Format display time
		final MessageBean mb = ml.get(position);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(mb.getTime());
		StringBuffer t = new StringBuffer();
		t.append(cal.get(Calendar.MONTH) + 1).append("/")
				.append(cal.get(Calendar.DAY_OF_MONTH)).append("/")
				.append(cal.get(Calendar.YEAR)).append(" ")
				.append(cal.get(Calendar.HOUR_OF_DAY)).append(":");
		int min = cal.get(Calendar.MINUTE);
		if (min > 9)
		{
			t.append(min);
		}
		else
		{
			t.append(0).append(min);
		}
		switch (type)
		{
		case MessageBean.MESSAGE_IMG_RECEIVE:
			irh.pic.setImageBitmap(im.getBitmapFromSd(mb.getImgUrl()));
			irh.time.setText(t.toString());
			break;
		case MessageBean.MESSAGE_IMG_SEND:
			if (!im.containsBitmap(mb.getFaceUrl()))
			{
				iv = ish.img;
				new Thread(new Runnable() {
					@Override
					public void run()
					{
						Message m = handler.obtainMessage();
//						try
//						{
							ImageBean ib = new ImageBean();
							ib.b = im.getBitmap(mb.getFaceUrl());
							ib.iv = iv;
							m.obj = ib;
							m.sendToTarget();
//						} catch (BitmapLoadException e)
//						{
//							e.printStackTrace();
//						}
					}
				}).start();
			}
			else
			{
//				try
//				{
					// TODO check here
//					ish.img.setImageBitmap(im.getBitmap(mb.getFaceUrl()));
					ish.img.setImageResource(mb.getFaceUrl());
//				} catch (BitmapLoadException e)
//				{
//					e.printStackTrace();
//				}
			}
			ish.pic.setImageBitmap(im.getBitmapFromSd(mb.getImgUrl()));
			ish.time.setText(t.toString());
			if (mb.isError())
			{
				ish.error.setVisibility(View.VISIBLE);
				if (onErrorClickListener != null)
				{
					ish.error.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v)
						{
							onErrorClickListener.onErrorClick(
									(ErrorViewHolder) v.getTag(), mb);
						}
					});
				}
			}
			else
			{
				ish.error.setVisibility(View.GONE);
			}
			break;
		case MessageBean.MESSAGE_VOICE_RECEIVE:
			vrh.len.setText(String.valueOf(mb.getVoiceLength()) + "\"");
			if (nowPlayingPosition == position)
			{
				animStart(vrh, true);
			}
			else
			{
				vrh.anim.setBackgroundResource(R.drawable.chatfrom_voice_playing);
			}
			final VoiceViewHolder tvrh = vrh;
			vrh.content.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v)
				{
					playVoice(mb.getVoiceUrl(), tvrh, true, position);
				}
			});
			break;
		case MessageBean.MESSAGE_VOICE_SEND:
			if (!im.containsBitmap(mb.getFaceUrl()))
			{
				iv = vsh.img;
				new Thread(new Runnable() {
					@Override
					public void run()
					{
						Message m = handler.obtainMessage();
//						try
//						{
							ImageBean ib = new ImageBean();
							ib.b = im.getBitmap(mb.getFaceUrl());
							ib.iv = iv;
							m.obj = ib;
							m.sendToTarget();
//						} catch (BitmapLoadException e)
//						{
//							e.printStackTrace();
//						}
					}
				}).start();
			}
			else
			{
//				try
//				{
					vsh.img.setImageBitmap(im.getBitmap(mb.getFaceUrl()));
//				} catch (BitmapLoadException e)
//				{
//					e.printStackTrace();
//				}
			}
			vsh.len.setText(String.valueOf(mb.getVoiceLength()) + "\"");
			if (mb.isError())
			{
				vsh.error.setVisibility(View.VISIBLE);
				if (onErrorClickListener != null)
				{
					vsh.error.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v)
						{
							onErrorClickListener.onErrorClick(
									(ErrorViewHolder) v.getTag(), mb);
						}
					});
				}
			}
			else
			{
				vsh.error.setVisibility(View.GONE);
			}
			if (nowPlayingPosition == position)
			{
				animStart(vsh, false);
			}
			else
			{
				vsh.anim.setBackgroundResource(R.drawable.chatto_voice_playing);
			}
			final VoiceViewHolder tvsh = vsh;
			vsh.content.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v)
				{
					playVoice(mb.getVoiceUrl(), tvsh, false, position);
				}
			});
			break;
		}
		return convertView;
	}
	
	private void playVoice(String src, final VoiceViewHolder vh,
			final boolean isLeft, int position)
	{
		if (mp == null)
		{
			mp = new MediaPlayer();
		}
		if (mp.isPlaying())
		{
			mp.stop();
			mp.release();
			mp = null;
			nowPlayingPosition = -1;
			mp = new MediaPlayer();
			if (lastAnim != null && lastAnim.isRunning())
			{
				animStop(lastAnim, lastVH, lastIsLeft);
			}
		}
		try
		{
			mp.setDataSource(src);
			mp.prepare();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (IllegalStateException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		mp.start();
		nowPlayingPosition = position;

		final AnimationDrawable animationDrawable = animStart(vh, isLeft);
		mp.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp)
			{
				mp.release();
				mp = null;
				animStop(animationDrawable, vh, isLeft);
				nowPlayingPosition = -1;
			}
		});
		lastAnim = animationDrawable;
		lastVH = vh;
		lastIsLeft = isLeft;
	}
	
	public void onPause()
	{
		if (mp != null)
		{
			mp.release();
			mp = null;
			nowPlayingPosition = -1;
			if (lastAnim != null && lastAnim.isRunning())
			{
				animStop(lastAnim, lastVH, lastIsLeft);
			}
		}
		im.recycleBitmaps();
	}
	
	private void animStop(AnimationDrawable animationDrawable,
			VoiceViewHolder vh, final boolean isLeft)
	{
		animationDrawable.stop();
		if (isLeft)
		{
			vh.anim.setBackgroundResource(R.drawable.chatfrom_voice_playing);
		}
		else
		{
			vh.anim.setBackgroundResource(R.drawable.chatto_voice_playing);
		}
	}

	private AnimationDrawable animStart(VoiceViewHolder vh, final boolean isLeft)
	{
		if (isLeft)
		{
			vh.anim.setBackgroundResource(R.anim.rec_playing);
		}
		else
		{
			vh.anim.setBackgroundResource(R.anim.snd_playing);
		}
		AnimationDrawable animationDrawable = (AnimationDrawable) vh.anim
				.getBackground();
		animationDrawable.start();
		return animationDrawable;
	}

	public int updateMessage(MessageBean mb)
	{
		ml.remove(mb);
		mb.setTime(System.currentTimeMillis());
		mb.setError(false);
		ml.add(mb);
		this.notifyDataSetChanged();
		return md.update(mb);
	}

	public boolean modifyMessageStateError(MessageBean mb)
	{
		int index = ml.indexOf(mb);
		if (index > 0)
		{
			MessageBean m = ml.get(index);
			m.setError(true);
			md.update(m);
			this.notifyDataSetChanged();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean modifyMessageStateNormal(MessageBean mb)
	{
		int index = ml.indexOf(mb);
		if (index > 0)
		{
			MessageBean m = ml.get(index);
			m.setError(false);
			md.update(m);
			this.notifyDataSetChanged();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public class ImgViewHolder extends ErrorViewHolder {
		/**
		 * Image
		 */
		ImageView pic;
		/**
		 * Head phote
		 */
		ImageView img;
		/**
		 * Sent time
		 */
		TextView time;
	}

	public class VoiceViewHolder extends ErrorViewHolder {
		/**
		 * Play time
		 */
		TextView len;
		/**
		 * Head phote
		 */
		ImageView img;
		/**
		 * Sent time
		 */
		TextView time;
		/**
		 * Voice content
		 */
		View content;
		/**
		 * animation
		 */
		ImageView anim;
	}

	class ImageBean {
		ImageView iv;
		Bitmap b;
	}
}
