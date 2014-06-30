package com.zhaoyan.juyou.game.chengyudahui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
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
import android.widget.Toast;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaData;
import com.baidu.frontia.FrontiaFile;
import com.baidu.frontia.FrontiaQuery;
import com.baidu.frontia.api.FrontiaStorage;
import com.baidu.frontia.api.FrontiaStorageListener.DataInfoListener;
import com.baidu.frontia.api.FrontiaStorageListener.DataOperationListener;
import com.baidu.frontia.api.FrontiaStorageListener.FileProgressListener;
import com.baidu.frontia.api.FrontiaStorageListener.FileTransferListener;
import com.zhaoyan.common.file.APKUtil;
import com.zhaoyan.common.util.SharedPreferenceUtil;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.adapter.GetAppAdapter;
import com.zhaoyan.juyou.game.chengyudahui.frontia.AppInfo;
import com.zhaoyan.juyou.game.chengyudahui.frontia.BaiduFrontiaUser;
import com.zhaoyan.juyou.game.chengyudahui.frontia.Conf;
import com.zhaoyan.juyou.game.chengyudahui.frontia.GetAppListener;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;

public class GetAppActivity extends ListActivity implements OnItemClickListener {
	private static final String TAG = "GetAppActivity";
	private ListView listView;
	private ProgressDialog progressDialog;
	
	private GetAppAdapter mAdapter;
	private FrontiaStorage mCloudStorage;
	private String lastDownloadApk;
	private BaiduFrontiaUser user;
	
	private SharedPreferences sp = null;
	
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
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Bundle bundle = null;
			bundle = msg.getData();
			AppInfo appInfo = null;
			if (bundle != null) {
				int position = bundle.getInt(GetAppListener.KEY_ITEM_POSITION);
				Log.d(TAG, "handlerMessage.position=" + position);
				appInfo = appList.get(position);
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
				} else {
					Toast.makeText(getApplicationContext(), "Cannot open this app", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		};
	};

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

		progressDialog = new ProgressDialog(GetAppActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在读取云盘数据，请稍后!");
		progressDialog.setCancelable(false);
		
		// 获取传递过来的用户数据
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		user = (BaiduFrontiaUser) bundle.get("USER");

		listView = getListView();
		
		mAdapter = new GetAppAdapter(appList, getApplicationContext());
		listView.setAdapter(mAdapter);
		mAdapter.registerKeyListener(getAppListener);
		queryAppInfos();
		
		listView.setOnItemClickListener(this);
		
		sp = SharedPreferenceUtil.getSharedPreference(getApplicationContext(), Conf.SHARED_PREFS_NAME);
	}
	
