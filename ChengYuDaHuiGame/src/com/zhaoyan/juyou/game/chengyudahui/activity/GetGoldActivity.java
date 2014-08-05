package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.account.GetUserInfoResultListener;
import com.zhaoyan.juyou.account.ZhaoYanAccount;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.frontia.BaiduFrontiaUser;
import com.zhaoyan.juyou.game.chengyudahui.frontia.Conf;

public class GetGoldActivity extends Activity implements OnClickListener {
	private static final String TAG = GetGoldActivity.class.getSimpleName();

	private Context mContext;
	private ZhaoYanAccountManager mZhaoYanAccountManager;

	private TextView mUserNameTextView;
	private TextView mGoldTextView;
	private TextView mJifenTextView;

	private ZhaoYanAccount mCurrentAccount;

	private BroadcastReceiver mLogoutReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle("领取俸禄");
		setContentView(R.layout.get_gold_activity);
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

		Button downloadAppButton = (Button) findViewById(R.id.btn_download_app);
		downloadAppButton.setOnClickListener(this);

		Button logoutButton = (Button) findViewById(R.id.btn_logout);
		logoutButton.setOnClickListener(this);
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
		startActivityForResult(intent, Conf.REQUEST_CODE3);
	}

	private void launchLogin() {
		Intent intent = new Intent(mContext, ZhaoYanLoginActivity.class);
		startActivity(intent);
	}

	private void toast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_download_app:
		case R.id.tv_download_app:
			launchGetApp();
			break;
		case R.id.ll_acount_info:
			launchAccountSetting();
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
}
