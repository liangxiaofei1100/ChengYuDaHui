package com.zhaoyan.juyou.game.chengyudahui.spy;

import android.os.Bundle;

public interface SpyListener {

	public static final int MSG_UPDATE_HOST_UI = 0;
	public static final int MSG_UPDATE_USER_UI = 1;
	public static final int MSG_GAME_CREATED = 2;
	public static final int MSG_EXIT_GAME = 3;
	public static final int MSG_NO_USER_CONNECTED = 4;
	
	public static final String CALLBACK_FLAG = "callback_flag";
	
	public static final String KEY_APPID = "key_appid";
	public static final String KEY_USERLIST = "key_userlist";
	public static final String KEY_HOSTLIST = "key_hostlist";
	public static final String KEY_STATUS = "key_status";
	
	public static final int STATUS_INIT = 0;
	public static final int STATUS_CREATED = 1;
	public static final int STATUS_SEARCHED = 2;
	public static final int STATUS_JOINED = 3;
	
	void onCallBack(Bundle bundle);
}
