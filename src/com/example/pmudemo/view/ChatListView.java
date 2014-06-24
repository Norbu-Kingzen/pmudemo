package com.example.pmudemo.view;

import com.example.pmudemo.R;
import com.example.pmudemo.adapter.LiveChatAdapter;
import com.example.pmudemo.bean.MessageBean;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * @author weishijie
 *
 */
public class ChatListView extends LinearLayout {

	private LiveChatAdapter adapter;
	private ListView lv;
	
	public ChatListView(Context context,
			OnErrorClickListener onErrorClickListener)
	{
		super(context);
		init(context, onErrorClickListener);
	}
	
	public ChatListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, null);
	}
	
	private void init(Context context, OnErrorClickListener onErrorClickListener)
	{
		LayoutInflater.from(context)
				.inflate(R.layout.chat_listview, this, true);
		lv = (ListView) findViewById(R.id.chatlistview);
		adapter = new LiveChatAdapter(context, onErrorClickListener);
		lv.setAdapter(adapter);
		lv.post(new Runnable() {
			@Override
			public void run()
			{
				goBottom();
			}
		});
	}
	
	public int addMessage(MessageBean mb)
	{
		int id = adapter.addMsg(mb);
		goBottom();
		return id;
	}

	
	/**
	 * Make chat listview show the latest message(at the bottom)
	 */
	public void goBottom()
	{
		lv.setSelection(adapter.getCount());
	}

	public int updateMessage(MessageBean mb)
	{
		return adapter.updateMessage(mb);
	}

	public boolean modifyMessageStateError(MessageBean mb)
	{
		return adapter.modifyMessageStateError(mb);
	}

	public boolean modifyMessageStateNormal(MessageBean mb)
	{
		return adapter.modifyMessageStateNormal(mb);
	}

	public void onPause()
	{
		adapter.onPause();
	}
	
	public interface OnErrorClickListener {
		public void onErrorClick(ErrorViewHolder holder, MessageBean mb);
	}

}
