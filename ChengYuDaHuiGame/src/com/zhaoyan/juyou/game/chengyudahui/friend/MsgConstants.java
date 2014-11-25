package com.zhaoyan.juyou.game.chengyudahui.friend;

import android.os.Environment;

public class MsgConstants {

	public static final String APP_ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hanyu";
	public static final String LOCAL_IMAGE_DIR = APP_ROOT_DIR + "/image";
	
	public static final int REQUEST_CODE_TAKE_PICTURE = 0x10;
	public static final int REQUEST_CODE_SELECT_IMAGE = 0x11;
	public static final int REQUEST_CODE_TAKE_LOCATION = 0x12;
}
