package com.example.pmudemo.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageManager {
	private Map<Object, Bitmap> mBitmaps;
	private BitmapCache cache;
	private Context context;

	// private BitmapCacheSD l2Cache;

	public ImageManager(Context context)
	{
		this.context = context;
		mBitmaps = Collections.synchronizedMap(new HashMap<Object, Bitmap>());
	}

	/**
	 * Judge if the specified bitmap exists in cache, if so, then get bitmap from cache,
	 * else, store it into cache and then return it
	 * 
	 * @param resource resource
	 * @return Bitmap
	 */
	public Bitmap getBitmap(int resource)
	{
		if (!mBitmaps.containsKey(resource))
		{
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			// 是否可清除
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			// Open a data stream for reading a raw resource.
			// it can only be used to open drawable, sound, and raw resources.
			InputStream is = context.getResources().openRawResource(resource);
			// Decode an input stream into a bitmap.
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
			mBitmaps.put(resource, bitmap);
		}
		return mBitmaps.get(resource);
	}

	/**
	 * Judge if the specified bitmap exists in cache, if exist, then get bitmap from cache,
	 * else, store it into cache and then return it
	 * 
	 * @param path Image path
	 * @return Bitmap
	 */
	public Bitmap getBitmapFromSd(String path)
	{
		if (!mBitmaps.containsKey(path))
		{
			Bitmap bitmap = BitmapCacheSD.getBitmapFromSD(path);
			if (bitmap != null)
			{
				mBitmaps.put(path, bitmap);
			}
		}
		return mBitmaps.get(path);
	}

	/**
	 * Judge if the specified net bitmap exists in cache, if exist, then get bitmap from cache,
	 * else, download from net and store it into cache and then return it
	 * 
	 * @param url
	 * @return
	 * @throws BitmapLoadException
	 */
	public Bitmap getBitmap(String url) throws BitmapLoadException
	{
		if (url == null)
		{
			return null;
		}
		if (cache == null)
		{
			cache = getCache();
		}
		if (!cache.containsKey(url))
		{
			Bitmap bitmap = null;
			try
			{
				// Get bitmap input stream from net
				URL myFileUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inPreferredConfig = Bitmap.Config.RGB_565;
				opt.inPurgeable = true;
				opt.inInputShareable = true;
				InputStream is = conn.getInputStream();
				// Decode an input stream into a bitmap
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
			} catch (MalformedURLException e)
			{
				throw new BitmapLoadException(e.getMessage(),
						"MalformedURLException");
			} catch (IOException e)
			{
				throw new BitmapLoadException(e.getMessage(), "IOException");
			}
			if (bitmap != null)
			{
				cache.put(url, bitmap);
			}
		}
		return cache.get(url);
	}

	/**
	 * Judge if the net bitmap exists in the cache
	 * 
	 * @param url
	 * @return
	 */
	public boolean containsBitmap(String url)
	{
		if (cache == null)
		{
			return false;
		}
		else
		{
			return cache.containsKey(url);
		}
	}
	
	/**
	 * Judge if the specified bitmap resource exists in the cache
	 * 
	 * @param resId
	 * @return
	 */
	public boolean containsBitmap(int resId)
	{
		if (cache == null)
		{
			return false;
		}
		else
		{
			return cache.containsKey(resId);
		}
	}

	// TODO ask
	@SuppressWarnings({ "rawtypes" })
	public void recycleBitmaps()
	{
		Iterator<Entry<Object, Bitmap>> itr = mBitmaps.entrySet().iterator();
		while (itr.hasNext())
		{
			Map.Entry e = (Map.Entry) itr.next();
			if (e != null)
			{
				Bitmap b = (Bitmap) e.getValue();
				if (b != null && !b.isRecycled())
				{
					b.recycle();
					b = null;
				}
			}
		}
		mBitmaps.clear();
		if (cache != null)
		{
			cache.recycleBitmaps();
		}
	}

	/**
	 * Free memory
	 */
	public void freeMem()
	{
		if (cache != null)
		{
			cache.freeMemory();
		}
	}

	private BitmapCache getCache()
	{
		if (cache == null)
		{
			cache = new BitmapCache();
		}
		return cache;
	}
}