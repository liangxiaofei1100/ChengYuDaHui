package com.zhaoyan.juyou.game.chengyudahui;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.IBinder;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.BackgroundMusicService.BackgroundMusicBinder;
import com.zhaoyan.juyou.game.chengyudahui.activity.SettingActivity;

/**
 * Background music manage
 * 
 */
public class BackgroundMusicManager {
	private static final String TAG = BackgroundMusicManager.class
			.getSimpleName();

	private BackgroundMusicService mService;
	private Context mContext;
	private boolean mIsBackgroundMusicEnabled = false;

	private static final BackgroundMusicManager mInstance = new BackgroundMusicManager();

	private BackgroundMusicManager() {
		mContext = JuYouApplication.getApplicContext();
		SharedPreferences preferences = mContext.getSharedPreferences(
				SettingActivity.PREFERENCE_SETTING_NAME, Context.MODE_PRIVATE);
		preferences
				.registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
		mIsBackgroundMusicEnabled = preferences.getBoolean(
				SettingActivity.PREFERENCE_KEY_BACKGROUND_MUSIC, true);
	}

	public static final BackgroundMusicManager getInstance() {
		return mInstance;
	}

	public void init(Context context) {
		Log.d(TAG, "init");
		mContext = context;

		if (isBackgroundMusicEnabled()) {
			startService(context);
		} else {
			Log.d(TAG, "isBackgroundMusicEnabled false");
		}
	}

	public void startService(Context context) {
		Log.d(TAG, "startService");
		context = context.getApplicationContext();
		Intent intent = new Intent(context, BackgroundMusicService.class);
		context.startService(intent);
		context.bindService(intent, mServiceConnection,
				Service.BIND_AUTO_CREATE);
	}

	public void stopService(Context context) {
		Log.d(TAG, "stopService");
		Intent intent = new Intent(context, BackgroundMusicService.class);
		context.stopService(intent);
		context.unbindService(mServiceConnection);
		mService = null;
	}

	public boolean isBackgroundMusicEnabled() {
		return mIsBackgroundMusicEnabled;
	}

	public boolean isPlaying() {
		boolean isPlaying = false;

		if (mService == null) {
			isPlaying = false;
		} else {
			try {
				isPlaying = mService.isPlaying();
			} catch (Exception e) {
				Log.e(TAG, "isPlaying" + e);
			}
		}
		return isPlaying;
	}

	public void play() {
		if (!isBackgroundMusicEnabled()) {
			return;
		}
		if (mService == null) {
			Intent intent = new Intent(mContext, BackgroundMusicService.class);
			intent.setAction(BackgroundMusicService.ACTION_PLAY);
			mContext.startService(intent);
			mContext.bindService(intent, mServiceConnection,
					Service.BIND_AUTO_CREATE);
		} else {
			try {
				mService.play();
			} catch (Exception e) {
				Log.e(TAG, "play " + e);
				e.printStackTrace();
			}
		}
	}

	public void pause() {
		if (mService == null) {
			Intent intent = new Intent(mContext, BackgroundMusicService.class);
			intent.setAction(BackgroundMusicService.ACTION_PAUSE);
			mContext.startService(intent);
			mContext.bindService(intent, mServiceConnection,
					Service.BIND_AUTO_CREATE);
		} else {
			try {
				mService.pause();
			} catch (Exception e) {
				Log.e(TAG, "pause error." + e);
			}
		}
	}

	public void stop() {
		if (mService == null) {
			Intent intent = new Intent(mContext, BackgroundMusicService.class);
			intent.setAction(BackgroundMusicService.ACTION_STOP);
			mContext.startService(intent);
			mContext.bindService(intent, mServiceConnection,
					Service.BIND_AUTO_CREATE);
		} else {
			try {
				mService.stop();
			} catch (Exception e) {
				Log.e(TAG, "stop error." + e);
			}
		}
	}

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.d(TAG, "onServiceDisconnected");
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			Log.d(TAG, "onServiceConnected ");
			BackgroundMusicBinder backgroundMusicBinder = (BackgroundMusicBinder) binder;
			mService = backgroundMusicBinder.getService();
			Log.d(TAG, "onServiceConnected service = " + mService);
		}
	};

	private SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(SharedPreferences preferences,
				String key) {
			Log.d(TAG, "onSharedPreferenceChanged " + key);
			if (SettingActivity.PREFERENCE_KEY_BACKGROUND_MUSIC.equals(key)) {
				mIsBackgroundMusicEnabled = preferences.getBoolean(key, true);
				if (mIsBackgroundMusicEnabled) {
					if (!isPlaying()) {
						play();
					}
				} else {
					if (isPlaying()) {
						stop();
					}
				}
			}
		}
	};

}
