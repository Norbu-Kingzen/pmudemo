package com.example.pmudemo.util;

import android.graphics.Bitmap;

public class BitmapCacheBean {
	private Bitmap bitmap;
	private int count;

	public BitmapCacheBean(Bitmap bitmap, int count)
	{
		this.bitmap = bitmap;
		this.count = count;
	}

	public Bitmap getBitmap()
	{
		return bitmap;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public void setBitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}

}