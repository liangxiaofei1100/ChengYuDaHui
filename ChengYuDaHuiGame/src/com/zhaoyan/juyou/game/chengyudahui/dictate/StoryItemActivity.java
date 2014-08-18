package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
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
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoyan.common.util.DownloadManagerPro;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.dictate.StoryDownloadDialog.OnDownloadOverListener;
import com.zhaoyan.juyou.game.chengyudahui.download.DownloadUtils;
import com.zhaoyan.juyou.game.chengyudahui.service.MusicPlayerService;

public class StoryItemActivity extends ActionBarActivity {
	private static final String TAG = StoryItemActivity.class.getSimpleName();
	
	private List<StoryInfo> mList = new ArrayList<StoryInfo>();
	
	private ViewPager mDirectionalViewPager;
	private StoryPagerAdapter mPagerAdapter;
	private CustomIndicator mIndicator;
	
	private Button mPlayPauseBtn, mNextBtn;
	private TextView mTitleView;
	private View mBottomBarView;
	
	private MusicPlayerService mMusicPlayerService = null;
	
	private DownloadManager mDownloadManager;
	private DownloadManagerPro mDownloadManagerPro;
	
	private DownloadChangeObserver mDownloadChangeObserver;
    
    private MyHandler myHandler;
    
    private StoryDownloadDialog mDownloaDialog = null;
    private StoryInfo mCurrentPlayStoryInfo = null;
    
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
				mTitleView.setText(mCurrentPlayStoryInfo.getTitle());
				mBottomBarView.setVisibility(View.VISIBLE);
				mPlayPauseBtn.setVisibility(View.VISIBLE);
				mPlayPauseBtn.setText("暂停");
				 
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
                    	mAudioUrl = localFileName;
                    	
