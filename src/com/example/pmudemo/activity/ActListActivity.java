package com.example.pmudemo.activity;

import java.util.ArrayList;

import com.example.pmudemo.R;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActListActivity extends ListActivity {

	private String[] mListTitle = { "Activity1", "Activity2", "Activity3", "Activity4"};
	private String[] mListTime = { "2014/06/13 09:34:33", "2014/06/13 11:34:33", "2014/06/13 17:34:33", "2014/06/12 09:34:33"};
	private ListView mListView = null;  
	private ActListAdapter myAdapter = null;
	private ArrayList arrayList = null;
    
	public class ActListAdapter extends ArrayAdapter<Object> {

		private int mTextViewResourceID = 0;  
	    private Context mContext;
	    private int[] colors = new int[] { 0xff626569, 0xff4f5257 };
	    
		public ActListAdapter(Context context, int resource) {
			super(context, resource);
			mTextViewResourceID = resource;  
			mContext = context;
		}
		@Override
		public int getCount() {  
	        return mListTitle.length;  
	    }
		
		@Override  
	    public boolean areAllItemsEnabled() {  
	        return false;  
	    }
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
	        return position;  
	    }
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return super.getItemId(position);
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			TextView arrayTitle = null;
			TextView arrayTime = null;
			Button arrayAddinBtn = null;
			
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(  
						mTextViewResourceID, null);  
				arrayTitle = (TextView)convertView.findViewById(R.id.array_title);
				arrayTime = (TextView)convertView.findViewById(R.id.array_time);
				arrayAddinBtn = (Button)convertView.findViewById(R.id.array_addinBtn);
				arrayAddinBtn.setOnClickListener(new View.OnClickListener() {
		            @Override  
		            public void onClick(View arg0) {  
		            Toast.makeText(ActListActivity.this,"click activity "+position, Toast.LENGTH_SHORT).show();  
		            }
		        });  
			}
			// set background color
			int colorPos = position % colors.length;  
	        convertView.setBackgroundColor(colors[colorPos]);
	        
	        arrayTitle.setText(mListTitle[position]);  
	        arrayTime.setText(mListTime[position]);  
	        return convertView;
//			return super.getView(position, convertView, parent);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		arrayList = this;
		mListView = getListView();
		myAdapter = new ActListAdapter(this,R.layout.activity_act_list);
		setListAdapter(myAdapter);
	}
}
