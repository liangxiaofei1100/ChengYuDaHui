package com.zhaoyan.juyou.game.chengyudahui.activity;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.angel.devil.view.AsyncImageView;
import com.zhaoyan.common.file.APKUtil;
import com.zhaoyan.common.util.DownloadManagerPro;
import com.zhaoyan.common.util.PreferencesUtils;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.frontia.AppInfo;
import com.zhaoyan.juyou.game.chengyudahui.frontia.Conf;
import com.zhaoyan.juyou.game.chengyudahui.frontia.DownloadUtils;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;

public class AppInfoActicity extends Activity implements OnClickListener {
	private static final String TAG = AppInfoActicity.class.getSimpleName();
	
	private AsyncImageView mAppIconView;
	private TextView mAppNameView,mAppVersionView,mAppSizeView;
	private TextView mJiFenView, mAppIntroduceView;
	private AsyncImageView mJieMianView1,mJieMianView2;
	private TextView mAuthorView;
	
//	private Button mCancelBtn;
	private LinearLayout mDownloadLL;
	private ProgressBar mDLBar;
	private TextView mInfoTV, mInfoTipTV;
	
	private AppInfo mAppInfo;
	
	private DownloadManager mDownloadManager;
	private DownloadManagerPro mDownloadManagerPro;
	
	private DownloadChangeObserver mDownloadChangeObserver;
    private CompleteReceiver mCompleteReceiver;
    
    private MyHandler myHandler;
    
    private long mDownloadId = 0;
    
