package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhaoyan.communication.UserHelper;
import com.zhaoyan.communication.UserInfo;
import com.zhaoyan.communication.ipc.aidl.User;
import com.zhaoyan.communication.provider.ZhaoYanCommunicationData;
import com.zhaoyan.communication.util.Log;

public class UserInfoSettingActivity extends Activity {
	private static final String TAG = UserInfoSettingActivity.class
			.getSimpleName();
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		login();

		Intent intent = new Intent();
		intent.setClass(this, ConnectFriendsActivity.class);
		intent.putExtra("Game", getIntent().getStringExtra("Game"));
		startActivity(intent);

		finish();
	}

	public boolean login() {
		Log.d(TAG, "login");

		// Set userinfo
		UserInfo userInfo = UserHelper.loadLocalUser(mContext);
		if (userInfo == null) {
			// This is the first time launch. Set user info.
			userInfo = new UserInfo();
			userInfo.setUser(new User());
			userInfo.setType(ZhaoYanCommunicationData.User.TYPE_LOCAL);
			userInfo.getUser().setUserName(android.os.Build.MANUFACTURER);
			userInfo.getUser().setUserID(0);
			userInfo.setHeadId(0);
		}
		UserHelper.saveLocalUser(mContext, userInfo);
		return true;
	}
}
