package com.zhaoyan.juyou.game.chengyudahui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.zhaoyan.communication.util.Log;

public class BackgroundMusicService extends Service {
	private static final String TAG = BackgroundMusicService.class
			.getSimpleName();

	public static final String ACTION_PLAY = "play";
	public static final String ACTION_PAUSE = "pause";
	public static final String ACTION_STOP = "stop";

	private Context mContext;
	private MediaPlayer mMediaPlayer;
	private BackgroundMusicBinder mBinder;

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}

	public void init() {
		Log.d(TAG, "init");
		mContext = this;
		mBinder = new BackgroundMusicBinder();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand " + intent);
		if (intent != null) {
			String action = intent.getAction();
			if (ACTION_PLAY.equals(action)) {
				play();
			} else if (ACTION_PAUSE.equals(action)) {
				pause();
			} else if (ACTION_STOP.equals(action)) {
				stop();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mMediaPlayer != null) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		if (mBinder == null) {
			mBinder = new BackgroundMusicBinder();
		}
		return mBinder;
	}

	public boolean isPlaying() {
		if (mMediaPlayer == null) {
			return false;
		}
		return mMediaPlayer.isPlaying();
	}

	public void play() {
		Log.d(TAG, "play");
		if (mMediaPlayer == null) {
			mMediaPlayer = MediaPlayer.create(mContext, R.raw.bg_music);
			mMediaPlayer.setLooping(true);
		}

		if (mMediaPlayer.isPlaying()) {
			Log.d(TAG, "play isPlaying");
			return;
		}

		mMediaPlayer.start();
	}

	public void pause() {
		Log.d(TAG, "pause");
		if (mMediaPlayer == null) {
			return;
		}
		mMediaPlayer.pause();
	}

	public void stop() {
		Log.d(TAG, "stop");
		if (mMediaPlayer == null) {
			return;
		}
		mMediaPlayer.stop();
		mMediaPlayer.release();
		mMediaPlayer = null;
	}

	public class BackgroundMusicBinder extends Binder {
		public BackgroundMusicService getService() {
			return BackgroundMusicService.this;
		}
	}
}
