package com.example.pmudemo.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.graphics.Bitmap;

public class BitmapCache {

	private Map<String, BitmapCacheBean> mBitmaps;
	/**
	 * 2级缓存（SD卡中）
	 */
	private BitmapCacheSD l2Cache = new BitmapCacheSD();

	public BitmapCache()
	{
		mBitmaps = Collections
				.synchronizedMap(new HashMap<String, BitmapCacheBean>());
	}

	public void put(String key, Bitmap bitmap)
	{
		if (!mBitmaps.containsKey(key))
		{
			mBitmaps.put(key, new BitmapCacheBean(bitmap, 0));
		}
	}

	public boolean containsKey(String key)
	{
		return mBitmaps.containsKey(key) || l2Cache.containsKey(key);
	}
	
	/**
	 * If BitmapCache contains specified resources
	 * @param key
	 * @return
	 */
	public boolean containsKey(int key)
	{
		return mBitmaps.containsKey(key) || l2Cache.containsKey(key);
	}

	public Bitmap get(String key)
	{
		Bitmap ret = null;
		if (mBitmaps.containsKey(key))
		{
			BitmapCacheBean bcb = mBitmaps.get(key);
			if (bcb != null)
			{
				bcb.setCount(bcb.getCount() + 1);
				ret = bcb.getBitmap();
			}
		}
		else
		{
			Bitmap b = l2Cache.get(key);
			if (b != null)
			{
				put(key, b);
				ret = b;
			}
		}
		return ret;
	}

	public int getSize()
	{
		return mBitmaps.entrySet().size();
	}

	public List<Entry<String, BitmapCacheBean>> toList()
	{
		Set<Entry<String, BitmapCacheBean>> set = mBitmaps.entrySet();
		List<Entry<String, BitmapCacheBean>> list = new ArrayList<Entry<String, BitmapCacheBean>>(
				set);
		return list;
	}

	private void removeLastByLRU()
	{
		Set<Entry<String, BitmapCacheBean>> set = mBitmaps.entrySet();
		List<Entry<String, BitmapCacheBean>> list = new ArrayList<Entry<String, BitmapCacheBean>>(
				set);
		Collections.sort(list, new CountComparator());
		for (int i = 0; i < set.size() / 2; i++)
		{
			Entry<String, BitmapCacheBean> e = list.get(i);
			set.remove(e);
			freeEntry(e);
		}
	}

	public void removeSDCardCacheByLRU()
	{
		l2Cache.removeLastByLRU();
	}

	public void freeMemory()
	{
		cache2L2();
		removeLastByLRU();
	}

	private void cache2L2()
	{
		l2Cache.cache(this);
	}

	@SuppressWarnings({ "rawtypes" })
	public void recycleBitmaps()
	{
		cache2L2();
		Iterator<Entry<String, BitmapCacheBean>> itr = mBitmaps.entrySet()
				.iterator();
		while (itr.hasNext())
		{
			Map.Entry e = (Map.Entry) itr.next();
			freeEntry(e);
		}
		mBitmaps.clear();
	}

	@SuppressWarnings("rawtypes")
	private void freeEntry(Map.Entry e)
	{
		if (e != null)
		{
			BitmapCacheBean bcb = (BitmapCacheBean) e.getValue();
			Bitmap b = bcb.getBitmap();
			if (b != null && !b.isRecycled())
			{
				b.recycle();
				b = null;
				bcb = null;
				e = null;
			}
		}
	}

	private class CountComparator implements
			Comparator<Entry<String, BitmapCacheBean>> {

		@Override
		public int compare(Entry<String, BitmapCacheBean> lhs,
				Entry<String, BitmapCacheBean> rhs)
		{
			return lhs.getValue().getCount() - rhs.getValue().getCount();
		}

	}
}
