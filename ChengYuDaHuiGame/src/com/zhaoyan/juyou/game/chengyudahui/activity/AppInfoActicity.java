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
import android.widget.TextView;
import android.widget.Toast;

import com.angel.devil.view.AsyncImageView;
import com.zhaoyan.common.file.APKUtil;
import com.zhaoyan.common.progressbutton.SubmitProcessButton;
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
	
	private SubmitProcessButton mDLButton;
//	private Button mCancelBtn;
	
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
		
		mDLButton = (SubmitProcessButton) findViewById(R.id.btn_d);
		mDLButton.setOnClickListener(this);
		mDLButton.setCompleteText(getString(R.string.install));
		
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
			
			switch (mAppInfo.getStatus()) {
			case Conf.DOWNLOADED:
				mDLButton.setText(R.string.install);
				break;
			case Conf.INSTALLED:
				mDLButton.setText(R.string.open);
				break;
			case Conf.NEED_UDPATE:
				mDLButton.setText(R.string.update);
				break;
			case Conf.NOT_DOWNLOAD:
				mDLButton.setText(R.string.download);
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
		case R.id.btn_d:
			int status = mAppInfo.getStatus();
			switch (status) {
			case Conf.DOWNLOADED:
				String localPath = PreferencesUtils.getString(getApplicationContext(), mAppInfo.getPackageName(), null);
				if (new File(localPath).exists()) {
					APKUtil.installApp(getApplicationContext(), localPath);
				} else {
					download();
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
				download();
				break;
			case Conf.DOWNLOADING:
				showCancelDownloadDialog(mAppInfo);
				break;
			default:
				break;
			}
			break;
//		case R.id.ib_cancel:
//			mDLButton.setProgress(0);
//			mDLButton.setText("下载");
//			mDownloadManager.remove(mDownloadId);
//			
//			mCancelBtn.setVisibility(View.INVISIBLE);
//			break;

		default:
			break;
		}
	}
	
	private void download(){
		mDLButton.setText(R.string.pre_download);
//		mCancelBtn.setVisibility(View.VISIBLE);
		mAppInfo.setStatus(Conf.DOWNLOADING);
		mDownloadId = DownloadUtils.downloadApp(getApplicationContext(), mDownloadManager, mAppInfo);
		
		Intent intent = new Intent(Conf.ACTION_START_DOWNLOAD);
		intent.putExtra(Conf.KEY_NAME_DOWNLOAD_ID, mDownloadId);
		intent.putExtra(Conf.KEY_NAME_ITEM_POSITION, mItemPosition);
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
				sendBroadcast(intent);
				
				appInfo.setStatus(Conf.NOT_DOWNLOAD);
				appInfo.setDownloadId(-1);
				appInfo.setProgressBytes(0);
				appInfo.setPercent(0);
				mDLButton.setText(R.string.download);
				mDLButton.setProgress(0);
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
                        mDLButton.setLoadingText("0%");
                        if (msg.arg2 < 0) {
                        	mDLButton.setProgress(0);
                        } else {
//                        	String percent = getNotiPercent(msg.arg1, msg.arg2);
                        	int progress = getProgress(msg.arg1, msg.arg2);
                        	Log.d(TAG, "progress:" + progress);
                        	mDLButton.setLoadingText(progress + "%");
                        	mDLButton.setProgress(progress);
                        }
                    } else {
                        if (status == DownloadManager.STATUS_FAILED) {
                        	int reason = mDownloadManagerPro.getReason(mDownloadId);
                        	mDLButton.setErrorText("下载失败");
                        	mDLButton.setError("fail code:" + reason);
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
