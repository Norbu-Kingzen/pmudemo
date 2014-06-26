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
	/** Columns */
	private final static String ID = "ID";
	private final static String ACTIVITY_ID = "ACTIVITY_ID";
	private final static String FACE_URL = "FACE_URL";
	/**
	 * Text
	 */
	private final static String MSG = "MSG";
	private final static String IMG_URL = "IMG_URL";
	private final static String VOICE_URL = "VOICE_URL";
	private final static String VOICE_LENGTH = "VOICE_LENGTH";
	/**
	 * Message type : 1 - audio;2 - picture
	 */
	private final static String TYPE = "TYPE";
	/**
	 * Server time of the message been sent to server
	 */
	private final static String TIME = "TIME";
	/**
	 * If this message is sent successfully : 0 - successful; 1 - failed
	 */
	private final static String ISERROR = "ISERROR";

	public MsgDao(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String sql = "Create table IF NOT EXISTS " + TABLE_NAME + "(" + ID
				+ " integer PRIMARY KEY," + ACTIVITY_ID + " integer," + FACE_URL + " text," + MSG
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

	public List<MessageBean> selectLast500(int activityId)
	{
		List<MessageBean> mbList = new ArrayList<MessageBean>();
		String[] columns = new String[] { ID, ACTIVITY_ID, FACE_URL, MSG, IMG_URL,
				VOICE_URL, VOICE_LENGTH, TYPE, TIME, ISERROR };
		String whereClause = ACTIVITY_ID + "=?";
		String[] whereArgs = new String[] { String.valueOf(activityId) };
		String groupBy = null;
		String having = null;
		String orderBy = TIME + " ASC";
		String limit = null;
		// SQL query
		Cursor c = this.getReadableDatabase().query(TABLE_NAME, columns,
				whereClause, whereArgs, groupBy, having, orderBy, limit);
		// Add message bean object into mbList by loop
		while (c.moveToNext())
		{
			int columnIndex = 0;
			int id = c.getInt(columnIndex ++);
//			int activityId = c.getInt(columnIndex ++);
			int faceUrl = c.getInt(columnIndex ++);
			String msg = c.getString(columnIndex ++);
			String imgUrl = c.getString(columnIndex ++);
			String voiceUrl = c.getString(columnIndex ++);
			long voiceLength = c.getLong(columnIndex ++);
			int type = c.getInt(columnIndex ++);
			long time = c.getLong(columnIndex ++);
			int isError = c.getInt(columnIndex ++);
			mbList.add(new MessageBean(id, activityId, faceUrl, msg, imgUrl, voiceUrl,
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
		values.put(ACTIVITY_ID, mb.getActivityId());
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
