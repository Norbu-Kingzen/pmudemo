package com.example.pmudemo.bean;

/**
 * 
 * @author weishijie
 *
 */
public class MessageBean {

	public static final int MESSAGE_IMG_SEND = 0;
	public static final int MESSAGE_IMG_RECEIVE = 1;
	public static final int MESSAGE_VOICE_SEND = 2;
	public static final int MESSAGE_VOICE_RECEIVE = 3;
	private int id;
	private String msg;
	private String imgUrl;
	private String voiceUrl;
	private long voiceLength;
	private int faceUrl;
	private int type;
	private long time;
	private boolean isError;

	public MessageBean()
	{
	}

	public MessageBean(int id, int faceUrl, String msg, String imgUrl,
			String voiceUrl, long voiceLength, int type, long time,
			boolean isError)
	{
		this.id = id;
		this.faceUrl = faceUrl;
		this.msg = msg;
		this.imgUrl = imgUrl;
		this.voiceUrl = voiceUrl;
		this.type = type;
		this.time = time;
		this.voiceLength = voiceLength;
		this.isError = isError;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getVoiceUrl() {
		return voiceUrl;
	}

	public void setVoiceUrl(String voiceUrl) {
		this.voiceUrl = voiceUrl;
	}

	public long getVoiceLength() {
		return voiceLength;
	}

	public void setVoiceLength(long voiceLength) {
		this.voiceLength = voiceLength;
	}

	public int getFaceUrl() {
		return faceUrl;
	}

	public void setFaceUrl(int faceUrl) {
		this.faceUrl = faceUrl;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}
}
