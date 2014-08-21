package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.juyou.game.chengyudahui.BackgroundMusicManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class BackgroundMusicBaseActivity extends BaseActivity {
	protected BackgroundMusicManager mBackgroundMusicManager;
	private boolean mPlayWhenActivitySwitch = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBackgroundMusicManager = BackgroundMusicManager.getInstance();

	}

	@Override
	protected void onStart() {
		super.onStart();
		mPlayWhenActivitySwitch = false;
		if (mBackgroundMusicManager.isBackgroundMusicEnabled()
				&& !mBackgroundMusicManager.isPlaying()) {
			mBackgroundMusicManager.play();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mPlayWhenActivitySwitch) {
			return;
		}
		if (mBackgroundMusicManager.isPlaying()) {
			mBackgroundMusicManager.pause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mPlayWhenActivitySwitch) {
			return;
		}
		mBackgroundMusicManager.stop();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		mPlayWhenActivitySwitch = true;
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		mPlayWhenActivitySwitch = true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mPlayWhenActivitySwitch = true;
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setPlayWhenActivitySwitch(boolean play) {
		mPlayWhenActivitySwitch = play;
	}
}
