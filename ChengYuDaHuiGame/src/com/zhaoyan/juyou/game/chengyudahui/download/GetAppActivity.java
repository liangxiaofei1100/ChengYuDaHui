package com.zhaoyan.juyou.game.chengyudahui.download;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.a.a.a.a.a;
import com.zhaoyan.common.file.APKUtil;
import com.zhaoyan.common.util.DownloadManagerPro;
import com.zhaoyan.common.util.PreferencesUtils;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.account.GoldOperationResultListener;
import com.zhaoyan.juyou.account.ZhaoYanAccount;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.bae.GetAppInfoBae;
import com.zhaoyan.juyou.bae.GetAppInfoResultListener;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;

public class GetAppActivity extends ActionBarActivity implements
		OnItemClickListener {
	private static final String TAG = "GetAppActivity";
	private ListView mListView;

	private GetAppAdapter mAdapter;

	private AppReceiver mAppReceiver = null;

	private DownloadManager mDownloadManager;
	private DownloadManagerPro mDownloadManagerPro;

	private DownloadChangeObserver mDownloadChangeObserver;

	private HashMap<Long, Integer> mIdMaps = new HashMap<Long, Integer>();

	private GetAppListener getAppListener = new GetAppListener() {

		@Override
		public void onCallBack(Bundle bundle) {
			int flag = bundle.getInt(GetAppListener.CALLBACK_FLAG);
			Log.d(TAG, "onCallBack flag:" + flag);

			Message message = mHandler.obtainMessage(flag);
			message.setData(bundle);
			mHandler.removeMessages(flag);
			mHandler.sendMessage(message);
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Log.d(TAG, "msg.what=" + msg.what);
			Bundle bundle = null;
			bundle = msg.getData();
			AppInfo appInfo = null;
			int position = -1;
			if (bundle != null) {
				position = bundle.getInt(GetAppListener.KEY_ITEM_POSITION);
				Log.d(TAG, "handlerMessage.position=" + position);
				appInfo = mAppList.get(position);
			} else {
				Log.v(TAG, "handlerMessage appinfo is null");
			}

			switch (msg.what) {
			case GetAppListener.MSG_STOP_DOWNLOAD:
				showCancelDownloadDialog(appInfo);
				break;
			case GetAppListener.MSG_START_DOWNLOAD:
			case GetAppListener.MSG_UPDATE_APP:
				if (msg.what == GetAppListener.MSG_UPDATE_APP) {
					appInfo.setLastStatus(Conf.NEED_UDPATE);
				} else {
					appInfo.setLastStatus(Conf.NOT_DOWNLOAD);
				}
				appInfo.setStatus(Conf.DOWNLOADING);
				appInfo.setProgressBytes(0);
				appInfo.setPercent(0);

				mAdapter.notifyDataSetChanged();

				download(appInfo, position);
				break;
			case GetAppListener.MSG_UPDATE_UI:
				mAdapter.notifyDataSetChanged();
				break;
			case GetAppListener.MSG_INSTALL_APP:
				String localPath = appInfo.getAppLocalPath();
				Log.d(TAG, "MSG_INSTALL_APP.localPath:" + localPath);
				if (new File(localPath).exists()) {
					APKUtil.installApp(getApplicationContext(), localPath);
				} else {
					appInfo.setLastStatus(Conf.NOT_DOWNLOAD);
					appInfo.setStatus(Conf.DOWNLOADING);
					appInfo.setProgressBytes(0);
					appInfo.setPercent(0);

					mAdapter.notifyDataSetChanged();

					download(appInfo, position);
				}
				break;
			case GetAppListener.MSG_OPEN_APP:
				String packagename = appInfo.getPackageName();
				PackageManager pm = getPackageManager();
				Intent intent = pm.getLaunchIntentForPackage(packagename);
				if (null != intent) {
					startActivity(intent);
					addGold(GetAppActivity.this, 10);
				} else {
					Toast.makeText(getApplicationContext(),
							"Cannot open this app", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	};

	public static void addGold(final Context context, int goldNum) {
		ZhaoYanAccount account = ZhaoYanAccountManager
				.getAccountFromLocal(context);
		if (account == null) {
			Toast.makeText(context, "领取金币失败，请先登录。", Toast.LENGTH_SHORT).show();
			return;
		}
		ZhaoYanAccountManager.addGold(account.userName, goldNum,
				new GoldOperationResultListener() {

					@Override
					public void onGoldOperationSuccess(String message) {
						Toast.makeText(context, message, Toast.LENGTH_SHORT)
								.show();
					}

					@Override
					public void onGoldOperationFail(String message) {
						Toast.makeText(context, message, Toast.LENGTH_SHORT)
								.show();
					}
				});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_app_main);
		setTitle("应用下载");

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mAppReceiver = new AppReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(mAppReceiver, filter);

		IntentFilter filter2 = new IntentFilter();
		filter2.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		filter2.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
		filter2.addAction(Conf.ACTION_CANCEL_DOWNLOAD);
		filter2.addAction(Conf.ACTION_START_DOWNLOAD);
		registerReceiver(mAppReceiver, filter2);

		mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		mDownloadManagerPro = new DownloadManagerPro(mDownloadManager);

		mDownloadChangeObserver = new DownloadChangeObserver();

		mListView = (ListView) findViewById(android.R.id.list);
		ProgressBar loadingBar = (ProgressBar) findViewById(R.id.bar_info_loading);
		mListView.setEmptyView(loadingBar);

		mAdapter = new GetAppAdapter(mAppList, getApplicationContext());
		mListView.setAdapter(mAdapter);
		mAdapter.registerKeyListener(getAppListener);
		queryAppInfos();

		mListView.setOnItemClickListener(this);

		getContentResolver().registerContentObserver(
				DownloadManagerPro.CONTENT_URI, true, mDownloadChangeObserver);
	}

	public void getShareApp(View view) {
		Intent intent = new Intent(this, ShareAppClientActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
	}

	public void stopDownload(AppInfo appInfo) {
		Log.d(TAG, "stopDownload.id:" + appInfo.getDownloadId());
		mDownloadManager.remove(appInfo.getDownloadId());
	}

	private List<AppInfo> mAppList = new ArrayList<AppInfo>();

	/**
	 * query appinfos from cloud
	 */
	public void queryAppInfos() {
		GetAppInfoBae getAppInfoBae = new GetAppInfoBae();
		getAppInfoBae.getAppInfos(new GetAppInfoResultListener() {
			@Override
			public void onSuccesss(String appInfoJson) {
				Log.d(TAG, "query.onSuccess");
				if (appInfoJson == null || appInfoJson.isEmpty()) {
					Toast.makeText(getApplicationContext(),
							"onSuccess.Data is null", Toast.LENGTH_SHORT)
							.show();
				}
				try {
					JSONArray array = new JSONArray(appInfoJson);
					JSONObject jsonObject = null;
					AppInfo appInfo = null;
					for (int i = 0; i < array.length(); i++) {
						jsonObject = array.getJSONObject(i);
						appInfo = AppInfo.parseJson(jsonObject);

						String packagename = appInfo.getPackageName();
						boolean isInstalled = APKUtil.isAppInstalled(
								getApplicationContext(), packagename);
						String serverVersion = appInfo.getVersion();
						String localVersion = APKUtil.getInstalledAppVersion(
								getApplicationContext(), packagename);
						boolean isVersionEqual = serverVersion
								.equals(localVersion);
						Log.d(TAG, "serverVersion:" + serverVersion
								+ ",localVersion:" + localVersion);
						Log.d(TAG, "isVersionEqual:" + isVersionEqual);

						String localPath = PreferencesUtils.getString(
								getApplicationContext(), packagename, null);

						if (isInstalled) {
							if (!isVersionEqual) {
								appInfo.setStatus(Conf.NEED_UDPATE);
							} else {
								appInfo.setStatus(Conf.INSTALLED);
							}
						} else if (localPath != null) {
							appInfo.setStatus(Conf.DOWNLOADED);
							appInfo.setAppLocalPath(localPath);
						} else {
							appInfo.setStatus(Conf.NOT_DOWNLOAD);
						}
						mAppList.add(appInfo);
					}
				} catch (JSONException e) {
					Log.e(TAG, "Json error:" + e.toString());
					e.printStackTrace();
				}
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFail(String message) {
				Log.e(TAG, "query.onFail:" + message);
				Toast.makeText(GetAppActivity.this, "查询失败.errMsg:" + message,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	private void download(AppInfo appInfo, int position) {
		Log.d(TAG, "download.label:" + appInfo.getLabel() + ",position:"
				+ position);
		long downloadId = DownloadUtils.downloadApp(getApplicationContext(),
				mDownloadManager, appInfo);
		appInfo.setDownloadId(downloadId);
		mIdMaps.put(downloadId, position);
	}

	protected void showCancelDownloadDialog(final AppInfo appInfo) {
		AlertDialog.Builder builder = new Builder(GetAppActivity.this);
		builder.setMessage("将取消下载:" + appInfo.getLabel());
		builder.setTitle("停止下载");
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				stopDownload(appInfo);
				int lastStatus = appInfo.getLastStatus();
				if (lastStatus == 0) {
					appInfo.setStatus(Conf.NOT_DOWNLOAD);
				} else {
					appInfo.setStatus(lastStatus);
				}
				appInfo.setDownloadId(-1);
				appInfo.setProgressBytes(0);
				appInfo.setPercent(0);
				mAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		AppInfo appInfo = mAppList.get(position);
		Log.d(TAG, "onItemClick:" + appInfo.getLabel());
		Intent intent = new Intent();
		intent.setClass(GetAppActivity.this, AppInfoActicity.class);
		intent.putExtra("appinfo", appInfo);
		intent.putExtra(Conf.KEY_NAME_ITEM_POSITION, position);
		startActivity(intent);
	}

	class DownloadChangeObserver extends ContentObserver {

		public DownloadChangeObserver() {
			super(mHandler);
		}

		@Override
		public void onChange(boolean selfChange) {
			Log.d(TAG, "onChange");
			updateView();
		}

	}

	public void updateView() {
		int[] bytesAndStatus = null;
		int status = -1;
		long downloadId = -1;
		int position = -1;
		AppInfo appInfo = null;
		int percent = 0;
		long progressBytes = 0;
		Set<Long> keys = mIdMaps.keySet();
		Iterator<Long> iterator = keys.iterator();
		while (iterator.hasNext()) {
			downloadId = iterator.next();
			Log.d(TAG, "downloadid:" + downloadId);
			position = mIdMaps.get(downloadId);
			Log.d(TAG, "position:" + position);
			bytesAndStatus = mDownloadManagerPro.getBytesAndStatus(downloadId);
			appInfo = mAppList.get(position);

			status = bytesAndStatus[2];

			if (DownloadUtils.isDownloading(status)) {
				progressBytes = bytesAndStatus[0];
				percent = Utils.getProgress(bytesAndStatus[0],
						bytesAndStatus[1]);
				appInfo.setProgressBytes(progressBytes);
				appInfo.setPercent(percent);
				mHandler.sendMessage(mHandler
						.obtainMessage(GetAppListener.MSG_UPDATE_UI));
			} else {
				if (status == DownloadManager.STATUS_FAILED) {
					int reason = mDownloadManagerPro.getReason(downloadId);
					Log.e(TAG, "download failed.reason:" + reason);
				} else if (status == DownloadManager.STATUS_SUCCESSFUL) {
					Log.d(TAG, "Download Successful");
				} else {
					Log.d(TAG, "Other status:" + status);
				}
			}
		}
	}

	class AppReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "action:" + action);
			if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
				String packageName = intent.getData().getSchemeSpecificPart();
				for (AppInfo appInfo : mAppList) {
					if (packageName.equals(appInfo.getPackageName())) {
						appInfo.setStatus(Conf.INSTALLED);
						mAdapter.notifyDataSetChanged();
					}
				}
			} else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
				String packageName = intent.getData().getSchemeSpecificPart();
				Log.d(TAG, "action removed :" + packageName);
				for (AppInfo appInfo : mAppList) {
					if (packageName.equals(appInfo.getPackageName())) {
						appInfo.setStatus(Conf.DOWNLOADED);
						mAdapter.notifyDataSetChanged();
					}
				}
			} else if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
				long completeDownloadId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				if (!mIdMaps.containsKey(completeDownloadId)) {
					// if the completeDownloadId is not my id,ignore
					return;
				}
				AppInfo appInfo = mAppList.get(getPosition(completeDownloadId));
				Log.d(TAG, "complteteid" + completeDownloadId);
				String localFileName = mDownloadManagerPro
						.getFileName(completeDownloadId);
				appInfo.setAppLocalPath(localFileName);
				Log.d(TAG, "localFileName:" + localFileName);
				updateView();
				appInfo.setDownloadId(-1);
				removeDownloadId(completeDownloadId);

				// if download successful, install apk
				int status = mDownloadManagerPro
						.getStatusById(completeDownloadId);

				Log.d(TAG, "status:" + status);
				if (status == DownloadManager.STATUS_SUCCESSFUL) {
					PreferencesUtils.putString(getApplicationContext(),
							appInfo.getPackageName(), localFileName);
					appInfo.setStatus(Conf.DOWNLOADED);
					appInfo.setProgressBytes(0);
					appInfo.setPercent(0);
					mHandler.sendMessage(mHandler
							.obtainMessage(GetAppListener.MSG_UPDATE_UI));
					APKUtil.installApp(context, localFileName);
				} else {
					Log.e(TAG, "download complete.status:" + status);
				}
			} else if (DownloadManager.ACTION_NOTIFICATION_CLICKED
					.equals(action)) {
				Log.d(TAG, "Notification clicked");
			} else if (Conf.ACTION_CANCEL_DOWNLOAD.equals(action)) {
				long downloadId = intent.getLongExtra(
						Conf.KEY_NAME_DOWNLOAD_ID, -1);
				Log.d(TAG, "ACTION_CANCEL_DOWNLOAD:" + downloadId);
				int lastStatus = intent.getIntExtra(Conf.KEY_NAME_LAST_STATUS,
						1);
				if (downloadId != -1) {
					int position = getPosition(downloadId);
					AppInfo appInfo = mAppList.get(position);
					appInfo.setStatus(lastStatus);
					appInfo.setDownloadId(-1);
					appInfo.setProgressBytes(0);
					appInfo.setPercent(0);
					removeDownloadId(downloadId);
					mAdapter.notifyDataSetChanged();
				}
			} else if (Conf.ACTION_START_DOWNLOAD.equals(action)) {
				long downloadId = intent.getLongExtra(
						Conf.KEY_NAME_DOWNLOAD_ID, -1);
				int position = intent.getIntExtra(Conf.KEY_NAME_ITEM_POSITION,
						-1);
				Log.d(TAG, "ACTION_START_DOWNLOAD:" + downloadId + ","
						+ position);
				int lastStatus = intent.getIntExtra(Conf.KEY_NAME_LAST_STATUS,
						1);
				if (downloadId != -1 && position != -1) {
					AppInfo appInfo = mAppList.get(position);
					appInfo.setLastStatus(lastStatus);
					appInfo.setStatus(Conf.DOWNLOADING);
					appInfo.setDownloadId(downloadId);
					mIdMaps.put(downloadId, position);

					updateView();
				}
			}
		}
	}

	private int getPosition(long downloadId) {
		Log.d(TAG, "getPosition.id:" + downloadId);
		int postion = -1;
		if (mIdMaps.containsKey(downloadId)) {
			postion = mIdMaps.get(downloadId);
		}
		Log.d(TAG, "getPosition.position:" + postion);
		return postion;
	}

	private void removeDownloadId(long id) {
		if (mIdMaps.containsKey(id)) {
			mIdMaps.remove(id);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mAppReceiver);
		mAdapter.unregisterMyKeyListener(getAppListener);
	}
}
