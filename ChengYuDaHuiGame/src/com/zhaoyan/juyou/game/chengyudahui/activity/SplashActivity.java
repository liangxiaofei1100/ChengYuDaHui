package com.zhaoyan.juyou.game.chengyudahui.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zhaoyan.juyou.game.chengyudahui.DBConfig;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.utils.CopyFileUtil;

/**
 * @ClassName: SplashActivity
 * @author yuri
 * @date 2014-11-18 10:29:58
 */
public class SplashActivity extends Activity {

	private static final int GO_HOME = 100;
	private static final int GO_LOGIN = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// TODO you can do some init op here
		// 只要第一次创建一下database目录就可以了
		File databaseDir = new File(DBConfig.DATABASE_DIR);
		if (!databaseDir.exists()) {
			databaseDir.mkdirs();
		}

		CopyFileUtil.copyDB(getApplicationContext(), DBConfig.DB_CHENGYU_NAME);
		CopyFileUtil.copyDB(getApplicationContext(), DBConfig.DB_GUOXUE_NAME);
		CopyFileUtil.copyDB(getApplicationContext(), DBConfig.DB_WORD_NAME);
		CopyFileUtil.copyDB(getApplicationContext(), DBConfig.DB_DICTATE_NAME);
		CopyFileUtil.copyDB(getApplicationContext(), DBConfig.DB_STORY_NAME);

		CopyFileUtil.copyFile(getApplicationContext(), DBConfig.FILE_KNOWLEDGE1);

		mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				startActivity(new Intent(SplashActivity.this,
						MainMenuActivity.class));
				finish();
				break;
			case GO_LOGIN:
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
