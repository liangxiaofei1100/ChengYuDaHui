package com.zhaoyan.juyou.game.chengyudahui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.adapter.GetAppAdapter;
import com.zhaoyan.juyou.game.chengyudahui.frontia.AppInfo;
import com.zhaoyan.juyou.game.chengyudahui.frontia.BaiduFrontiaUser;
import com.zhaoyan.juyou.game.chengyudahui.frontia.Conf;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;

public class GetAppActivity extends ListActivity {
	private static final String TAG = "GetAppActivity";
	private ListView listView;
	private ProgressDialog progressDialog;
	
	private GetAppAdapter mAdapter;
	private FrontiaStorage mCloudStorage;
	private String lastDownloadApk;
	private BaiduFrontiaUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCloudStorage = Frontia.getStorage();
		
		setTitle("应用下载");

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
		queryAppInfos();
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ListView listView = (ListView) arg0;
//				HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(arg2);
				AppInfo appInfo = appList.get(arg2);
				
				String sdCardPathString = Environment.getExternalStorageDirectory().getPath();
				if (!new File(sdCardPathString).exists()) {
					new File(sdCardPathString).mkdirs();
				}
				String fileSizeStr = appInfo.getSize();
				long aviable = Utils.getAvailableBlockSize(sdCardPathString);
				if (aviable <= 1024 * 1024 * 1024) {
//					String fileSizeStr = Utils.getFormatSize(fileSize);
					String availableStr = Utils.getFormatSize(aviable);
					Toast.makeText(getApplicationContext(), "可用空间不足"
							+ "\n" + "文件大小:" + fileSizeStr + "\n"
							+ "可用空间:" + availableStr, Toast.LENGTH_SHORT).show();
					return;
				}
				
				String remotePathString = appInfo.getAppUrl();
				FrontiaFile mFile = new FrontiaFile();
				Log.d(TAG, "remotePath:" + remotePathString);
				mFile.setRemotePath(remotePathString);

				// 首先获得要下载的APP的name,在转换成本地path作为下载后的保存位置
				int index = remotePathString.lastIndexOf('/');// 从路径的末尾倒数第一个'/'作为文件名和路径的分隔符
				String appName = remotePathString.substring(index + 1);// 获得从‘/’字符以后的字串，即文件名；
				
				String nativePath = sdCardPathString+Conf.LOCAL_APP_DOWNLOAD_PATH+"/" + appName;
				Log.d(TAG, "nativePath:" + nativePath);
				mFile.setNativePath(nativePath);
				downloadFile(mFile);
			}
		});
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
					appList.add(appInfo);
				}
				
				mAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Log.e(TAG, "query apps.onFailure.error code:" + arg0 + ".errorMsg:" + arg1);
				progressDialog.dismiss();
			}
		});
	}

	protected void downloadFile(final FrontiaFile mFile) {
		progressDialog.setMessage("正在准备下载……");
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				Log.d(TAG, "onCancel");
				showCancelDownloadDialog(mFile);
			}
		});
		progressDialog.show();
		Log.d(TAG, "ready to downloadFile,remote path:" + mFile.getRemotePath()
				+ ",local path:" + mFile.getNativePath());
		mCloudStorage.downloadFile(mFile, new FileProgressListener() {
			@Override
			public void onProgress(String source, long bytes, long total) {
				Log.d(TAG, "downloading..." + bytes);
				String dlMsg = "正在下载:" + source;
				String totalSize = Utils.getFormatSize(total);
				String dlSized = Utils.getFormatSize(bytes);
				String dlPercent = bytes * 100 / total + "%";
				progressDialog.setMessage(dlMsg + "\n"
										+ "文件大小:" + totalSize + "\n"
										+ "已下载:" + dlSized + "\n"
										+ "下载进度:" + dlPercent );
			}

		}, new FileTransferListener() {
			@Override
			public void onSuccess(String source, String newTargetName) {
				Log.d(TAG, "======onSuccess======");
				lastDownloadApk = newTargetName;
				Log.d(TAG,"Local File:"+ newTargetName +",Clound File:"+source);
				progressDialog.dismiss();
				showInstallDialog();
				//start a thread to update cloud data
				new Thread(new Runnable() {
					@Override
					public void run() {
						updateDownloadCountData();
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
}
