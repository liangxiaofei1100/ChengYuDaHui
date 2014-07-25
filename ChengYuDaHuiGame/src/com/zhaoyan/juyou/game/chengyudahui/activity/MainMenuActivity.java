package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.study.StudyActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Main menu of the App.
 * 
 */
public class MainMenuActivity extends Activity {
	private static final String TAG = MainMenuActivity.class.getSimpleName();
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		mContext = this;
	}

	public void launchChengYuStudy(View view) {
		Log.d(TAG, "launchChengYuStudy");
		Intent intent = new Intent(mContext, GetGoldActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
	}
}
