package com.example.pmudemo;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

/**
 * @author weishijie
 *
 */
public class MyApplication extends Application {

	private Map<Object, Object> map;
	/**
	 * Constructor
	 */
	public MyApplication() {
		map = new HashMap<Object, Object>();
	}
	
	public void put(Object key, Object value) {
		map.put(key, value);
	}
	public Object get(Object key) {
		return map.get(key);
	}

}