                    	mDownloaDialog.downloadOver("下载完成", localFileName);
                    }
                }
			}
		}
	};
	
	private StoryListener storyListener = new StoryListener() {
		
		@Override
		public void onCallBack(Bundle bundle) {
			int flag = bundle.getInt(StoryListener.CALLBACK_FLAG);
			Log.d(TAG, "onCallBack flag:" + flag);

			Message message = myHandler.obtainMessage(flag);
			message.setData(bundle);
			myHandler.removeMessages(flag);
			myHandler.sendMessage(message);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.story_item);
		setTitle("天天听故事");
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		StoryInfo info = null;
		//test /init
		info = new StoryInfo();
		info.setRemotePath("storys/chengyu/chengyu_01.mp3");
		info.setFileName("chengyu_01.mp3");
		info.setTitle("矮子看戏");
		info.setSize(1065210);	
		info.setLocalPath(DownloadUtils.getExistStoryLocalPath(getApplicationContext(), "chengyu_01.mp3"));
		mList.add(info);
		
		info = new StoryInfo();
		info.setRemotePath("storys/chengyu/chengyu_02.mp3");
		info.setFileName("chengyu_02.mp3");
		info.setTitle("安步当车");
		info.setSize(2347509);
		info.setLocalPath(DownloadUtils.getExistStoryLocalPath(getApplicationContext(), "chengyu_02.mp3"));
		mList.add(info);
		
		info = new StoryInfo();
		info.setRemotePath("storys/chengyu/chengyu_03.mp3");
		info.setFileName("chengyu_03.mp3");
		info.setTitle("按图索骥");
		info.setSize(1161341);
		info.setLocalPath(DownloadUtils.getExistStoryLocalPath(getApplicationContext(), "chengyu_03.mp3"));
		mList.add(info);
		
		info = new StoryInfo();
		info.setRemotePath("storys/chengyu/chengyu_04.mp3");
		info.setFileName("chengyu_04.mp3");
		info.setTitle("暗渡成仓");
		info.setSize(2273498);	
		info.setLocalPath(DownloadUtils.getExistStoryLocalPath(getApplicationContext(), "chengyu_04.mp3"));
		mList.add(info);
		
		info = new StoryInfo();
		info.setRemotePath("storys/bear/bear_01.mp3");
		info.setFileName("bear_01.mp3");
		info.setTitle("小熊维尼晚安故事-星期二-01-片头");
		info.setSize(1333359);	
		info.setLocalPath(DownloadUtils.getExistStoryLocalPath(getApplicationContext(), "bear_01.mp3"));
		mList.add(info);
		
		info = new StoryInfo();
		info.setRemotePath("storys/bear/bear_02.mp3");
		info.setFileName("bear_02.mp3");
		info.setTitle("小熊维尼晚安故事-星期二-02-追逐影子");
		info.setSize(4194374);	
		info.setLocalPath(DownloadUtils.getExistStoryLocalPath(getApplicationContext(), "bear_02.mp3"));
		mList.add(info);
		
		//test init
//		for (int i = 0; i < 55; i++) {
//			info = new StoryInfo();
//			info.setTitle("TEST" + i);
//			mList.add(info);
//		}
		
		mIndicator = (CustomIndicator) findViewById(R.id.story_tv_count);
		mPlayPauseBtn = (Button) findViewById(R.id.story_btn_playpause);
		mTitleView = (TextView) findViewById(R.id.story_tv_title);
		mBottomBarView = findViewById(R.id.story_rl_bottom_bar);
		
		mDirectionalViewPager = (ViewPager) findViewById(R.id.story_viewpager);
		mPagerAdapter = new StoryPagerAdapter(this, mList, mIndicator, 6);
		mDirectionalViewPager.setAdapter(mPagerAdapter);
		mDirectionalViewPager.setOnPageChangeListener(listener);
		mPagerAdapter.registerKeyListener(storyListener);
		
		bindService(new Intent(this,MusicPlayerService.class), mPlaybackConnection, Context.BIND_AUTO_CREATE);
		
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
	
	private OnPageChangeListener listener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			mIndicator.setCurrentPosition(arg0);
			mPagerAdapter.setCurrentPage(arg0);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void doSelect(StoryInfo info){
		StoryInfo storyInfo = null;
		for (int i = 0; i < mList.size(); i++) {
			storyInfo = mList.get(i);
			if ( info.getRemotePath().equals(storyInfo.getRemotePath() )) {
				storyInfo.setSelect(true);
			} else {
				storyInfo.setSelect(false);
			}
		}
		mPagerAdapter.update();
	}
	
	public void startPlay(StoryInfo info){
		doSelect(info);
		
		mMusicPlayerService.setDataSource(info.getLocalPath());
		mMusicPlayerService.start();
	}
	
	public void playPause(View view) {
		 // Perform action on click
        if (mMusicPlayerService != null && mMusicPlayerService.isPlaying()) {
        	mMusicPlayerService.pause();
            mPlayPauseBtn.setText("播放");
        } else if (mMusicPlayerService != null){
        	mMusicPlayerService.start();
        	mPlayPauseBtn.setText("暂停");
        }
	}
	
	public void next(View view){
		
	}

	public void stop(View view) {
		if (mMusicPlayerService != null) {
//			mStopBtn.setVisibility(View.INVISIBLE);
//			mPlayPauseBtn.setVisibility(View.INVISIBLE);
//			mStartPlayButton.setVisibility(View.VISIBLE);
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
//                    	mDownloadBtn.setText("正在下载:0%");
                    	mDownloaDialog.updateProgress(0);
                        if (msg.arg2 < 0) {
                        } else {
                        	int progress = getProgress(msg.arg1, msg.arg2);
                        	Log.d(TAG, "progress:" + progress);
                        	mDownloaDialog.updateProgress(progress);
//                        	mDownloadBtn.setText("正在下载:" + progress + "%");
                        }
                    } else {
                        if (status == DownloadManager.STATUS_FAILED) {
                        	int reason = mDownloadManagerPro.getReason(mDownloadId);
                        	Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
//                        	mDownloadBtn.setText("下载失败，点击重新下载");
                        	Log.e(TAG, "download failed.reason:" + reason);
                        } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
//                        	mDownloadBtn.setText("下载完成");
//                        	mDownloadBtn.setEnabled(false);
                        } else {
                        }
                    }
                    break;
                case StoryListener.MSG_START_DOWNLOAD:
                	Bundle bundle = msg.getData();
                	StoryInfo info = null;
                	if (bundle != null) {
						info = bundle.getParcelable(StoryListener.KEY_ITEM_STORYINFO);
						mCurrentPlayStoryInfo = info;
						mDownloaDialog = new StoryDownloadDialog(StoryItemActivity.this, info);
						mDownloaDialog.setOnDwonloadOverListener(new OnDownloadOverListener() {
							@Override
							public void downloadOver(StoryInfo info) {
								// TODO Auto-generated method stub
								Log.d(TAG, "startPlay,localpath:" + info.getLocalPath());
								startPlay(info);
							}
						});
						mDownloaDialog.show();
						
						mDownloadId = DownloadUtils.downloadAudio(StoryItemActivity.this, mDownloadManager, info);
					} else {
						Log.e(TAG, "myhandler.bundle is null");
					}
                	break;
                case StoryListener.MSG_START_PLAY:
                	Bundle bundle2 = msg.getData();
                	StoryInfo info2 = null;
                	if (bundle2 != null) {
                		info2 = bundle2.getParcelable(StoryListener.KEY_ITEM_STORYINFO);
						mCurrentPlayStoryInfo = info2;
						startPlay(info2);
                	} else {
                		Log.e(TAG, "myhandler.bundle is null");
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
		if (mPagerAdapter != null) {
			mPagerAdapter.unregisterMyKeyListener(storyListener);
		}
		
		super.onDestroy();
	}
	
}
