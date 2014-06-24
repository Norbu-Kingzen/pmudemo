package com.example.pmudemo.view;

import android.view.View;
import android.widget.ImageView;

/**
 * ErrorViewHolder class
 * Used when send message error
 * 
 * @author weishijie
 *
 */
public class ErrorViewHolder {

	public ImageView error;

	public void removeError()
	{
		error.setVisibility(View.GONE);
	}
}
