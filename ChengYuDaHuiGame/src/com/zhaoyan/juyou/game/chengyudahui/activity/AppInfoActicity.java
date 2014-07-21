package com.zhaoyan.juyou.game.chengyudahui.activity;

import java.io.File;
import java.text.DecimalFormat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;

public class AppInfoActicity extends Activity implements OnClickListener {
	private static final String TAG = "AppInfoActicity";
	
	private AsyncImageView mAppIconView;
	private TextView mAppNameView,mAppVersionView,mAppSizeView;
	private TextView mJiFenView, mAppIntroduceView;
	private AsyncImageView mJieMianView1,mJieMianView2;
	private TextView mAuthorView;
	
	private SubmitProcessButton mDLButton;
	
	private AppInfo mAppInfo;
	
	private DownloadManager mDownloadManager;
	private DownloadManagerPro mDownloadManagerPro;
	public static final String KEY_NAME_DOWNLOAD_ID = "downloadId";
	
	private DownloadChangeObserver mDownloadChangeObserver;
    private CompleteReceiver mCompleteReceiver;
    
    private MyHandler myHandler;
    
    private long mDownloadId = 0;
    
    private static final String URL_EX = "http://bcs.duapp.com/bccd1eyw7zka7zmyudununkbfgmbaas/";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appinfo);
		
		Intent intent = getIntent();
		if (intent != null) {
			Log.d(TAG, "intent is not null");
			mAppInfo = intent.getParcelableExtra("appinfo");
		} else {
			Log.e(TAG, "intent is null");
		}
		
		if (mAppInfo != null) {
			Log.d(TAG, mAppInfo.toString());
		} else {
			Log.e(TAG, "mAppInfo is null");
		}
		
		initView();
		
		myHandler = new MyHandler();
		
		mDownloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
		mDownloadManagerPro = new DownloadManagerPro(mDownloadManager);
		
		mDownloadId = PreferencesUtils.getLong(getApplicationContext(), KEY_NAME_DOWNLOAD_ID);
		
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
		
		mDLButton = (SubmitProcessButton) findViewById(R.id.btn_d);
		mDLButton.setOnClickListener(this);
		mDLButton.setCompleteText("安装");
		
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
				mDLButton.setText("安装");
				break;
			case Conf.INSTALLED:
				mDLButton.setText("打开");
				break;
			case Conf.NEED_UDPATE:
				mDLButton.setText("更新");
				break;
			case Conf.NOT_DOWNLOAD:
				mDLButton.setText("下载");
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
		int status = mAppInfo.getStatus();
		switch (status) {
		case Conf.DOWNLOADED:
			String localPath = PreferencesUtils.getString(getApplicationContext(), mAppInfo.getPackageName(), null);
			if (new File(localPath).exists()) {
				APKUtil.installApp(getApplicationContext(), localPath);
			} else {
				preDownload(mAppInfo);
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
			int progress = mDLButton.getProgress();
			if (progress > 0 && progress < 100) {
				mDLButton.setProgress(0);
				mDLButton.setText("下载");
				mDownloadManager.remove(mDownloadId);
			} else {
				preDownload(mAppInfo);
			}
			break;
		default:
			break;
		}
	}
	
	public void preDownload(AppInfo appInfo){
		String sdCardPathString = Environment.getExternalStorageDirectory().getPath();
		if (!new File(sdCardPathString).exists()) {
			new File(sdCardPathString).mkdirs();
		}
		long fileSize = appInfo.getAppSize();
		long aviable = Utils.getAvailableBlockSize(sdCardPathString);
		if (aviable <= fileSize) {
			String fileSizeStr = Utils.getFormatSize(fileSize);
			String availableStr = Utils.getFormatSize(aviable);
			Toast.makeText(getApplicationContext(), "可用空间不足"
					+ "\n" + "文件大小:" + fileSizeStr + "\n"
					+ "可用空间:" + availableStr, Toast.LENGTH_SHORT).show();
			return;
		}
		
		String remotePathString = appInfo.getAppUrl();
		Log.d(TAG, "remotePath:" + (URL_EX + remotePathString));
		Uri downloadUri = Uri.parse(URL_EX + remotePathString);

		int index = remotePathString.lastIndexOf('/');
		String appName = remotePathString.substring(index + 1);
		
		String nativePath = sdCardPathString+Conf.LOCAL_APP_DOWNLOAD_PATH+"/" + appName;
		Log.d(TAG, "nativePath:" + nativePath);
		downloadFile(downloadUri, sdCardPathString + Conf.LOCAL_APP_DOWNLOAD_PATH, appName);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void downloadFile(Uri uri, String dir, String appName) {
		Log.d(TAG, "ready to downloadFile,remote path:" + uri.getPath()
				+ ",local path:" + (dir + "/" +  appName));
		DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Conf.LOCAL_APP_DOWNLOAD_PATH, appName);
        request.setTitle("应用下载:" + appName);
        request.setDescription("zhaoyan desc");
        //下载完毕后，保留通知栏信息
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);
        request.setMimeType("application/com.chenyu.download.file");
        mDownloadId = mDownloadManager.enqueue(request);
        /** save download id to preferences **/
        PreferencesUtils.putLong(getApplicationContext(), KEY_NAME_DOWNLOAD_ID, mDownloadId);
        updateView();
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
                        } else {
                        }
                    }
                    break;
            }
        }
    }
    
    static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");

    public static final int    MB_2_BYTE             = 1024 * 1024;
    public static final int    KB_2_BYTE             = 1024;

    /**
     * @param size
     * @return
     */
    public static CharSequence getAppSize(long size) {
        if (size <= 0) {
            return "0M";
        }

        if (size >= MB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double)size / MB_2_BYTE)).append("M");
        } else if (size >= KB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double)size / KB_2_BYTE)).append("K");
        } else {
            return size + "B";
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
	
}
