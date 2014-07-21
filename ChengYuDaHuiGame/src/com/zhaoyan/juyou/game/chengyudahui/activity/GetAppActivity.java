package com.zhaoyan.juyou.game.chengyudahui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaFile;
import com.baidu.frontia.api.FrontiaStorage;
import com.baidu.frontia.api.FrontiaStorageListener.FileProgressListener;
import com.baidu.frontia.api.FrontiaStorageListener.FileTransferListener;
import com.zhaoyan.common.file.APKUtil;
import com.zhaoyan.common.util.PreferencesUtils;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.account.GoldOperationResultListener;
import com.zhaoyan.juyou.account.ZhaoYanAccount;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.bae.GetAppInfoBae;
import com.zhaoyan.juyou.bae.GetAppInfoResultListener;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.adapter.GetAppAdapter;
import com.zhaoyan.juyou.game.chengyudahui.frontia.AppInfo;
import com.zhaoyan.juyou.game.chengyudahui.frontia.Conf;
import com.zhaoyan.juyou.game.chengyudahui.frontia.GetAppListener;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;

public class GetAppActivity extends ListActivity implements OnItemClickListener {
	private static final String TAG = "GetAppActivity";
	private ListView mListView;

	private GetAppAdapter mAdapter;
	private FrontiaStorage mCloudStorage;
	private String lastDownloadApk;

	private AppReceiver mAppReceiver = null;

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
			if (bundle != null) {
				int position = bundle.getInt(GetAppListener.KEY_ITEM_POSITION);
				Log.d(TAG, "handlerMessage.position=" + position);
				appInfo = mAppList.get(position);
			} else {
				Log.v(TAG, "handlerMessage appinfo is null");
			}

