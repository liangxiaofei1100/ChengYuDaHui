package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.juyou.game.chengyudahui.BackgroundMusicManager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

public class BackgroundMusicBaseActivity extends BaseActivity {
	protected BackgroundMusicManager mBackgroundMusicManager;
	private boolean mPlayWhenActivitySwitch = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideActionBar();
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setPlayWhenActivitySwitch(true);
		super.onBackPressed();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		setPlayWhenActivitySwitch(true);
		super.finish();
	}

	private void hideActionBar() {
		PackageManager packageManager = getPackageManager();
		try {
			ActivityInfo info = packageManager.getActivityInfo(
					getComponentName(), 0);
			String name = getResources().getResourceEntryName(info.theme);
			if (name != null) {
				if (name.equals("LauncherTheme")
						|| name.equals("ActivityTheme")
						|| name.equals("ActivityTheme2")) {
					getSupportActionBar().hide();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