    private int mItemPosition = -1;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appinfo);
		
		Intent intent = getIntent();
		if (intent != null) {
			Log.d(TAG, "intent is not null");
			mAppInfo = intent.getParcelableExtra("appinfo");
			mItemPosition = intent.getIntExtra(Conf.KEY_NAME_ITEM_POSITION, -1);
		} else {
			Log.e(TAG, "intent is null");
		}
		
		if (mAppInfo != null) {
			Log.d(TAG, mAppInfo.toString());
			if (mAppInfo.getDownloadId() != -1) {
				mDownloadId = mAppInfo.getDownloadId();
			}
		} else {
			Log.e(TAG, "mAppInfo is null");
		}
		
		initView();
		
		myHandler = new MyHandler();
		
		mDownloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
		mDownloadManagerPro = new DownloadManagerPro(mDownloadManager);
		
		mDownloadId = PreferencesUtils.getLong(getApplicationContext(), Conf.KEY_NAME_DOWNLOAD_ID);
		
		updateView();
		
		mDownloadChangeObserver = new DownloadChangeObserver();
        mCompleteReceiver = new CompleteReceiver();
        
        /** register download success broadcast **/
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        registerReceiver(mCompleteReceiver, filter);
		
		 /** observer download change **/
        getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, mDownloadChangeObserver);
	}
	
	private void initView(){
		mAppIconView = (AsyncImageView) findViewById(R.id.iv_app_icon);
		mAppNameView = (TextView) findViewById(R.id.tv_app_name);
		mAppVersionView = (TextView) findViewById(R.id.tv_app_version);
		mAppSizeView = (TextView) findViewById(R.id.tv_app_size);
		mJiFenView = (TextView) findViewById(R.id.tv_app_jifen);
		mAppIntroduceView = (TextView) findViewById(R.id.tv_app_introduce);
		mAuthorView = (TextView) findViewById(R.id.tv_app_author);
		
		mJieMianView1 = (AsyncImageView) findViewById(R.id.iv_app_jiemian1);
		mJieMianView2 = (AsyncImageView) findViewById(R.id.iv_app_jiemian2);
		
		mAppIconView.setDefaultImageResource(R.drawable.ic_launcher);
		
//		mCancelBtn = (Button) findViewById(R.id.ib_cancel);
//		mCancelBtn.setOnClickListener(this);
		
		mDownloadLL = (LinearLayout) findViewById(R.id.ll_info_dl);
		mDownloadLL.setOnClickListener(this);
		mDLBar = (ProgressBar) findViewById(R.id.bar_info_loading);
		mDLBar.setVisibility(View.GONE);
		mInfoTV = (TextView) findViewById(R.id.tv_info_dl);
		mInfoTipTV = (TextView) findViewById(R.id.tv_info_d_tip);
		mInfoTipTV.setVisibility(View.GONE);
		
		if (mAppInfo != null) {
			mAppIconView.setPath(mAppInfo.getIconUrl());
			
			mAppNameView.setText(mAppInfo.getLabel());
			mAppVersionView.setText(getString(R.string.app_version, mAppInfo.getVersion()));
			String size = getString(R.string.app_size, Utils.getFormatSize(mAppInfo.getAppSize()));
			mAppSizeView.setText(size);
			
			mJiFenView.setText(mAppInfo.getGoldInfos());
			
			mAppIntroduceView.setText(mAppInfo.getIntroduce());
			mAuthorView.setText(getString(R.string.app_author, mAppInfo.getAuthor()));
			
			mJieMianView1.setPath(mAppInfo.getJiemianUrl1());
			mJieMianView2.setPath(mAppInfo.getJiemianUrl2());
			mJieMianView1.setDefaultImageResource(R.drawable.sw_downloading_bg);
			mJieMianView2.setDefaultImageResource(R.drawable.sw_downloading_bg);
			
			switch (mAppInfo.getStatus()) {
			case Conf.DOWNLOADED:
				mInfoTV.setText(R.string.install);
				break;
			case Conf.INSTALLED:
				mInfoTV.setText(R.string.open);
				break;
			case Conf.NEED_UDPATE:
				mInfoTV.setText(R.string.update);
				break;
			case Conf.NOT_DOWNLOAD:
				mInfoTV.setText(R.string.download);
				break;

			default:
				break;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mCompleteReceiver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.btn_d:
		case R.id.ll_info_dl:
			int status = mAppInfo.getStatus();
			switch (status) {
			case Conf.DOWNLOADED:
				String localPath = PreferencesUtils.getString(getApplicationContext(), mAppInfo.getPackageName(), null);
				if (new File(localPath).exists()) {
					APKUtil.installApp(getApplicationContext(), localPath);
				} else {
					mAppInfo.setLastStatus(Conf.NOT_DOWNLOAD);
					download(Conf.NOT_DOWNLOAD);
				}
				break;
			case Conf.INSTALLED:
				String packagename = mAppInfo.getPackageName();
				PackageManager pm = getPackageManager();
				Intent intent = pm.getLaunchIntentForPackage(packagename);
				if (null != intent) {
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(),
							"Cannot open this app", Toast.LENGTH_SHORT).show();
				}
				break;
			case Conf.NEED_UDPATE:
			case Conf.NOT_DOWNLOAD:
				mAppInfo.setLastStatus(status);
				download(status);
				break;
			case Conf.DOWNLOADING:
				showCancelDownloadDialog(mAppInfo);
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}
	}
	
	private void download(int lastStatus){
		mInfoTV.setText(R.string.pre_download);
		mDLBar.setVisibility(View.VISIBLE);
//		mCancelBtn.setVisibility(View.VISIBLE);
		mAppInfo.setStatus(Conf.DOWNLOADING);
		mDownloadId = DownloadUtils.downloadApp(getApplicationContext(), mDownloadManager, mAppInfo);
		mAppInfo.setDownloadId(mDownloadId);
		
		Intent intent = new Intent(Conf.ACTION_START_DOWNLOAD);
		intent.putExtra(Conf.KEY_NAME_DOWNLOAD_ID, mDownloadId);
		intent.putExtra(Conf.KEY_NAME_ITEM_POSITION, mItemPosition);
		intent.putExtra(Conf.KEY_NAME_LAST_STATUS, lastStatus);
		sendBroadcast(intent);
		
		updateView();
	}
	
	private void showCancelDownloadDialog(final AppInfo appInfo) {
		AlertDialog.Builder builder = new Builder(AppInfoActicity.this);
		builder.setMessage("将取消下载:" + appInfo.getLabel());
		builder.setTitle("停止下载");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				mDownloadManager.remove(appInfo.getDownloadId());
				Intent intent = new Intent(Conf.ACTION_CANCEL_DOWNLOAD);
				intent.putExtra(Conf.KEY_NAME_DOWNLOAD_ID, mDownloadId);
				if (0 == mAppInfo.getLastStatus()) {
					intent.putExtra(Conf.KEY_NAME_LAST_STATUS, Conf.NOT_DOWNLOAD);
				} else {
					intent.putExtra(Conf.KEY_NAME_LAST_STATUS, mAppInfo.getLastStatus());
				}
				sendBroadcast(intent);
				int lastStatus = appInfo.getLastStatus();
				if (lastStatus == Conf.NEED_UDPATE) {
					mInfoTV.setText(R.string.update);
				} else {
					mInfoTV.setText(R.string.download);
				}
				mDLBar.setVisibility(View.GONE);
				mInfoTipTV.setVisibility(View.GONE);
				
				appInfo.setDownloadId(-1);
				appInfo.setProgressBytes(0);
				appInfo.setPercent(0);
			}
		});
		builder.setNegativeButton("取消", null);
		builder.create().show();
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
	
	class CompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	String action = intent.getAction();
        	if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
        		long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                String localFileName = mDownloadManagerPro.getFileName(mDownloadId);
                Log.d(TAG, "localFileName:" + localFileName);
                Log.d(TAG, "completeDownloadId:" + completeDownloadId + ",mDOwnloadId:" + mDownloadId);
                if (completeDownloadId == mDownloadId) {
//                    initData();
                    updateView();
                    // if download successful, install apk
                    int status = mDownloadManagerPro.getStatusById(mDownloadId);
                    Log.d(TAG, "status:" +  status);
                    if ( status == DownloadManager.STATUS_SUCCESSFUL) {
                    	PreferencesUtils.putString(getApplicationContext(), mAppInfo.getPackageName(), localFileName);
                    	mInfoTV.setText("点击安装");
                    	mDLBar.setVisibility(View.GONE);
                    	mInfoTipTV.setVisibility(View.GONE);
                    	
                    	mAppInfo.setStatus(Conf.DOWNLOADED);
                    	APKUtil.installApp(context, localFileName);
                    }
                }
			} else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
				Log.d(TAG, "Notification clicked");
			}
        }
    };

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
                    	mInfoTV.setText("正在下载:0%");
                    	mInfoTipTV.setVisibility(View.VISIBLE);
                    	mInfoTipTV.setText("(点击取消下载)");
                        if (msg.arg2 < 0) {
                        } else {
                        	int progress = getProgress(msg.arg1, msg.arg2);
                        	Log.d(TAG, "progress:" + progress);
                        	mInfoTV.setText("正在下载:" + progress + "%");
                        }
                    } else {
                        if (status == DownloadManager.STATUS_FAILED) {
                        	int reason = mDownloadManagerPro.getReason(mDownloadId);
                        	Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                        	mInfoTV.setText("重新下载");
                        	mDLBar.setVisibility(View.GONE);
                        	mInfoTipTV.setVisibility(View.GONE);
                        	Log.e(TAG, "download failed.reason:" + reason);
                        } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
//                        	ToastUtils.show(getApplicationContext(), "下载完成");
//                    		mCancelBtn.setVisibility(View.INVISIBLE);
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
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	
    }
	
}