			switch (msg.what) {
			case GetAppListener.MSG_STOP_DOWNLOAD:
				stopDownload(appInfo);
				appInfo.setStatus(Conf.NOT_DOWNLOAD);
				appInfo.setProgressBytes(0);
				appInfo.setPercent(0);
				break;
			case GetAppListener.MSG_START_DOWNLOAD:
			case GetAppListener.MSG_UPDATE_APP:
				appInfo.setStatus(Conf.DOWNLOADING);
				appInfo.setProgressBytes(0);
				appInfo.setPercent(0);

				mAdapter.notifyDataSetChanged();

				startDownload(appInfo);
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
					appInfo.setStatus(Conf.DOWNLOADING);
					appInfo.setProgressBytes(0);
					appInfo.setPercent(0);

					mAdapter.notifyDataSetChanged();

					startDownload(appInfo);
				}
				break;
			case GetAppListener.MSG_OPEN_APP:
				String packagename = appInfo.getPackageName();
				PackageManager pm = getPackageManager();
				Intent intent = pm.getLaunchIntentForPackage(packagename);
				if (null != intent) {
					startActivity(intent);
					addGold();
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

	private void addGold() {
		ZhaoYanAccount account = ZhaoYanAccountManager.getAccountFromLocal(this);
		if (account == null) {
			Toast.makeText(this, "领取金币失败，请先登录。", Toast.LENGTH_SHORT).show();
			return;
		}
		ZhaoYanAccountManager.addGold(account.userName, 10,
				new GoldOperationResultListener() {

					@Override
					public void onGoldOperationSuccess(String message) {
						Toast.makeText(GetAppActivity.this, message,
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onGoldOperationFail(String message) {
						Toast.makeText(GetAppActivity.this, message,
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_app_main);
		Frontia.init(this.getApplicationContext(), Conf.APIKEY);
		mCloudStorage = Frontia.getStorage();

		setTitle("应用下载");

		mAppReceiver = new AppReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(mAppReceiver, filter);

		mListView = getListView();
		ProgressBar loadingBar = (ProgressBar) findViewById(R.id.bar_info_loading);
		mListView.setEmptyView(loadingBar);

		mAdapter = new GetAppAdapter(mAppList, getApplicationContext());
		mListView.setAdapter(mAdapter);
		mAdapter.registerKeyListener(getAppListener);
		queryAppInfos();

		mListView.setOnItemClickListener(this);
	}

	public void startDownload(AppInfo appInfo) {
		String sdCardPathString = Environment.getExternalStorageDirectory()
				.getPath();
		if (!new File(sdCardPathString).exists()) {
			new File(sdCardPathString).mkdirs();
		}
		long filesize = appInfo.getAppSize();
		long aviable = Utils.getAvailableBlockSize(sdCardPathString);
		if (aviable <= filesize) {
			String fileSizeStr = Utils.getFormatSize(filesize);
			String availableStr = Utils.getFormatSize(aviable);
			Toast.makeText(
					getApplicationContext(),
					"可用空间不足" + "\n" + "文件大小:" + fileSizeStr + "\n" + "可用空间:"
							+ availableStr, Toast.LENGTH_SHORT).show();
			return;
		}

		String remotePathString = appInfo.getAppUrl();
		FrontiaFile file = new FrontiaFile();
		Log.d(TAG, "remotePath:" + remotePathString);
		file.setRemotePath(remotePathString);

		// 首先获得要下载的APP的name,在转换成本地path作为下载后的保存位置
		int index = remotePathString.lastIndexOf('/');// 从路径的末尾倒数第一个'/'作为文件名和路径的分隔符
		String appName = remotePathString.substring(index + 1);// 获得从‘/’字符以后的字串，即文件名；
		
		String localDir = sdCardPathString + Conf.LOCAL_APP_DOWNLOAD_PATH;
		if (!new File(localDir).exists()) {
			new File(localDir).mkdirs();
		}
		
		String nativePath = sdCardPathString+Conf.LOCAL_APP_DOWNLOAD_PATH+"/" + appName;
		Log.d(TAG, "nativePath:" + nativePath);
		file.setNativePath(nativePath);
		downloadFile(appInfo, file);
	}

	public void stopDownload(AppInfo appInfo) {
		String sdCardPathString = Environment.getExternalStorageDirectory()
				.getPath();

		String remotePathString = appInfo.getAppUrl();
		FrontiaFile file = new FrontiaFile();
		Log.d(TAG, "remotePath:" + remotePathString);
		file.setRemotePath(remotePathString);

		int index = remotePathString.lastIndexOf('/');
		String appName = remotePathString.substring(index + 1);
		String nativePath = sdCardPathString + Conf.LOCAL_APP_DOWNLOAD_PATH
				+ "/" + appName;
		Log.d(TAG, "nativePath:" + nativePath);
		file.setNativePath(nativePath);

		mCloudStorage.stopTransferring(file);
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
						boolean isInstalled = APKUtil.isAppInstalled(getApplicationContext(), packagename);
						String serverVersion = appInfo.getVersion();
						String localVersion = APKUtil.getInstalledAppVersion(getApplicationContext(), packagename);
						boolean isVersionEqual =serverVersion.equals(localVersion);
						Log.d(TAG, "serverVersion:" + serverVersion + ",localVersion:" + localVersion);
						Log.d(TAG, "isVersionEqual:" + isVersionEqual);
						
						String localPath = PreferencesUtils.getString(getApplicationContext(), packagename, null);
						
						if (isInstalled ) {
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

	double prev = 0;

	protected void downloadFile(final AppInfo appInfo, final FrontiaFile mFile) {

		Log.d(TAG, "ready to downloadFile,remote path:" + mFile.getRemotePath()
				+ ",local path:" + mFile.getNativePath());
		prev = 0;
		mCloudStorage.downloadFile(mFile, new FileProgressListener() {
			@Override
			public void onProgress(String source, long bytes, long total) {
//				Log.d(TAG, "downloading..." + bytes);
				// String dlMsg = "正在下载:" + source;
				int percent = (int) (bytes * 100 / total);
//				Log.d(TAG, "prev=" + prev + ",percent=" + percent);
				if (prev != percent) {
					appInfo.setProgressBytes(bytes);
					appInfo.setPercent(percent);
					mHandler.sendMessage(mHandler
							.obtainMessage(GetAppListener.MSG_UPDATE_UI));
					prev = percent;
				}
			}

		}, new FileTransferListener() {
			@Override
			public void onSuccess(String source, String newTargetName) {
				Log.d(TAG, "======onSuccess======");
				lastDownloadApk = newTargetName;
				Log.d(TAG, "Local File:" + newTargetName + ",Clound File:"
						+ source);
				// showInstallDialog();
				appInfo.setStatus(Conf.DOWNLOADED);
				appInfo.setAppLocalPath(newTargetName);
				mAdapter.notifyDataSetChanged();
				PreferencesUtils.putString(getApplicationContext(), appInfo.getPackageName(), newTargetName);
				// key:app package name,value:app local file path
				APKUtil.installApp(getApplicationContext(), newTargetName);
			}

			@Override
			public void onFailure(String source, int errCode, String errMsg) {
				Log.e(TAG, "onFailure.Error:" + source + ",errCode:" + errCode
						+ "errMsg:" + errMsg);
				Toast.makeText(GetAppActivity.this, "应用下载失败！",
						Toast.LENGTH_LONG).show();
			}

		});
	}

	protected void showCancelDownloadDialog(final FrontiaFile file) {
		AlertDialog.Builder builder = new Builder(GetAppActivity.this);
		builder.setMessage("确定取消下载:" + file.getRemotePath());
		builder.setTitle("停止下载");
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				mCloudStorage.stopTransferring(file);
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
		startActivity(intent);
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
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mAppReceiver);
	}
}
