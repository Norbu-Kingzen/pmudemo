package com.example.pmudemo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class BitmapCacheSD {
	public static final String PICKMEUP_ROOT = "/pmudemo/";
	/**
	 * 2级缓存路径（SD卡中）
	 */
	public static final String L2_CACHE_PATH = "cache/L2/";
	public static final int CACHE_FOLDER_SIZE_LIMIT_IN_MB = 50;

	private final String SD_CACHE_PATH = Environment
			.getExternalStorageDirectory() + PICKMEUP_ROOT + L2_CACHE_PATH;
	private File dir;

	public BitmapCacheSD()
	{
		dir = new File(SD_CACHE_PATH);
		if (!dir.exists())
		{
			dir.mkdirs();
		}
	}

	/**
	 * Restore bitmaps stored in L1 cache to L2 cache (save as png)
	 * 
	 * @param l1Cache 1级缓存
	 */
	public void cache(BitmapCache l1Cache)
	{
		List<Entry<String, BitmapCacheBean>> list = l1Cache.toList();
		if (list != null && !list.isEmpty())
		{
			for (int i = 0; i < list.size(); i++)
			{
				Entry<String, BitmapCacheBean> e = list.get(i);
				String key = (String) e.getKey();
				String path = null;
				try
				{
					// Specify the path to local sd cache path
					path = dir.getAbsolutePath() + "/" + getMD5(key);
				} catch (NoSuchAlgorithmException e2)
				{
					Log.e("Util", "URL to MD5 error.");
					e2.printStackTrace();
				}
				if (path != null)
				{
					Bitmap b = e.getValue().getBitmap();
					try
					{
						// Save bitmap to sd cache path, as png
						saveBitmapAsPng(b, path, false);
					} catch (IOException e1)
					{
						Log.e("Utils", "L1Cache save error.");
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public boolean containsKey(Object key)
	{
		boolean ret = false;
		String[] fileName = dir.list();
		if (fileName != null)
		{
			try
			{
				String keyName = getMD5((String) key);
				for (int i = 0; i < fileName.length; i++)
				{
					if (fileName[i].equalsIgnoreCase(keyName))
					{
						ret = true;
						break;
					}
				}
			} catch (NoSuchAlgorithmException e)
			{
				Log.e("Utils", "URL to MD5 error.");
				e.printStackTrace();
				ret = false;
			}
		}
		return ret;
	}

	public Bitmap get(String key)
	{
		String path = null;
		try
		{
			path = dir.getAbsolutePath() + "/" + getMD5(key);
		} catch (NoSuchAlgorithmException e)
		{
			Log.e("Utils", "URL to MD5 error.");
			e.printStackTrace();
		}
		Bitmap bitmap = null;
		if (path != null)
		{
			bitmap = getBitmapFromSD(path);
		}
		return bitmap;
	}

	/**
	 * Delete one third of the files stored in L2 cache(SDCard)
	 */
	public void removeLastByLRU()
	{
		if (getTotalCacheSize() > CACHE_FOLDER_SIZE_LIMIT_IN_MB)
		{
			File[] files = dir.listFiles();
			List<File> fileList = Arrays.asList(files);
			// Sort by last modified time
			Collections.sort(fileList, new LastModifiedDesComparator());
			// Delete one third of the files
			for (int i = 0; i < fileList.size() / 3; i++)
			{
				fileList.get(i).delete();
			}
		}
	}

	/**
	 * Total size in MB.
	 * 
	 * @return
	 */
	private long getTotalCacheSize()
	{
		long totalSize = 0;
		File[] files = dir.listFiles();
		if (files != null)
		{
			for (int i = 0; i < files.length; i++)
			{
				totalSize += files[i].length();
			}
		}
		return totalSize / 1024 / 1024;
	}

	private class LastModifiedDesComparator implements Comparator<File> {
		@Override
		public int compare(File lhs, File rhs)
		{
			return (int) (lhs.lastModified() - rhs.lastModified());
		}
	}

	private String getMD5(String val) throws NoSuchAlgorithmException
	{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(val.getBytes());
		byte[] m = md5.digest();
		return getString(m);
	}

	private String getString(byte[] b)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
		{
			sb.append(b[i]);
		}
		return sb.toString();
	}

	/**
	 * Get a bitmap from sd card
	 * 
	 * @param path path
	 * @return Bitmap
	 */
	public static Bitmap getBitmapFromSD(String path)
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path, opt);
		return bitmap;
	}

	/**
	 * Save bitmaps as png to specified path
	 * 
	 * @param bitmap
	 * @param path
	 * @param isOverwrite
	 * @return
	 * @throws IOException
	 */
	private String saveBitmapAsPng(Bitmap bitmap, String path,
			boolean isOverwrite) throws IOException
	{
		String ret = null;
		if (bitmap != null)
		{
			File f = new File(path);
			if (isOverwrite)
			{
				f.createNewFile();
			}
			else
			{
				if (f.exists())
				{
					f.setLastModified(Calendar.getInstance().getTimeInMillis());
					return f.getAbsolutePath();
				}
				else
				{
					f.createNewFile();
				}
			}
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
			ret = f.getAbsolutePath();
		}
		return ret;
	}
}