	public void startDownload(AppInfo appInfo){
		String sdCardPathString = Environment.getExternalStorageDirectory().getPath();
		if (!new File(sdCardPathString).exists()) {
			new File(sdCardPathString).mkdirs();
		}
		long filesize = appInfo.getAppSize();
		long aviable = Utils.getAvailableBlockSize(sdCardPathString);
		if (aviable <= filesize) {
			String fileSizeStr = Utils.getFormatSize(filesize);
			String availableStr = Utils.getFormatSize(aviable);
			Toast.makeText(getApplicationContext(), "可用空间不足"
					+ "\n" + "文件大小:" + fileSizeStr + "\n"
					+ "可用空间:" + availableStr, Toast.LENGTH_SHORT).show();
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
	
	public void stopDownload(AppInfo appInfo){
		String sdCardPathString = Environment.getExternalStorageDirectory().getPath();
		
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
	
	private List<AppInfo> appList = new ArrayList<AppInfo>();
	public void queryAppInfos(){
		progressDialog.show();
		FrontiaQuery query = new FrontiaQuery();
		query.equals("AppInfo", "AppInfo");
		
		mCloudStorage.findData(query, new DataInfoListener() {
			
			@Override
			public void onSuccess(List<FrontiaData> arg0) {
				progressDialog.dismiss();
				if (arg0 == null || arg0.size() == 0) {
					Toast.makeText(getApplicationContext(), "onSuccess.Data is null", Toast.LENGTH_SHORT).show();
				}
				
				JSONObject jsonObject;
				for (int i = 0; i < arg0.size(); i++) {
					jsonObject = arg0.get(i).toJSON();
					AppInfo appInfo = AppInfo.parseJson(jsonObject);
					
					boolean isInstalled = APKUtil.isAppInstalled(getApplicationContext(), appInfo.getPackageName());
					String serverVersion = appInfo.getAppVersion();
					String localVersion = APKUtil.getInstalledAppVersion(getApplicationContext(), appInfo.getPackageName());
					boolean isVersionEqual =serverVersion.equals(localVersion);
					Log.d(TAG, "serverVersion:" + serverVersion + ",localVersion:" + localVersion);
					Log.d(TAG, "isVersionEqual:" + isVersionEqual);
					
					String localPath = sp.getString(appInfo.getPackageName(), null);
					
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
					appList.add(appInfo);
				}
				
				mAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Log.e(TAG, "query apps.onFailure.error code:" + arg0 + ".errorMsg:" + arg1);
				Toast.makeText(GetAppActivity.this, "查询失败.errMsg:" + arg1, Toast.LENGTH_LONG).show();
				progressDialog.dismiss();
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
				Log.d(TAG, "downloading..." + bytes);
//				String dlMsg = "正在下载:" + source;
				int percent = (int) (bytes * 100 / total);
				Log.d(TAG, "prev=" + prev + ",percent=" + percent);
				if (prev != percent) {
					appInfo.setProgressBytes(bytes);
					appInfo.setPercent(percent);
					mHandler.sendMessage(mHandler.obtainMessage(GetAppListener.MSG_UPDATE_UI));
					prev = percent;
				}
//				String totalSize = Utils.getFormatSize(total);
//				String dlSized = Utils.getFormatSize(bytes);
//				String dlPercent = bytes * 100 / total + "%";
//				progressDialog.setMessage(dlMsg + "\n"
//										+ "文件大小:" + totalSize + "\n"
//										+ "已下载:" + dlSized + "\n"
//										+ "下载进度:" + dlPercent );
				
			}

		}, new FileTransferListener() {
			@Override
			public void onSuccess(String source, String newTargetName) {
				Log.d(TAG, "======onSuccess======");
				lastDownloadApk = newTargetName;
				Log.d(TAG,"Local File:"+ newTargetName +",Clound File:"+source);
//				showInstallDialog();
				appInfo.setStatus(Conf.DOWNLOADED);
				appInfo.setAppLocalPath(newTargetName);
				mAdapter.notifyDataSetChanged();
				Editor editor = sp.edit();
				editor.putString(appInfo.getPackageName(), newTargetName);
				editor.commit();
				//key:app package name,value:app local file path
				
				APKUtil.installApp(getApplicationContext(), newTargetName);
				//start a thread to update cloud data
				new Thread(new Runnable() {
					@Override
					public void run() {
//						updateDownloadCountData();
					}
				}).start();
			}

			@Override
			public void onFailure(String source, int errCode, String errMsg) {
				progressDialog.dismiss();
				Log.e(TAG, "onFailure.Error:" + source + ",errCode:" + errCode + "errMsg:" + errMsg);
				Toast.makeText(GetAppActivity.this, "应用下载失败！", Toast.LENGTH_LONG).show();
			}

		});
	}
	//

	protected void updateDownloadCountData() {
		// 更新用户上传应用次数数据
		Log.d(TAG, "updateDownloadCountData");
		FrontiaQuery query = new FrontiaQuery();
		query.equals("IMEI", user.imei);

		FrontiaData newData = new FrontiaData();
		newData.put("IMEI", user.imei);
		newData.put("DEVICE", user.device);
		newData.put("OS", user.os);
		newData.put("PHONE", user.phone);
		newData.put("MAIL", user.mail);
		newData.put("NAME", user.name);
		newData.put("PASSWORD", user.password);
		user.downloadAppCount = String.valueOf(Integer.valueOf(user.downloadAppCount) + 1);
		newData.put("DOWNLOAD_COUNT", user.downloadAppCount);
		newData.put("START_COUNT", user.startAppCount);
		newData.put("UPLOAD_COUNT", user.uploadAppCount);
		mCloudStorage.updateData(query, newData, new DataOperationListener() {
			@Override
			public void onSuccess(long count) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("USER", user);
				intent.putExtras(bundle);
				// intent.putExtra("PHONE", et_phone.getText().toString());
				// intent.putExtra("MAIL", et_mail.getText().toString());
				setResult(Conf.RESULT_CODE3, intent);
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				Log.e(TAG, "update cloud user info fail:errCode:" + errCode + ",errMsg:" + errMsg);
				Toast.makeText(GetAppActivity.this, "账户信息更新失败！", Toast.LENGTH_LONG).show();
			}
		});
	}

	protected void showInstallDialog() {
		AlertDialog.Builder builder = new Builder(GetAppActivity.this);
		builder.setMessage("应用已经下载完成，是否安装？");
		builder.setTitle("提示");
		builder.setPositiveButton("安装", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (lastDownloadApk != null) {
					arg0.dismiss();
					Intent intent = new Intent();
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
					File mFile = new File(lastDownloadApk);
					intent.setData(Uri.fromFile(mFile));
					startActivity(intent);
				}
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				arg0.dismiss();
			}
		});
		builder.create().show();
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
		// TODO Auto-generated method stub
		
	}
	
	class AppReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "action:" + action);
	        if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {   
	            String packageName = intent.getData().getSchemeSpecificPart(); 
	            for(AppInfo appInfo : appList){
	            	if (packageName.equals(appInfo.getPackageName())) {
						appInfo.setStatus(Conf.INSTALLED);
						mAdapter.notifyDataSetChanged();
					}
	            }
	        } else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {   
	        	String packageName = intent.getData().getSchemeSpecificPart(); 
	            Log.d(TAG, "action removed :" + packageName);
	            for(AppInfo appInfo : appList){
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
