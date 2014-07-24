package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;

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
	}

	public void launchMainMenu(View view) {
		Log.d(TAG, "launchMainMenu");
		Intent intent = new Intent(mContext, MainMenuActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
		finish();
	}

}
