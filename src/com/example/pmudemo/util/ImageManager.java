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

	public Bitmap getBitmap(int resource)
	{
		if (!mBitmaps.containsKey(resource))
		{
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			InputStream is = context.getResources().openRawResource(resource);
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
			mBitmaps.put(resource, bitmap);
		}
		return mBitmaps.get(resource);
	}

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