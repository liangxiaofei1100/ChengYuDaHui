package com.zhaoyan.juyou.game.chengyudahui.friend;

public class MsgConfig {
	
	public static final int TYPE_TEXT = 0x00;
	public static final int TYPE_IMAGE = 0x01;
	public static final int TYPE_VOICE = 0x02;
	public static final int TYPE_LOCATION = 0x03;
	public static final int TYPE_VIDEO = 0x04;
	
	public static final int STATUS_SEND_SUCCESS = 0x10;
	public static final int STATUS_SEND_FAIL = 0x11;
	public static final int STATUS_SEND_RECEIVERED = 0x12;
	public static final int STATUS_SEND_START = 0x13;
	
	public static final String ACTION_NEW_MESSAGE = "com.zhaoyan.hanyu.action_new_message";
}
