package com.zhaoyan.juyou.game.chengyudahui.invite;

import java.io.File;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.zhaoyan.common.net.NetWorkUtil;
import com.zhaoyan.common.net.WiFiAP;
import com.zhaoyan.common.net.http.HttpShareServer;
import com.zhaoyan.common.util.QRCodeEncoder;
import com.zhaoyan.communication.SocketPort;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.BackgroundMusicBaseActivity;
import com.zhaoyan.juyou.game.chengyudahui.activity.BaseActivity;

public class InviteHttpActivity extends BackgroundMusicBaseActivity  {
	private static final String TAG = "InviteHttpActivity";

	private String WIFI_AP_NAME;

	private TextView mWiFiAPNameTextView;
	private TextView mAddressTextView;
	private ImageView mQuickResponseCodeImageView;
	private Context mContext;

	private static final int HTTP_SHARE_SERVER_PORT = SocketPort.HTTP_SHARE_SERVER_PORT;
	private HttpShareServer mHttpShareServer;

	private Bitmap mQRCodeBitmap;
	private ProgressDialog mZyProgressDialog;
	
	private WifiManager mWifiManager;
	private boolean mIsWifiEnabled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.invite_http);
		setTitle("WiFi邀请");
		mContext = this;
		WIFI_AP_NAME = getString(R.string.app_name);

		initView();
		
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mIsWifiEnabled = mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;

		mZyProgressDialog = new ProgressDialog(mContext);
		mZyProgressDialog.setCanceledOnTouchOutside(false);
		mZyProgressDialog.setMessage(getString(R.string.invite_http_opening));
		mZyProgressDialog.show();

		mHttpShareServer = new HttpShareServer();

		disableWiFiAP();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WiFiAP.ACTION_WIFI_AP_STATE_CHANGED);
		registerReceiver(mWiFiAPBroadcastReceiver, intentFilter);
		enableWiFiAP();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateView();
	}

	private boolean createHttpShareServer() {
		ApplicationInfo packageInfo = getApplicationInfo();
		Uri uri = Uri.fromFile(new File(packageInfo.sourceDir));

		boolean result = mHttpShareServer.createHttpShare(
				getApplicationContext(), HTTP_SHARE_SERVER_PORT, uri);
		if (result) {
			Log.d(TAG, "createHttpShare port: " + HTTP_SHARE_SERVER_PORT
					+ " successs.");
			if (mZyProgressDialog.isShowing()) {
				mZyProgressDialog.dismiss();
			}
		} else {
			Log.e(TAG, "createHttpShare port: " + HTTP_SHARE_SERVER_PORT
					+ " fail.");
		}
		return result;
	}

	private boolean enableWiFiAP() {
		if (NetWorkUtil.isWifiApEnabled(mContext)) {
			return true;
		}
		boolean result = NetWorkUtil.setWifiAPEnabled(getApplicationContext(),
				WIFI_AP_NAME, true);
		if (result) {
			Log.d(TAG, "enableWiFiAP: " + WIFI_AP_NAME + " success.");
		} else {
			Log.e(TAG, "enableWiFiAP: " + WIFI_AP_NAME + " fail.");
		}
		return result;
	}

	private void disableWiFiAP() {
		if (NetWorkUtil.isWifiApEnabled(mContext)) {
			NetWorkUtil.setWifiAPEnabled(mContext, null, false);
		}
	}

	private String getHttpShareUrl() {
		String ipAddress = WiFiAP.IP;
		return "http://" + ipAddress + ":" + HTTP_SHARE_SERVER_PORT;
	}

	private void initView() {
		mWiFiAPNameTextView = (TextView) findViewById(R.id.tv_network_http_share_ap_name);
		mAddressTextView = (TextView) findViewById(R.id.tv_network_http_share_address);
		mQuickResponseCodeImageView = (ImageView) findViewById(R.id.iv_network_http_share);
	}

	private void updateView() {
		mWiFiAPNameTextView.setText(getString(
				R.string.http_share_first_step_tip, WIFI_AP_NAME));

		mAddressTextView.setText(getString(
				R.string.http_share_second_step_tip1, getHttpShareUrl()));
		releaseQRCodeBitmap();
		try {
			mQRCodeBitmap = QRCodeEncoder.createQRCode(getHttpShareUrl(), 350);

			mQuickResponseCodeImageView.setImageBitmap(mQRCodeBitmap);
		} catch (WriterException e) {
			Log.e(TAG, "createQRCode fail." + e);
		}
	}

	private void releaseQRCodeBitmap() {
		if (mQRCodeBitmap != null) {
			mQuickResponseCodeImageView.setImageDrawable(null);
			mQRCodeBitmap.recycle();
			mQRCodeBitmap = null;
		}
	}

	@Override
	protected void onDestroy() {
		releaseQRCodeBitmap();
		mHttpShareServer.stopServer();

		try {
			unregisterReceiver(mWiFiAPBroadcastReceiver);
		} catch (Exception e) {
			// ignore.
		}

		disableWiFiAP();
		super.onDestroy();
		
		if (mIsWifiEnabled) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	private BroadcastReceiver mWiFiAPBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, android.content.Intent intent) {
			String action = intent.getAction();
			if (WiFiAP.ACTION_WIFI_AP_STATE_CHANGED.equals(action)) {
				int wifiApState = intent
						.getIntExtra(WiFiAP.EXTRA_WIFI_AP_STATE,
								WiFiAP.WIFI_AP_STATE_FAILED);
				handleWifiApchanged(wifiApState);
			}
		}

		private void handleWifiApchanged(int state) {
			switch (state) {
			case WiFiAP.WIFI_AP_STATE_ENABLED:
				createHttpShareServer();
				break;
			case WiFiAP.WIFI_AP_STATE_DISABLED:
				enableWiFiAP();
				break;
			default:
				break;
			}
		}
	};

}
