package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.disklrucache.Util;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.account.GetUserInfoResultListener;
import com.zhaoyan.juyou.account.ZhaoYanAccount;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.ZyData.SignInColumns;
import com.zhaoyan.juyou.game.chengyudahui.download.BaiduFrontiaUser;
import com.zhaoyan.juyou.game.chengyudahui.download.Conf;
import com.zhaoyan.juyou.game.chengyudahui.download.GetAppActivity;
import com.zhaoyan.juyou.game.chengyudahui.download.ShareAppActivity;
import com.zhaoyan.juyou.game.chengyudahui.download.ShareAppClientActivity;
import com.zhaoyan.juyou.game.chengyudahui.invite.InviteBluetoothActivity;
import com.zhaoyan.juyou.game.chengyudahui.invite.InviteHttpActivity;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;

public class GetGoldActivity extends BackgroundMusicBaseActivity implements
		OnClickListener {
	private static final String TAG = GetGoldActivity.class.getSimpleName();

	private Context mContext;
	private ZhaoYanAccountManager mZhaoYanAccountManager;

	private TextView mUserNameTextView;
	private TextView mGoldTextView;
	private TextView mJifenTextView;

	private ZhaoYanAccount mCurrentAccount;

	private BroadcastReceiver mLogoutReceiver;

	private Toast mToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle("领取俸禄");
		setContentView(R.layout.get_gold_activity);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mZhaoYanAccountManager = new ZhaoYanAccountManager();

		initView();

		IntentFilter intentFilter = new IntentFilter(
				ZhaoYanAccountSettingActivity.ACTION_ACCOUNT_LOGOUT);
		mLogoutReceiver = new LogoutReceiver();
		registerReceiver(mLogoutReceiver, intentFilter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		initAccount();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCurrentAccount = ZhaoYanAccountManager.getAccountFromLocal(mContext);
		if (mCurrentAccount != null) {
			updateGoldInfoTextView(mCurrentAccount);
			connectServerToUpdateAccountInfo();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(mLogoutReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		mUserNameTextView = (TextView) findViewById(R.id.tv_username);
		mGoldTextView = (TextView) findViewById(R.id.tv_gold);
		mJifenTextView = (TextView) findViewById(R.id.tv_jifen);

		View accountView = findViewById(R.id.ll_acount_info);
		accountView.setOnClickListener(this);

		View downloadAppView = findViewById(R.id.tv_download_app);
		downloadAppView.setOnClickListener(this);

		View dailySignView = findViewById(R.id.tv_daily_gold);
		dailySignView.setOnClickListener(this);
	}

	private void initAccount() {
		ZhaoYanAccount account = ZhaoYanAccountManager
				.getAccountFromLocal(mContext);
		if (account == null) {
			// User have not login before.
			toast("请先登录");
			launchLogin();
			finish();
		}
	}

	private void connectServerToUpdateAccountInfo() {
		if (mCurrentAccount == null) {
			mCurrentAccount = ZhaoYanAccountManager
					.getAccountFromLocal(mContext);
		}
		ZhaoYanAccountManager.getZhaoYanAccountInfo(mCurrentAccount.userName,
				new GetUserInfoResultListener() {

					@Override
					public void onGetUserInfoSuccess(ZhaoYanAccount user) {
						if (user == null || user.userName == null) {
							Log.e(TAG,
									"onGetUserInfoSuccess error, user or username is empty.");
							return;
						}
						updateLocalAccountInfo(user);
					}

					@Override
					public void onGetUserInfoFail(String message) {
						Log.d(TAG, "onGetUserInfoFail " + message);
					}
				});
	}

	private void updateGoldInfoTextView(ZhaoYanAccount account) {
		if (account == null) {
			mUserNameTextView.setText("请先登录。");
			return;
		}
		mUserNameTextView.setText("账户：" + account.userName);
		mGoldTextView.setText("功名：" + account.gold);
		mJifenTextView.setText("俸禄：" + account.jifen);
	}

	private void updateLocalAccountInfo(ZhaoYanAccount accountServer) {
		ZhaoYanAccountManager.updateLocalAccountWithServerAccountInfo(mContext,
				accountServer);
		updateGoldInfoTextView(accountServer);
	}

	private void launchGetApp() {
		Intent intent = new Intent(mContext, GetAppActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("USER", new BaiduFrontiaUser());
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void launchLogin() {
		Intent intent = new Intent(mContext, ZhaoYanLoginActivity.class);
		startActivity(intent);
	}

	private void toast(String message) {
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
		mToast.show();
	}

	public void shareApp(View view) {
		Intent intent = new Intent(mContext, ShareAppActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
	}
	
	public void inviteWiFi(View view){
		Intent intent = new Intent(mContext, InviteHttpActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
	}
	
	public void inviteBluetooth(View view){
		Intent intent = new Intent(mContext, InviteBluetoothActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_download_app:
			launchGetApp();
			break;
		case R.id.ll_acount_info:
			launchAccountSetting();
			break;
		case R.id.tv_daily_gold:
			long lastSignDate = getLastSignInDate();
			if (lastSignDate != -1) {
				if (!Utils.isToday(lastSignDate)) {
					// sign
					signIn();
					return;
				}
			} else {
				// sign
				signIn();
				return;
			}

			toast("今天已经签过了，请明天再来!");
			break;
		default:
			break;
		}
	}

	private void launchAccountSetting() {
		Intent intent = new Intent(mContext,
				ZhaoYanAccountSettingActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.activity_right_in, 0);
	}

	class LogoutReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	}

	private long getLastSignInDate() {
		long date = -1;
		Cursor cursor = getContentResolver().query(SignInColumns.CONTENT_URI,
				null, null, null, null);

		if (cursor.moveToLast()) {
			date = cursor.getLong(cursor.getColumnIndex(SignInColumns.DATE));
		}

		if (cursor != null) {
			cursor.close();
		}
		return date;
	}

	private void signIn() {
		GetAppActivity.addGold(mContext, 10);
		toast("签到成功!");

		ContentValues values = new ContentValues();
		values.put(SignInColumns.DATE, java.lang.System.currentTimeMillis());
		getContentResolver().insert(SignInColumns.CONTENT_URI, values);
	}
}
