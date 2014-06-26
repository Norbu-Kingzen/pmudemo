package com.example.pmudemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pmudemo.R;

public class ActsListActivity extends Activity {

	private ListView lv;
    /*定义一个动态数组*/
	List<HashMap<String, Object>> listItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acts_list);
		
		// 将数据存入listItem中
		setData(new ArrayList<HashMap<String, Object>>());
		
		lv = (ListView) findViewById(R.id.actLst);
		//得到一个ActListAdapter对象
		ActListAdapter mAdapter = new ActListAdapter(this);
		//为ListView绑定Adapter
		lv.setAdapter(mAdapter);
		/*为ListView添加自己的点击事件*/
		lv.setOnItemClickListener(new ListViewOnItemClickLsnr());
		
	}
	
	/**
	 * 将后台数据存入listItem
	 * @param actLists
	 */
	private void setData(List<HashMap<String, Object>> actLists) {
		listItem = new ArrayList<HashMap<String, Object>>();
		for(int i=0;i<30;i++)  {  
	    	HashMap<String, Object> map = new HashMap<String, Object>();  
	    	map.put("ItemTitle", "Activity " + i);  
	    	map.put("ItemText", "This is activity " + i );  
	    	listItem.add(map);
	         } 
	}
	/*添加一个得到数据的方法，方便使用*/ 
	private List<HashMap<String, Object>> getData(){
//	    ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
	        return listItem;
	    }
	
	/**
	 * List View onClick listener
	 * This is used for list click event(notice that it's not the button click event)
	 * @author weishijie
	 *
	 */
	class ListViewOnItemClickLsnr implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Toast.makeText(ActsListActivity.this,"click list item "+position, Toast.LENGTH_SHORT).show();  
		}
	}
	
	/**
	 *  新建一个类继承BaseAdapter，实现视图与数据的绑定
     */
	private class ActListAdapter extends BaseAdapter {

		//得到一个LayoutInfalter对象用来导入布局
		private LayoutInflater mInflater;
		
		public ActListAdapter(Context context) {
		    this.mInflater = LayoutInflater.from(context);
		}
		
		/**
		 * 返回数组的长度
		 */
		@Override
		public int getCount() {
			return getData().size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			Log.v("ActsListActivity", "getView " + position + " " + convertView);
			
			// list项目多于1个屏幕时后续item不会被一次性加载，当向上滑动屏幕时就会进入if分支
			if (convertView == null) {
				holder=new ViewHolder();
				// 加载listItem的布局文件
				convertView = mInflater.inflate(R.layout.activity_acts_list_items, null);
				
				holder.arrayTitle = (TextView)convertView.findViewById(R.id.array_title);
				holder.arrayTime = (TextView)convertView.findViewById(R.id.array_time);
				holder.arrayAddinBtn = (Button)convertView.findViewById(R.id.array_addinBtn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			/*设置TextView显示的内容，即我们存放在动态数组中的数据*/
			holder.arrayTitle.setText(getData().get(position).get("ItemTitle").toString());
			holder.arrayTime.setText(getData().get(position).get("ItemText").toString());
			holder.arrayAddinBtn.setOnClickListener(new View.OnClickListener() {
	            @Override  
	            public void onClick(View arg0) {  
	            Toast.makeText(ActsListActivity.this,"click activity "+position, Toast.LENGTH_SHORT).show();  
	            }
	        });
			return convertView;
		}
	}
	/*存放控件*/ 
	public final class ViewHolder{
	    public TextView arrayTitle;
	    public TextView arrayTime;
	    public Button arrayAddinBtn;
	    }
}
