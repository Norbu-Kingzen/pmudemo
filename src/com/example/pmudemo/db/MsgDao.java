package com.example.pmudemo.db;

import java.util.ArrayList;
import java.util.List;

import com.example.pmudemo.bean.MessageBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author weishijie
 *
 */
public class MsgDao extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "pickmeup";
	private final static int DATABASE_VERSION = 1;

	private final static String TABLE_NAME = "PICKMEUP_MSG";
	private final static String ID = "ID";
	private final static String FACE_URL = "FACE_URL";
	private final static String MSG = "MSG";
	private final static String IMG_URL = "IMG_URL";
	private final static String VOICE_URL = "VOICE_URL";
	private final static String VOICE_LENGTH = "VOICE_LENGTH";
	private final static String TYPE = "TYPE";
	private final static String TIME = "TIME";
	private final static String ISERROR = "ISERROR";

	public MsgDao(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String sql = "Create table IF NOT EXISTS " + TABLE_NAME + "(" + ID
				+ " integer PRIMARY KEY," + FACE_URL + " text," + MSG
				+ " text," + IMG_URL + " text," + VOICE_URL + " text,"
				+ VOICE_LENGTH + " integer," + TYPE + " integer," + ISERROR
				+ " integer," + TIME + " integer);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	public List<MessageBean> selectLast500()
	{
		List<MessageBean> mbList = new ArrayList<MessageBean>();
		String[] columns = new String[] { ID, FACE_URL, MSG, IMG_URL,
				VOICE_URL, VOICE_LENGTH, TYPE, TIME, ISERROR };
		String whereClause = null;
		String[] whereArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = TIME + " ASC";
		String limit = null;
		Cursor c = this.getReadableDatabase().query(TABLE_NAME, columns,
				whereClause, whereArgs, groupBy, having, orderBy, limit);
		while (c.moveToNext())
		{
			int id = c.getInt(0);
			int faceUrl = c.getInt(1);
			String msg = c.getString(2);
			String imgUrl = c.getString(3);
			String voiceUrl = c.getString(4);
			long voiceLength = c.getLong(5);
			int type = c.getInt(6);
			long time = c.getLong(7);
			int isError = c.getInt(8);
			mbList.add(new MessageBean(id, faceUrl, msg, imgUrl, voiceUrl,
					voiceLength, type, time, isError == 0 ? false : true));
		}
		c.close();
		c = null;
		this.getReadableDatabase().close();
		return mbList;
	}

	public int insert(MessageBean mb)
	{
		if (mb == null)
		{
			throw new IllegalArgumentException("MessageBean");
		}
		ContentValues values = new ContentValues();
		values.put(FACE_URL, mb.getFaceUrl());
		values.put(MSG, mb.getMsg());
		values.put(IMG_URL, mb.getImgUrl());
		values.put(VOICE_URL, mb.getVoiceUrl());
		values.put(VOICE_LENGTH, mb.getVoiceLength());
		values.put(TYPE, mb.getType());
		values.put(TIME, mb.getTime());
		values.put(ISERROR, mb.isError() ? 1 : 0);
		this.getWritableDatabase().insert(TABLE_NAME, "", values);
		final String MY_QUERY = "SELECT last_insert_rowid() FROM " + TABLE_NAME;
		Cursor cur = this.getWritableDatabase().rawQuery(MY_QUERY, null);
		cur.moveToFirst();
		int id = cur.getInt(0);
		cur.close();
		this.getWritableDatabase().close();
		return id;
	}

	public int update(MessageBean mb)
	{
		if (mb == null)
		{
			throw new IllegalArgumentException("MessageBean");
		}
		String whereClause = ID + "=?";
		String[] whereArgs = new String[] { String.valueOf(mb.getId()) };
		ContentValues values = new ContentValues();
		values.put(FACE_URL, mb.getFaceUrl());
		values.put(MSG, mb.getMsg());
		values.put(IMG_URL, mb.getImgUrl());
		values.put(VOICE_URL, mb.getVoiceUrl());
		values.put(VOICE_LENGTH, mb.getVoiceLength());
		values.put(TYPE, mb.getType());
		values.put(TIME, mb.getTime());
		values.put(ISERROR, mb.isError() ? 1 : 0);
		int ret = this.getWritableDatabase().update(TABLE_NAME, values,
				whereClause, whereArgs);
		this.getWritableDatabase().close();
		return ret;
	}
}
