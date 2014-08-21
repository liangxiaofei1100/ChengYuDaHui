package com.zhaoyan.juyou.game.chengyudahui.service;

import java.io.IOException;

import com.zhaoyan.communication.util.Log;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.IBinder;

public class MusicPlayerService extends Service {
	private static final String TAG = MusicPlayerService.class.getSimpleName();
	private final IBinder mBinder = new LocalBinder();

	private MediaPlayer mMediaPlayer = null;

	public static final String PLAYER_PREPARE_END = "com.zhaoyao.juyou.musicplayerservice.prepared";
	public static final String PLAY_COMPLETED = "com.zhaoyao.juyou.musicplayerservice.playcompleted";
	public static final String PLAYER_NOT_PREPARE = "com.zhaoyao.juyou.musicplayerservice.notprepare";

	MediaPlayer.OnCompletionListener mCompleteListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			broadcastEvent(PLAY_COMPLETED);
		}
	};

	MediaPlayer.OnPreparedListener mPrepareListener = new MediaPlayer.OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
			broadcastEvent(PLAYER_PREPARE_END);
		}
	};

	private void broadcastEvent(String what) {
		Intent i = new Intent(what);
		sendBroadcast(i);
	}

	public void onCreate() {
		super.onCreate();

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(mPrepareListener);
		mMediaPlayer.setOnCompletionListener(mCompleteListener);
		mMediaPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.e(TAG, "onError.what:" + what + ",extra:" + extra);
				if (what == -38) {
					broadcastEvent(PLAYER_NOT_PREPARE);
					return true;
				}
				return false;
			}
		});
	}

	public class LocalBinder extends Binder {
		public MusicPlayerService getService() {
			return MusicPlayerService.this;
		}
	}

	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void setDataSource(String path) {

		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepare();
		} catch (IOException e) {
			return;
		} catch (IllegalArgumentException e) {
			return;
		}
	}

	public void start() {
		mMediaPlayer.start();
	}

	public void stop() {
		mMediaPlayer.stop();
	}

	public void pause() {
		mMediaPlayer.pause();
	}

	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}

	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	public int getPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	public long seek(long whereto) {
		mMediaPlayer.seekTo((int) whereto);
		return whereto;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
		}
		super.onDestroy();
	}
}
