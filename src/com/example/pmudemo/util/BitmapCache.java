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

	/**
	 * 1级缓存（内存中）
	 */
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

	/**
	 * Put a bitmap into L1 cache(as BitmapCacheBean)
	 * 
	 * @param key
	 * @param bitmap
	 */
	public void put(String key, Bitmap bitmap)
	{
		if (!mBitmaps.containsKey(key))
		{
			mBitmaps.put(key, new BitmapCacheBean(bitmap, 0));
		}
	}

	/**
	 * Whether specified resources exists in cache (include L1 cache and L2 cache)
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key)
	{
		return mBitmaps.containsKey(key) || l2Cache.containsKey(key);
	}
	
	/**
	 * Whether specified resources exists in cache (include L1 cache and L2 cache)
	 * 
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
		// L1 cache
		if (mBitmaps.containsKey(key))
		{
			BitmapCacheBean bcb = mBitmaps.get(key);
			if (bcb != null)
			{
				// TODO ask
				bcb.setCount(bcb.getCount() + 1);
				ret = bcb.getBitmap();
			}
		}
		// L2 cache
		else
		{
			Bitmap b = l2Cache.get(key);
			if (b != null)
			{
				// Put into L1 cache(as BitmapCacheBean)
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

	/**
	 * Convert bitmap map to bitmap list
	 * 
	 * @return List<Entry<String, BitmapCacheBean>>
	 */
	public List<Entry<String, BitmapCacheBean>> toList()
	{
		Set<Entry<String, BitmapCacheBean>> set = mBitmaps.entrySet();
		List<Entry<String, BitmapCacheBean>> list = new ArrayList<Entry<String, BitmapCacheBean>>(
				set);
		return list;
	}

	/**
	 * Remove last half of bitmaps in the L1 cache(memory)
	 */
	private void removeLastByLRU()
	{
		// Convert to list
		Set<Entry<String, BitmapCacheBean>> set = mBitmaps.entrySet();
		List<Entry<String, BitmapCacheBean>> list = new ArrayList<Entry<String, BitmapCacheBean>>(
				set);
		// Sort
		Collections.sort(list, new CountComparator());
		// Remove last half
		for (int i = 0; i < set.size() / 2; i++)
		{
			Entry<String, BitmapCacheBean> e = list.get(i);
			set.remove(e);
			freeEntry(e);
		}
	}

	/**
	 * Delete one third of the files stored in L2 cache(SDCard)
	 */
	public void removeSDCardCacheByLRU()
	{
		l2Cache.removeLastByLRU();
	}

	/**
	 * Free memory
	 */
	public void freeMemory()
	{
		// Restore into L2 cache
		cache2L2();
		// Remove last half of bitmaps in the L1 cache(memory)
		removeLastByLRU();
	}

	/**
	 * Restore bitmaps stored in L1 cache to L2 cache
	 */
	private void cache2L2()
	{
		l2Cache.cache(this);
	}

	/**
	 * Recycle bitmaps in L1 cache(memory)
	 */
	@SuppressWarnings({ "rawtypes" })
	public void recycleBitmaps()
	{
		// Restore bitmaps stored in L1 cache to L2 cache
		cache2L2();
		Iterator<Entry<String, BitmapCacheBean>> itr = mBitmaps.entrySet()
				.iterator();
		while (itr.hasNext())
		{
			Map.Entry e = (Map.Entry) itr.next();
			// Free entry
			freeEntry(e);
		}
		mBitmaps.clear();
	}

	/**
	 * Free entry
	 * @param e
	 */
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
