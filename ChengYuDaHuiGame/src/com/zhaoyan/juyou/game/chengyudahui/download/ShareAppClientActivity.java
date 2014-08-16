package com.zhaoyan.juyou.game.chengyudahui.download;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.zhaoyan.common.file.APKUtil;
import com.zhaoyan.communication.FileTransferService;
import com.zhaoyan.communication.SocketCommunicationManager;
import com.zhaoyan.communication.UserInfo;
import com.zhaoyan.communication.UserManager;
import com.zhaoyan.communication.UserManager.OnUserChangedListener;
import com.zhaoyan.communication.ZYConstant;
import com.zhaoyan.communication.connect.ServerConnector;
import com.zhaoyan.communication.ipc.aidl.User;
import com.zhaoyan.communication.provider.ZhaoYanCommunicationData;
import com.zhaoyan.communication.provider.ZhaoYanCommunicationProvider;
import com.zhaoyan.communication.qrcode.SendFileMessage;
import com.zhaoyan.communication.qrcode.ServerInfoMessage;
import com.zhaoyan.communication.search.SearchUtil;
import com.zhaoyan.communication.util.Log;

public class ShareAppClientActivity extends ActionBarActivity implements
		OnUserChangedListener {
	private static final String TAG = ShareAppClientActivity.class
			.getSimpleName();

	private Context mContext;
	private ServerConnector mServerConnector;

	private ProgressDialog mCreateServerDialog;
	private GuanjiaReceiver mGuanjiaReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("接收应用");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mContext = this;

		initView();
		setContentView(new TextView(mContext));
		launchQRCodeScan();

		SocketCommunicationManager.getInstance().closeAllCommunication();
		mServerConnector = ServerConnector.getInstance(mContext);
		UserManager.getInstance().registerOnUserChangedListener(this);

		mCreateServerDialog = new ProgressDialog(mContext);
		mCreateServerDialog.setMessage("正在连接，请稍后。。。");
		mCreateServerDialog.setCanceledOnTouchOutside(false);
		mCreateServerDialog.show();

		IntentFilter filter = new IntentFilter(
				FileTransferService.ACTION_NOTIFY_SEND_OR_RECEIVE);
		mGuanjiaReceiver = new GuanjiaReceiver();
		registerReceiver(mGuanjiaReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		UserManager.getInstance().unregisterOnUserChangedListener(this);
		try {
			unregisterReceiver(mGuanjiaReceiver);
		} catch (Exception e) {
			Log.d(TAG, "unregisterReceiver " + e);
		}
		SearchUtil.clearWifiConnectHistory(mContext);
	}

	private void initView() {

	}

	private void launchQRCodeScan() {
		Intent intent = new Intent(mContext, CaptureActivity.class);
		startActivityForResult(intent, 1);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			String qrcode = data.getStringExtra(CaptureActivity.EXTRA_RESULT);

			SendFileMessage sendFileMessage = new SendFileMessage();
			sendFileMessage.getQRCodeMessage(qrcode);
			UserInfo serverInfo = getUserInfoFromServerInfoMessage(sendFileMessage.serverInfoMessage);
			mServerConnector.connectServer(serverInfo);
		} else {
			finish();
		}
	}

	private UserInfo getUserInfoFromServerInfoMessage(
			ServerInfoMessage serverInfoMessage) {
		String name = "";
		String ip = serverInfoMessage.ip;
		int type = serverInfoMessage.networkType;
		String ssid = serverInfoMessage.ssid;

		UserInfo userInfo = new UserInfo();
		User user = new User();
		user.setUserName(name);
		userInfo.setUser(user);
		switch (type) {
		case ZhaoYanCommunicationData.User.NETWORK_AP:
			userInfo.setType(ZhaoYanCommunicationData.User.TYPE_REMOTE_SEARCH_AP);
			break;
		case ZhaoYanCommunicationData.User.NETWORK_WIFI:
			userInfo.setType(ZhaoYanCommunicationData.User.TYPE_REMOTE_SEARCH_LAN);
			break;

		default:
			break;
		}
		userInfo.setIpAddress(ip);
		userInfo.setSsid(ssid);
		return userInfo;
	}

	@Override
	public void onUserConnected(User user) {
		Log.d(TAG, "onUserConnected");
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mCreateServerDialog.setMessage("已连接，正在文件传输。。。");
			}
		});

	}

	@Override
	public void onUserDisconnected(User user) {

	}

	class GuanjiaReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "onReceive:action=" + action);
			if (mCreateServerDialog.isShowing()) {
				mCreateServerDialog.dismiss();
				Toast.makeText(mContext, "传输完成", Toast.LENGTH_SHORT).show();
				installApk();
				finish();
			}
		}
	}

	private static final String[] PROJECTION = {
			ZhaoYanCommunicationData.History._ID,
			ZhaoYanCommunicationData.History.FILE_PATH,
			ZhaoYanCommunicationData.History.FILE_NAME,
			ZhaoYanCommunicationData.History.FILE_SIZE,
			ZhaoYanCommunicationData.History.SEND_USERNAME,
			ZhaoYanCommunicationData.History.RECEIVE_USERNAME,
			ZhaoYanCommunicationData.History.PROGRESS,
			ZhaoYanCommunicationData.History.DATE,
			ZhaoYanCommunicationData.History.STATUS,
			ZhaoYanCommunicationData.History.MSG_TYPE,
			ZhaoYanCommunicationData.History.FILE_TYPE,
			ZhaoYanCommunicationData.History.FILE_ICON,
			ZhaoYanCommunicationData.History.SEND_USER_HEADID,
			ZhaoYanCommunicationData.History.SEND_USER_ICON };

	private void installApk() {
		String apkFile = null;

		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(
				ZhaoYanCommunicationData.History.CONTENT_URI, PROJECTION, null,
				null, null);
		if (cursor != null) {
			try {
				if (cursor.getCount() > 0 && cursor.moveToLast()) {
					apkFile = cursor
							.getString(cursor
									.getColumnIndex(ZhaoYanCommunicationData.History.FILE_PATH));
				}
			} catch (Exception e) {
				Log.e(TAG, "read cursor error. " + e);
			} finally {
				cursor.close();
			}
		}

		if (apkFile == null) {
			return;
		}

		Log.d(TAG, "apkFile = " + apkFile);
		APKUtil.installApp(getApplicationContext(), apkFile);
	}
}
