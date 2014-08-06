package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.MainActivity;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.CopyDBFile;

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
		MainActivity.FILES_DIR = this.getFilesDir().getAbsolutePath();
		MainActivity.DB_DIR = MainActivity.FILES_DIR + "/database";
		MainActivity.DB_PATH = MainActivity.DB_DIR + "/chengyu.db";
		MainActivity.GUOXUE_DB_PATH = MainActivity.DB_DIR + "/guoxue.db";
		MainActivity.KNOWLEDGE_FILES = MainActivity.FILES_DIR + "/knowledge1.xml";//第一回的所有题目
		new CopyDBFile().copyDB(this);
		new CopyDBFile().copyGuoXueDB(this);
		new CopyDBFile().copyKnowledgeFile(this);
	}

	public void launchMainMenu(View view) {
		Log.d(TAG, "launchMainMenu");
		Intent intent = new Intent(mContext, MainMenuActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
		finish();
	}

}
