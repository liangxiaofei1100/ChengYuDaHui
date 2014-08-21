package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoyan.common.util.DownloadManagerPro;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.DBConfig;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.StoryData.ItemColums;
import com.zhaoyan.juyou.game.chengyudahui.dictate.StoryDownloadDialog.OnDownloadOverListener;
import com.zhaoyan.juyou.game.chengyudahui.download.DownloadUtils;
import com.zhaoyan.juyou.game.chengyudahui.service.MusicPlayerService;

public class StoryItemActivity extends ActionBarActivity{
	private static final String TAG = StoryItemActivity.class.getSimpleName();
	
	private List<StoryInfo> mList = new ArrayList<StoryInfo>();
	
	//the count items that every page will view
	private static final int PAGE_COUNT = 6;
	
	private ViewPager mViewPager;
	private StoryPagerAdapter mPagerAdapter;
	private CustomIndicator mIndicator;
	
	private ImageView mPlayPauseBtn;
	private ProgressBar mProgressBar;
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
    
    private UpdateThead mUpdateThead = null;
    
    private StoryItem mStoryItem = null;
    
    private StoryQuery mStoryQuery = null;

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
				mPlayPauseBtn.setImageResource(R.drawable.pause);
				
				int duration = mMusicPlayerService.getDuration();
				mProgressBar.setMax(duration);
				updateProgressBar();
			} else if (action.equals(MusicPlayerService.PLAY_COMPLETED)) {
				mPlayPauseBtn.setImageResource(R.drawable.play);
			} else if (action.equals(MusicPlayerService.PLAYER_NOT_PREPARE)) {
				StoryInfo info = mList.get(0);
				//播放第一个
				startPlay(info);
			} else if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.story_item);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		//getdata
		mStoryItem = getIntent().getParcelableExtra("storyItem");
		setTitle(mStoryItem.getTypeName());
		
		mIndicator = (CustomIndicator) findViewById(R.id.story_tv_count);
		mPlayPauseBtn = (ImageView) findViewById(R.id.story_iv_playpause);
		mTitleView = (TextView) findViewById(R.id.story_tv_title);
		mBottomBarView = findViewById(R.id.story_rl_bottom_bar);
		mProgressBar = (ProgressBar) findViewById(R.id.story_progressbar);
		
		mViewPager = (ViewPager) findViewById(R.id.story_viewpager);
		mViewPager.setOnPageChangeListener(pageChangeListener);
		
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
		filter.addAction(MusicPlayerService.PLAYER_NOT_PREPARE);
		filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		registerReceiver(mPlayerEvtReceiver, filter);
		
		getStoryDatas();
	}
	
	private void getStoryDatas(){
		Uri uri = null;
		switch (mStoryItem.getTypeId()) {
		case DBConfig.STORY_BEAR:
			uri = ItemColums.BEAR_CONTENT_URI;
			break;
		case DBConfig.STORY_CHENGYU:
			uri = ItemColums.CHENGYU_CONTENT_URI;
			break;
		case DBConfig.STORY_SLEEP:
			uri = ItemColums.SLEEP_CONTENT_URI;
			break;
		case DBConfig.STORY_CHILD:
			uri = ItemColums.CHILD_CONTENT_URI;
			break;
		case DBConfig.STORY_FAIRY_TALE:
			uri = ItemColums.FAIRY_TALE_CONTENT_URI;
			break;
		case DBConfig.STORY_HISTORY:
			uri = ItemColums.HISTORY_CONTENT_URI;
			break;
		case DBConfig.STORY_GOLD_CAT:
			uri = ItemColums.GOLD_CAT_CONTENT_URI;
			break;
		case DBConfig.STORY_XIYOUJI:
			uri = ItemColums.XIYOUJI_CONTENT_URI;
			break;
		case DBConfig.STORY_CHILD_SONG:
			uri = ItemColums.CHILD_SONG_CONTENT_URI;
			break;
		default:
			break;
		}
		mStoryQuery = new StoryQuery(getContentResolver());
		mStoryQuery.startQuery(0, null, uri, null, null, null, null);
	}
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

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
	
	private class StoryQuery extends AsyncQueryHandler{

		public StoryQuery(ContentResolver cr) {
			super(cr);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			super.onQueryComplete(token, cookie, cursor);
			StoryInfo info = null;
			if (cursor != null && cursor.moveToFirst()) {
				String title = "";
				String fileName = "";
				long size;
				do {
					title = cursor.getString(cursor.getColumnIndex(ItemColums.TITLE));
					fileName = cursor.getString(cursor.getColumnIndex(ItemColums.FILENAME));
					size = cursor.getLong(cursor.getColumnIndex(ItemColums.SIZE));
					
					info = new StoryInfo();
					info.setFolder(mStoryItem.getFolder());
					info.setFileName(fileName);
					info.setTitle(title);
					info.setSize(size);
					info.setLocalPath(DownloadUtils.getExistStoryLocalPath(
							getApplicationContext(), mStoryItem.getFolder(), fileName));
					mList.add(info);
				} while (cursor.moveToNext());
			}
			mPagerAdapter = new StoryPagerAdapter(StoryItemActivity.this, mList, mIndicator, PAGE_COUNT);
			mViewPager.setAdapter(mPagerAdapter);
			mPagerAdapter.registerKeyListener(storyListener);
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
			if ( info.getFileName().equals(storyInfo.getFileName())) {
				storyInfo.setSelect(true);
			} else {
				storyInfo.setSelect(false);
			}
		}
		mPagerAdapter.update();
	}
	
	public void startPlay(StoryInfo info){
		Log.d(TAG, "startPlay.info:" + info.getTitle());
		mCurrentPlayStoryInfo = info;
		if (info.getLocalPath() == null) {
			return;
		}
		doSelect(info);
		mMusicPlayerService.setDataSource(info.getLocalPath());
		mMusicPlayerService.start();
	}
	
	public void playPause(View view) {
		 // Perform action on click
        if (mMusicPlayerService != null && mMusicPlayerService.isPlaying()) {
        	mMusicPlayerService.pause();
            mPlayPauseBtn.setImageResource(R.drawable.play);
        } else if (mMusicPlayerService != null){
        	mMusicPlayerService.start();
        	mPlayPauseBtn.setImageResource(R.drawable.pause);
        }
	}
	
	public void next(View view){
		if (mCurrentPlayStoryInfo == null) {
			return;
		}
		
		int currentPosition = mCurrentPlayStoryInfo.getPosition();
		int next = currentPosition + 1;
		next = (next >= mList.size()) ? 0 : next;
		while (mList.get(next).getLocalPath() == null) {
			if (next == mList.size()) {
				next = 0;
			} else {
				next += 1;
				next = (next >= mList.size()) ? 0 : next;
			}
		}
		
//		int currentPage = mCurrentPlayStoryInfo.getPage();
//		int nextPage = mList.get(next).getPage();
//		if (nextPage != currentPage) {
//			mViewPager.setCurrentItem(nextPage, true);
//			mPagerAdapter.setCurrentPage(nextPage);
//		}
		startPlay(mList.get(next));
	}
	
	public void previous(View view){
		if (mCurrentPlayStoryInfo == null) {
			return;
		}
		
		int currentPosition = mCurrentPlayStoryInfo.getPosition();
		int pre = currentPosition - 1;
		pre = (pre < 0) ? (mList.size() - 1) : pre;
		while (mList.get(pre).getLocalPath() == null) {
			if (pre == 0) {
				pre = mList.size() - 1;
			} else {
				pre -= 1;
				pre = (pre < 0) ? (mList.size() - 1) : pre;
			}
		}
//		int currentPage = mCurrentPlayStoryInfo.getPage();
//		int nextPage = mList.get(pre).getPage();
//		if (nextPage != currentPage) {
//			mViewPager.setCurrentItem(nextPage, true);
//			mPagerAdapter.setCurrentPage(nextPage);
//		}
		startPlay(mList.get(pre));
	}

	public void stop(View view) {
		if (mMusicPlayerService != null) {
        	mMusicPlayerService.stop();
        } 
	}
	
	private void updateProgressBar(){
		mProgressBar.setProgress(0);
		if (mUpdateThead != null && mUpdateThead.isAlive()) {
			Log.d(TAG, "mupdate thread is alive");
			return;
		}
		
		mUpdateThead = new UpdateThead();
		mUpdateThead.start();
	}
	
	private class UpdateThead extends Thread {
		@Override
		public void run() {
			super.run();
			int currentPosition = 0;
			int total = mMusicPlayerService.getDuration();
			while (mMusicPlayerService != null && currentPosition < total) {
				try {
					Thread.sleep(1000);
					currentPosition = mMusicPlayerService.getPosition();
				} catch (InterruptedException e) {
					Log.e(TAG, "run.error:" + e.toString());
					return;
				} catch (Exception e) {
					Log.e(TAG, "run.error:" + e.toString());
					return;
				}
				mProgressBar.setProgress(currentPosition);
			}
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
						
						mDownloadId = DownloadUtils.downloadStorys(StoryItemActivity.this, mDownloadManager, info);
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
