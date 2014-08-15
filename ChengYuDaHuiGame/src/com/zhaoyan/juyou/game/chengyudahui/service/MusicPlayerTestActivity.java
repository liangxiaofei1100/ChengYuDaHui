package com.zhaoyan.juyou.game.chengyudahui.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoyan.common.util.DownloadManagerPro;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.download.DownloadUtils;

public class MusicPlayerTestActivity extends Activity {
	private static final String TAG = MusicPlayerTestActivity.class.getSimpleName();
	
	private TextView mTitleView;
	private Button mStartPlayButton;
	private Button mDownloadBtn,mPlayPauseBtn, mStopBtn;

	private MusicPlayerService mMusicPlayerService = null;
	
	private DownloadManager mDownloadManager;
	private DownloadManagerPro mDownloadManagerPro;
	
	private DownloadChangeObserver mDownloadChangeObserver;
    
    private MyHandler myHandler;
    
    private long mDownloadId = 0;
    private String mAudioUrl = "";

	private ServiceConnection mPlaybackConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "onServiceConnected");
			mMusicPlayerService = ((MusicPlayerService.LocalBinder) service)
					.getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			Log.d(TAG, "onServiceDisconnected");
			mMusicPlayerService = null;
		}
	};

	protected BroadcastReceiver mPlayerEvtReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MusicPlayerService.PLAYER_PREPARE_END)) {
				// will begin to play
				// mTextView.setVisibility(View.INVISIBLE);
				// mPlayPauseButton.setVisibility(View.VISIBLE);
				 mStopBtn.setVisibility(View.VISIBLE);
				 mPlayPauseBtn.setVisibility(View.VISIBLE);
				 mPlayPauseBtn.setText("暂停");
				 
				 mStartPlayButton.setVisibility(View.INVISIBLE);
			} else if (action.equals(MusicPlayerService.PLAY_COMPLETED)) {
				mPlayPauseBtn.setText("播放");
			}  else if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
        		long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                String localFileName = mDownloadManagerPro.getFileName(mDownloadId);
                Log.d(TAG, "localFileName:" + localFileName);
                Log.d(TAG, "completeDownloadId:" + completeDownloadId + ",mDOwnloadId:" + mDownloadId);
                if (completeDownloadId == mDownloadId) {
                    updateView();
                    // if download successful, install apk
                    int status = mDownloadManagerPro.getStatusById(mDownloadId);
                    Log.d(TAG, "status:" +  status);
                    if ( status == DownloadManager.STATUS_SUCCESSFUL) {
                    	mTitleView.setText("" + localFileName);
                    	mAudioUrl = localFileName;
                    	
                    	mStartPlayButton.setVisibility(View.VISIBLE);
//                    	mStopBtn.setVisibility(View.VISIBLE);
                    }
                }
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_test);
		
		bindService(new Intent(this,MusicPlayerService.class), mPlaybackConnection, Context.BIND_AUTO_CREATE);

		initView();
		
		myHandler = new MyHandler();
		
		mDownloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
		mDownloadManagerPro = new DownloadManagerPro(mDownloadManager);
		mDownloadChangeObserver = new DownloadChangeObserver();
		
		 /** observer download change **/
        getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, mDownloadChangeObserver);
        
		IntentFilter filter = new IntentFilter();
		filter.addAction(MusicPlayerService.PLAYER_PREPARE_END);
		filter.addAction(MusicPlayerService.PLAY_COMPLETED);
		filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		registerReceiver(mPlayerEvtReceiver, filter);
	}
	
	private void initView(){
		mTitleView = (TextView) findViewById(R.id.tv_music_title);
		
		mStartPlayButton = (Button) findViewById(R.id.btn_music_startplay);
		mDownloadBtn = (Button) findViewById(R.id.btn_music_download);
		mPlayPauseBtn = (Button) findViewById(R.id.btn_music_playpause);
		mStopBtn = (Button) findViewById(R.id.btn_music_stop);
	}

	public void download(View view) {
		mDownloadId = DownloadUtils.downloadAudio(getApplicationContext(), mDownloadManager, 4390657, "libai.mp3");
		updateView();
	}
	
	
	public void startPlay(View view){
		mMusicPlayerService.setDataSource(mAudioUrl);
		mMusicPlayerService.start();
	}

	public void play(View view) {
		 // Perform action on click
        if (mMusicPlayerService != null && mMusicPlayerService.isPlaying()) {
        	mMusicPlayerService.pause();
            mPlayPauseBtn.setText("播放");
        } else if (mMusicPlayerService != null){
        	mMusicPlayerService.start();
        	mPlayPauseBtn.setText("暂停");
        }
	}

	public void stop(View view) {
		if (mMusicPlayerService != null) {
			mStopBtn.setVisibility(View.INVISIBLE);
			mPlayPauseBtn.setVisibility(View.INVISIBLE);
			mStartPlayButton.setVisibility(View.VISIBLE);
        	mMusicPlayerService.stop();
        } 
	}
	
	class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(myHandler);
        }

        @Override
        public void onChange(boolean selfChange) {
            updateView();
        }

    }
	
	public void updateView() {
        int[] bytesAndStatus = mDownloadManagerPro.getBytesAndStatus(mDownloadId);
        myHandler.sendMessage(myHandler.obtainMessage(0, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]));
    }
	
	/**
     * MyHandler
     */
    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    int status = (Integer)msg.obj;
                    if (isDownloading(status)) {
                    	mDownloadBtn.setText("正在下载:0%");
                        if (msg.arg2 < 0) {
                        } else {
                        	int progress = getProgress(msg.arg1, msg.arg2);
                        	Log.d(TAG, "progress:" + progress);
                        	mDownloadBtn.setText("正在下载:" + progress + "%");
                        }
                    } else {
                        if (status == DownloadManager.STATUS_FAILED) {
                        	int reason = mDownloadManagerPro.getReason(mDownloadId);
                        	Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                        	mDownloadBtn.setText("下载失败，点击重新下载");
                        	Log.e(TAG, "download failed.reason:" + reason);
                        } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        	mDownloadBtn.setText("下载完成");
                        	mDownloadBtn.setEnabled(false);
                        } else {
                        }
                    }
                    break;
            }
        }
    }

    public static String getNotiPercent(long progress, long max) {
        int rate = 0;
        if (progress <= 0 || max <= 0) {
            rate = 0;
        } else if (progress > max) {
            rate = 100;
        } else {
            rate = (int)((double)progress / max * 100);
        }
        return new StringBuilder(16).append(rate).append("%").toString();
    }
    
    public static int getProgress(long progress, long max) {
        int rate = 0;
        if (progress <= 0 || max <= 0) {
            rate = 0;
        } else if (progress > max) {
            rate = 100;
        } else {
            rate = (int)((double)progress / max * 100);
        }
        return rate;
    }
    
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static boolean isDownloading(int downloadManagerStatus) {
        return downloadManagerStatus == DownloadManager.STATUS_RUNNING
                || downloadManagerStatus == DownloadManager.STATUS_PAUSED
                || downloadManagerStatus == DownloadManager.STATUS_PENDING;
    }
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unbindService(mPlaybackConnection);
		unregisterReceiver(mPlayerEvtReceiver);
		
		super.onDestroy();
	}

}
