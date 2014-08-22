package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoyan.common.util.DownloadManagerPro;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.DBConfig;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.StoryData.ItemColums;
import com.zhaoyan.juyou.game.chengyudahui.dictate.SideBar.OnTouchingLetterChangedListener;
import com.zhaoyan.juyou.game.chengyudahui.dictate.StoryDownloadDialog.OnDownloadOverListener;
import com.zhaoyan.juyou.game.chengyudahui.download.DownloadUtils;
import com.zhaoyan.juyou.game.chengyudahui.knowledge.Word;
import com.zhaoyan.juyou.game.chengyudahui.service.MusicPlayerService;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;
import com.zhaoyan.juyou.game.chengyudahui.view.Effectstype;
import com.zhaoyan.juyou.game.chengyudahui.view.NiftyDialogBuilder;
import com.zhaoyan.juyou.game.chengyudahui.view.TableTitleView;
import com.zhaoyan.juyou.game.chengyudahui.view.TableTitleView.OnTableSelectChangeListener;

public class StoryItemActivity extends ActionBarActivity implements OnTableSelectChangeListener, OnItemClickListener{
	private static final String TAG = StoryItemActivity.class.getSimpleName();
	
	private List<StoryInfo> mDownloadList = new ArrayList<StoryInfo>();
	private List<StoryInfo> mUnDownloadList = new ArrayList<StoryInfo>();

	private TableTitleView mTableTitleView;
	
	private ImageView mPlayPauseBtn;
	private ProgressBar mProgressBar;
	private TextView mPlayBarTitleView;
	private View mPlayBarView;
	private TextView mPlayBarMaxTimeView;
	private TextView mPlayBarCurrentTimeView;
	
	private MusicPlayerService mMusicPlayerService = null;
	
	private DownloadManager mDownloadManager;
	private DownloadManagerPro mDownloadManagerPro;
	
	private DownloadChangeObserver mDownloadChangeObserver;
    
    private MyHandler myHandler;
    
    private StoryDownloadDialog mDownloaDialog = null;
    private StoryInfo mCurrentPlayStoryInfo = null;
    
    private long mDownloadId = 0;
    
    private UpdateThead mUpdateThead = null;
    
    private StoryItem mStoryItem = null;
    
    private ListView mListView;
	private SideBar mSideBar;
	private TextView mTipTextView;
	private StoryItemAdapter mAdapter;
	private ProgressBar mLoadingBar;
	
	private CharacterParser mCharacterParser;
	private PinyinComparator mPinyinComparator;
	
	//current is download list or undowload list
	private int mCurrentTabPosition = 0;

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
				mPlayBarTitleView.setText(mCurrentPlayStoryInfo.getTitle());
				mPlayBarView.setVisibility(View.VISIBLE);
				mPlayPauseBtn.setVisibility(View.VISIBLE);
				mPlayPauseBtn.setImageResource(R.drawable.pause);
				
				int duration = mMusicPlayerService.getDuration();
				System.out.println(duration);
				mProgressBar.setMax(duration);
				mPlayBarMaxTimeView.setText(Utils.mediaTimeFormat(duration));
				
