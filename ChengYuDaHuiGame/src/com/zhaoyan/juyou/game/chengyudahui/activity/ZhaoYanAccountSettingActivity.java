package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.juyou.account.ZhaoYanAccount;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.game.chengyudahui.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ZhaoYanAccountSettingActivity extends Activity implements
		OnClickListener {
	private static final String TAG = ZhaoYanAccountSettingActivity.class
			.getSimpleName();

	public static final String ACTION_ACCOUNT_LOGOUT = "com.zhaoyan.juyou.game.chengyudahui.activity.ZhaoYanAccountSettingActivity.ACTION_ACCOUNT_LOGOUT";
	private Context mContext;

	private TextView mUsernameTextView;
	private TextView mGoldTextView;
	private TextView mEmailTextView;
	private TextView mPhoneTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle("账户信息");
		setContentView(R.layout.zhaoyan_account_setting_activity);

		initView();
		updateAccountInfo();

	}

	@Override
	protected void onResume() {
		super.onResume();
		updateAccountInfo();
	}

	private void updateAccountInfo() {
		ZhaoYanAccount account = ZhaoYanAccountManager
				.getAccountFromLocal(mContext);
		if (account == null) {
			return;
		}

		mUsernameTextView.setText("账号：" + account.userName);
		mGoldTextView.setText("金币：" + account.gold);
		mEmailTextView.setText("邮箱：" + account.email);
		mPhoneTextView.setText("电话：" + account.phone);
	}

	private void initView() {
		mUsernameTextView = (TextView) findViewById(R.id.tv_username);
		mUsernameTextView.setOnClickListener(this);

		mGoldTextView = (TextView) findViewById(R.id.tv_gold);
		mEmailTextView = (TextView) findViewById(R.id.tv_email);
		mEmailTextView.setOnClickListener(this);

		mPhoneTextView = (TextView) findViewById(R.id.tv_phone);

		Button logoutButton = (Button) findViewById(R.id.btn_logout);
		logoutButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_logout:
			logout();
			finish();
			break;
		case R.id.tv_username:
			launchModifyPassword();
			break;
		case R.id.tv_email:
			launchModifyEmail();
			break;

		default:
			break;
		}
	}

	private void launchModifyEmail() {
		Intent intent = new Intent(mContext, ZhaoYanModifyEmailActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.activity_right_in, 0);
	}

	private void launchModifyPassword() {
		Intent intent = new Intent(mContext, ZhaoYanModifyPaswordActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.activity_right_in, 0);
	}

	private void logout() {
		ZhaoYanAccountManager.deleteLocalAccount(mContext);
		// Notify GetGoldActivity
		Intent intent = new Intent(ACTION_ACCOUNT_LOGOUT);
		sendBroadcast(intent);

		launchLogin();
	}

	private void launchLogin() {
		Intent intent = new Intent(mContext, ZhaoYanLoginActivity.class);
		startActivity(intent);
	}

}
