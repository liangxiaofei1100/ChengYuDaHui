package com.zhaoyan.juyou.game.chengyudahui.activity;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.DBConfig;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.utils.CopyFileUtil;

/**
 * First page of the App. Press "Start Game" button to launch main menu
 * activity.
 */
public class LauncherActivity extends Activity {
	private static final String TAG = LauncherActivity.class.getSimpleName();
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		mContext = this;
		
		//只要第一次创建一下database目录就可以了
		File databaseDir = new File(DBConfig.DATABASE_DIR);
		if (!databaseDir.exists()) {
			databaseDir.mkdirs();
		}
		
		CopyFileUtil.copyDB(mContext, DBConfig.DB_CHENGYU_NAME);
		CopyFileUtil.copyDB(mContext, DBConfig.DB_GUOXUE_NAME);
		CopyFileUtil.copyDB(mContext, DBConfig.DB_WORD_NAME);
		CopyFileUtil.copyDB(mContext, DBConfig.DB_DICTATE_NAME);
		CopyFileUtil.copyDB(mContext, DBConfig.DB_STORY_NAME);
		
		CopyFileUtil.copyFile(mContext, DBConfig.FILE_KNOWLEDGE1);
	}

	public void launchMainMenu(View view) {
		Log.d(TAG, "launchMainMenu");
		Intent intent = new Intent(mContext, MainMenuActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
		finish();
	}

}