				updateProgressBar();
			} else if (action.equals(MusicPlayerService.PLAY_COMPLETED)) {
				mPlayPauseBtn.setImageResource(R.drawable.play);
			} else if (action.equals(MusicPlayerService.PLAYER_NOT_PREPARE)) {
//				StoryInfo info = mList.get(0);
				//播放第一个
//				startPlay(info);
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
		Log.d(TAG, "onCreate start");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		//getdata
		mStoryItem = getIntent().getParcelableExtra("storyItem");
		setTitle(mStoryItem.getTypeName());
		
		initView();
		
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
		Log.d(TAG, "onCreate end");
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	private void initView() {
		mCharacterParser = CharacterParser.getInstance();
		mPinyinComparator = new PinyinComparator();
		
		mSideBar = (SideBar) findViewById(R.id.story_sidebar);
		mTipTextView = (TextView) findViewById(R.id.story_tv_tip);
		mSideBar.setTextView(mTipTextView);
		
		//设置右侧触摸监听
		mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = mAdapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					mListView.setSelection(position);
				}

			}
		});
		
		mLoadingBar = (ProgressBar) findViewById(R.id.story_bar_loading);
		
		mListView = (ListView) findViewById(R.id.story_listview);
		mListView.setOnItemClickListener(this);
		mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		mTableTitleView = (TableTitleView) findViewById(R.id.story_table_title);
		mTableTitleView.initTitles(new String[] { "待下载", "已下载"});
		mTableTitleView.setOnTableSelectChangeListener(this);
		
		mPlayPauseBtn = (ImageView) findViewById(R.id.story_iv_playpause);
		mPlayBarTitleView = (TextView) findViewById(R.id.story_tv_title);
		mPlayBarView = findViewById(R.id.story_rl_bottom_bar);
		mProgressBar = (ProgressBar) findViewById(R.id.story_progressbar);
		mPlayBarMaxTimeView = (TextView) findViewById(R.id.story_tv_max_duration);
		mPlayBarCurrentTimeView = (TextView) findViewById(R.id.story_tv_current_duration);
	}
	
	private void getStoryDatas(){
		Log.d(TAG, "getStoryDatas start");
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
		GetDataTask getDataTask = new GetDataTask();
		getDataTask.execute(uri);
		Log.d(TAG, "getStoryDatas end");
	}
	
	
	/**
	 * get story infos from db
	 */
	private class GetDataTask extends AsyncTask<Uri, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadingBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Uri... params) {
			Cursor cursor = getContentResolver().query(params[0], null, null, null, null);
			StoryInfo info = null;
			if (cursor != null && cursor.moveToFirst()) {
				String title = "";
				String fileName = "";
				long size;
				String pinyin;
				String sortLetter;
				String localPath;
				do {
					title = cursor.getString(cursor.getColumnIndex(ItemColums.TITLE));
					fileName = cursor.getString(cursor.getColumnIndex(ItemColums.FILENAME));
					size = cursor.getLong(cursor.getColumnIndex(ItemColums.SIZE));
					
					info = new StoryInfo();
					info.setFolder(mStoryItem.getFolder());
					info.setFileName(fileName);
					info.setTitle(title);
					info.setSize(size);
					localPath = DownloadUtils.getExistStoryLocalPath(
							getApplicationContext(), mStoryItem.getFolder(), fileName);
					info.setLocalPath(localPath);
					
					pinyin = mCharacterParser.getSelling(info.getTitle());
					sortLetter = pinyin.substring(0, 1).toUpperCase(Locale.CHINA);
					
					// 正则表达式，判断首字母是否是英文字母
					if(sortLetter.matches("[A-Z]")){
						info.setSortLetter(sortLetter.toUpperCase(Locale.CHINA));
					}else{
						info.setSortLetter("#");
					}
					
					if (localPath == null || localPath.isEmpty()) {
						mUnDownloadList.add(info);
					} else {
						mDownloadList.add(info);
					}
				} while (cursor.moveToNext());
				
				Collections.sort(mDownloadList, mPinyinComparator);
				Collections.sort(mUnDownloadList, mPinyinComparator);
				
				for(StoryInfo storyInfo : mDownloadList){
					//得到重新排序后每个字所在的位置
					int position = mDownloadList.indexOf(storyInfo);
					storyInfo.setPosition(position);
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mLoadingBar.setVisibility(View.GONE);
			mAdapter = new StoryItemAdapter(StoryItemActivity.this, mUnDownloadList);
			mListView.setAdapter(mAdapter);
			updateTabText();
		}
	}
	
	private void updateTabText(){
		mTableTitleView.setTableTitle(0, "待下载(" + mUnDownloadList.size() + ")");
		mTableTitleView.setTableTitle(1, "已下载(" + mDownloadList.size() + ")");
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
	
	public void startPlay(StoryInfo info){
		Log.d(TAG, "startPlay.info:" + info.getTitle() + ",position:" + info.getPosition());
		mCurrentPlayStoryInfo = info;
		if (info.getLocalPath() == null) {
			return;
		}
		
		mAdapter.setSelect(info.getPosition());
		if (mCurrentTabPosition == 1) {
			mAdapter.notifyDataSetChanged();
		}
		
		mMusicPlayerService.setDataSource(info.getLocalPath());
		mMusicPlayerService.start();
	}
	
	public void playPause(View view) {
		if (mCurrentPlayStoryInfo == null) {
			if (mDownloadList.size() == 0) {
				Toast.makeText(getApplicationContext(), "没有故事可播放，请先下载", Toast.LENGTH_SHORT).show();
			} else {
				startPlay(mDownloadList.get(0));
			}
			return;
		}
		
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
		next = (next >= mDownloadList.size()) ? 0 : next;
		startPlay(mDownloadList.get(next));
	}
	
	public void previous(View view){
		if (mCurrentPlayStoryInfo == null) {
			return;
		}
		int currentPosition = mCurrentPlayStoryInfo.getPosition();
		int pre = currentPosition - 1;
		pre = (pre < 0) ? (mDownloadList.size() - 1) : pre;
		startPlay(mDownloadList.get(pre));
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
				myHandler.sendMessage(myHandler.obtainMessage(StoryListener.MSG_UPDATE_AUDIO_TIME));
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
                    if (DownloadUtils.isDownloading(status)) {
                    	mDownloaDialog.updateProgress(0);
                        if (msg.arg2 < 0) {
                        } else {
                        	int progress = Utils.getProgress(msg.arg1, msg.arg2);
                        	Log.d(TAG, "progress:" + progress);
                        	mDownloaDialog.updateProgress(progress);
                        }
                    } else {
                        if (status == DownloadManager.STATUS_FAILED) {
                        	int reason = mDownloadManagerPro.getReason(mDownloadId);
                        	Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                        	Log.e(TAG, "download failed.reason:" + reason);
                        } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
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
                case StoryListener.MSG_UPDATE_AUDIO_TIME:
                	int progress = mMusicPlayerService.getPosition();
                	mPlayBarCurrentTimeView.setText(Utils.mediaTimeFormat(progress));
                	break;
            }
        }
    }
    
	@Override
	public void onTableSelect(int position) {
		mCurrentTabPosition = position;
		switch (position) {
		case 0:
			mAdapter.updateListView(mUnDownloadList);
			break;
		case 1:
			mAdapter.updateListView(mDownloadList);
			break;
		default:
			break;
		}
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		StoryInfo info = null;
		switch (mCurrentTabPosition) {
		case 0:
			info = mUnDownloadList.get(position);
			showPreDownloadDialog(info);
			break;
		case 1:
			info = mDownloadList.get(position);
			startPlay(info);
			break;
		default:
			break;
		}
	}
	
	private void showPreDownloadDialog(final StoryInfo info){
		if (info.getLocalPath() == null) {
			final NiftyDialogBuilder dialogBuilder = new NiftyDialogBuilder(this, R.style.dialog_untran);
			dialogBuilder.withTitle(info.getTitle())
			.withTitleColor("#000000")
			.withDividerColor("#11000000")
			.withMessage("该故事尚未下载，是否下载\n下载需要50金币\n文件大小：" + Utils.getFormatSize(info.getSize()))
			.isCancelableOnTouchOutside(true) 
			.withDuration(50)
			.withEffect(Effectstype.FadeIn) 
			.withTipMessage(null)
			.withButton1Text("取消") 
			.withButton2Text("下载")
			.setButton1Click(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogBuilder.dismiss();
				}
			})
			.setButton2Click(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//download
					dialogBuilder.dismiss();
					startDownload(info);
				}
			})
			.show();
		}
	}
	
	private void startDownload(StoryInfo info){
		mDownloaDialog = new StoryDownloadDialog(StoryItemActivity.this, info);
		mDownloaDialog.setOnDwonloadOverListener(new OnDownloadOverListener() {
			@Override
			public void downloadOver(StoryInfo info) {
				Log.d(TAG, "startPlay,localpath:" + info.getLocalPath());
				mUnDownloadList.remove(info);
				mDownloadList.add(info);
				
				Collections.sort(mDownloadList, mPinyinComparator);
				Collections.sort(mUnDownloadList, mPinyinComparator);
				mAdapter.notifyDataSetChanged();
				
				//对已下载的进行一下位置确认
				for(StoryInfo storyInfo : mDownloadList){
					//得到重新排序后每个字所在的位置
					int position = mDownloadList.indexOf(storyInfo);
					storyInfo.setPosition(position);
				}
				startPlay(info);
				updateTabText();
			}
		});
		mDownloaDialog.show();
		mDownloadId = DownloadUtils.downloadStorys(StoryItemActivity.this, mDownloadManager, info);
	}
	
	@Override
	protected void onDestroy() {
		unbindService(mPlaybackConnection);
		unregisterReceiver(mPlayerEvtReceiver);
		
		super.onDestroy();
	}
	
}
