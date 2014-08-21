package com.zhaoyan.juyou.game.chengyudahui.download;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.zhaoyan.common.util.QRCodeEncoder;
import com.zhaoyan.communication.FileTransferService;
import com.zhaoyan.communication.SocketPort;
import com.zhaoyan.communication.UserHelper;
import com.zhaoyan.communication.UserInfo;
import com.zhaoyan.communication.UserManager;
import com.zhaoyan.communication.UserManager.OnUserChangedListener;
import com.zhaoyan.communication.ZYConstant.Extra;
import com.zhaoyan.communication.connect.ServerCreator;
import com.zhaoyan.communication.ipc.aidl.User;
import com.zhaoyan.communication.qrcode.SendFileMessage;
import com.zhaoyan.communication.qrcode.ServerInfoMessage;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.BackgroundMusicBaseActivity;
import com.zhaoyan.juyou.game.chengyudahui.activity.BaseActivity;

public class ShareAppServerActivity extends BackgroundMusicBaseActivity implements
		OnUserChangedListener {
	private static final String TAG = ShareAppServerActivity.class
			.getSimpleName();

	public static final String EXTRA_APP_FILE_PATH = "app";

	private ImageView mQRCodeImageView;

	private String mQRCodeContent;
	private Bitmap mQRCodeBitmap;

	private Context mContext;

	private ServerCreator mServerCreator;
	private ProgressDialog mCreateServerDialog;
	private BroadcastReceiver mServerCreateBroadcastReceiver;

	private UserManager mUserManager;
	private WifiManager mWifiManager;

	private boolean mIsWifiEnabled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("应用分享");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mContext = this;

		setContentView(R.layout.qrcode_display);
		initView();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ServerCreator.ACTION_SERVER_CREATED);
		if (mServerCreateBroadcastReceiver == null) {
			mServerCreateBroadcastReceiver = new ServerCreateBroadcastReceiver();
			mContext.registerReceiver(mServerCreateBroadcastReceiver,
					intentFilter);
		}

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mIsWifiEnabled = mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;

		mServerCreator = ServerCreator.getInstance(mContext);
		mServerCreator.createServer(ServerCreator.TYPE_AP);

		mUserManager = UserManager.getInstance();
		mUserManager.registerOnUserChangedListener(this);

		mCreateServerDialog = new ProgressDialog(mContext);
		mCreateServerDialog.setMessage("正在创建连接，请稍后。。。");
		mCreateServerDialog.setCanceledOnTouchOutside(false);
		mCreateServerDialog.show();
	}

	private String getAppFilePath() {
		String filePath = getIntent().getStringExtra(EXTRA_APP_FILE_PATH);
		return filePath;
	}

	private void updateQRcode() {
		UserInfo userInfo = UserHelper.getServerUserInfo(mContext);
		String ip = userInfo.getIpAddress();
		String ssid = userInfo.getSsid();

		ServerInfoMessage serverInfoMessage = new ServerInfoMessage(ssid, ip,
				SocketPort.COMMUNICATION_SERVER_PORT, userInfo.getNetworkType());
		SendFileMessage sendFileMessage = new SendFileMessage(serverInfoMessage);

		String qrcode = sendFileMessage.getQRCodeString();
		mQRCodeContent = qrcode;

		if (mQRCodeBitmap == null) {
			try {
				mQRCodeBitmap = QRCodeEncoder.createQRCode(mQRCodeContent, 350);
				mQRCodeImageView.setImageBitmap(mQRCodeBitmap);
			} catch (WriterException e) {
				Log.e(TAG, "create qrcode error " + e);
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseQRCodeBitmap();

		try {
			mContext.unregisterReceiver(mServerCreateBroadcastReceiver);
		} catch (Exception e) {
			Log.e(TAG, "onDestroyView " + e);
		}

		mServerCreator.stopServer();
		mUserManager.unregisterOnUserChangedListener(this);

		if (mIsWifiEnabled) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	private void initView() {
		mQRCodeImageView = (ImageView) findViewById(R.id.iv_qrcode);
	}

	private void releaseQRCodeBitmap() {
		if (mQRCodeBitmap != null) {
			mQRCodeImageView.setImageDrawable(null);
			mQRCodeBitmap.recycle();
			mQRCodeBitmap = null;
		}
	}

	private class ServerCreateBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ServerCreator.ACTION_SERVER_CREATED.equals(action)) {
				Log.d(TAG, "ACTION_SERVER_CREATED");
				updateQRcode();
				if (mCreateServerDialog.isShowing()) {
					mCreateServerDialog.dismiss();
				}

			}
		}
	}

	@Override
	public void onUserConnected(User user) {
		Log.d(TAG, "onUserConnected id = " + user.getUserID() + ",name = "
				+ user.getUserName());
		if (user.getUserID() == -1) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction(FileTransferService.ACTION_SEND_FILE);

		ArrayList<String> files = new ArrayList<String>();
		files.add(getAppFilePath());
		intent.putStringArrayListExtra(Extra.SEND_FILES, files);

		ArrayList<User> users = new ArrayList<User>();
		users.add(user);
		intent.putParcelableArrayListExtra(Extra.SEND_USERS, users);

		sendBroadcast(intent);
	}

	@Override
	public void onUserDisconnected(User user) {
		Log.d(TAG, "onUserDisconnected " + user.getUserName());
	}
}
