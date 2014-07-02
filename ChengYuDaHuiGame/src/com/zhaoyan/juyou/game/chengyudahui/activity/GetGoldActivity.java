package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.juyou.account.GetUserInfoResultListener;
import com.zhaoyan.juyou.account.LoginResultListener;
import com.zhaoyan.juyou.account.ZhaoYanAccount;
import com.zhaoyan.juyou.account.ZhaoYanAccountChecker;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.frontia.BaiduFrontiaUser;
import com.zhaoyan.juyou.game.chengyudahui.frontia.Conf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GetGoldActivity extends Activity implements OnClickListener {
	private static final String TAG = GetGoldActivity.class.getSimpleName();

	private static final int REQUEST_LOGIN = 1;

	private Context mContext;
	private ZhaoYanAccountManager mZhaoYanAccountManager;

	private TextView mUserNameTextView;
	private TextView mGoldTextView;

	private ZhaoYanAccount mCurrentAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle("轻松拿金币");
		setContentView(R.layout.get_gold_activity);
		mZhaoYanAccountManager = new ZhaoYanAccountManager();

		initView();
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

	private void initView() {
		mUserNameTextView = (TextView) findViewById(R.id.tv_username);
		mGoldTextView = (TextView) findViewById(R.id.tv_gold);

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
			launchLogin();
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
		mGoldTextView.setText("金币：" + account.gold);
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
		startActivityForResult(intent, REQUEST_LOGIN);
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
		case R.id.btn_logout:
			logout();
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

	private void logout() {
		ZhaoYanAccountManager.deleteLocalAccount(mContext);
		launchLogin();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_LOGIN:
			if (resultCode != RESULT_OK) {
				finish();
			}
			break;

		default:
			break;
		}
	}
}
