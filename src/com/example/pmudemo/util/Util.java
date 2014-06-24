/**
 * 
 */
package com.example.pmudemo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Utility class
 * @author weishijie
 *
 */
public class Util {
	
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static void showLongToast(Context context, String msg){
		Toast.makeText(
				context,
				msg,
				Toast.LENGTH_LONG).show();
	}
	
	public static void showShortToast(Context context, String msg){
		Toast.makeText(
				context,
				msg,
				Toast.LENGTH_SHORT).show();
	}
}
