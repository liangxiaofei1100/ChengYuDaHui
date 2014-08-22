package com.zhaoyan.juyou.game.chengyudahui.dictate;

import android.os.Bundle;

public interface StoryListener {
	
	public static final int MSG_START_DOWNLOAD = 1;
	public static final int MSG_STOP_DOWNLOAD = 2;
	public static final int MSG_UPDATE_UI = 3;
	public static final int MSG_START_PLAY = 4;
	public static final int MSG_OPEN_APP = 5;
	public static final int MSG_UPDATE_APP = 6;
	public static final int MSG_DOWNLOAD_COMPLETE = 7;
	public static final int MSG_UPDATE_AUDIO_TIME = 8;
	
	public static final String CALLBACK_FLAG = "callback_flag";
	public static final String KEY_ITEM_STORYINFO = "key_item_storyinfo";
	public static final String KEY_ITEM_POSITION = "key_item_position";
	public static final String KEY_ITEM_ID = "key_item_id";
	public static final String KEY_ITEM_OP_TYE = "key_item_op_type";
	public static final String KEY_ITEM_GROUP_NAME = "key_item_group_name";
	
	void onCallBack(Bundle bundle);
}
