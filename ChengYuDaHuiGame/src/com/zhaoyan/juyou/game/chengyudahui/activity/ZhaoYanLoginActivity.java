package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.common.net.NetWorkUtil;
import com.zhaoyan.juyou.account.LoginResultListener;
import com.zhaoyan.juyou.account.ZhaoYanAccount;
import com.zhaoyan.juyou.account.ZhaoYanAccountChecker;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.ZhaoYanRegisterActivity.LoginReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ZhaoYanLoginActivity extends Activity implements OnClickListener {
	private static final String TAG = ZhaoYanLoginActivity.class
			.getSimpleName();
	private Context mContext;

	private EditText mAccountEditText;
	private EditText mPasswordEditText;

	private BroadcastReceiver mLoginReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle("登录");
		setContentView(R.layout.zhaoyan_login);

		initView();
		IntentFilter intentFilter = new IntentFilter(
				ZhaoYanRegisterPaswordActivity.ACTION_REGISTER_LOGIN);
		mLoginReceiver = new LoginReceiver();
		registerReceiver(mLoginReceiver, intentFilter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(mLoginReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (ZhaoYanAccountManager.getAccountFromLocal(mContext) != null) {
			// Already login.
			finish();
		}
	}

	private void initView() {
		mAccountEditText = (EditText) findViewById(R.id.et_account);
		mPasswordEditText = (EditText) findViewById(R.id.et_password);
		Button loginButton = (Button) findViewById(R.id.btn_login);
		loginButton.setOnClickListener(this);

		TextView registerTextView = (TextView) findViewById(R.id.tv_register);
		registerTextView.setOnClickListener(this);

		TextView forgetPasswordTextView = (TextView) findViewById(R.id.tv_forget_password);
		forgetPasswordTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_login:
			login();
			break;
		case R.id.tv_register:
			launchZhaoAccountRegister();
			break;
		case R.id.tv_forget_password:
			forgetPasssword();
			break;

		default:
			break;
		}
	}

	private void forgetPasssword() {
		Intent intent = new Intent(mContext, ForgetPasswordActivity.class);
		intent.putExtra(ForgetPasswordActivity.EXTRA_USERNAME, mAccountEditText
				.getText().toString());
		startActivity(intent);
	}

	private void login() {
		final String userName = mAccountEditText.getText().toString();
		final String password = mPasswordEditText.getText().toString();

		ZhaoYanAccountChecker.CheckResult checkResult = ZhaoYanAccountChecker
				.checkUserName(userName);
		if (!checkResult.checkOK) {
			toast(checkResult.message);
			return;
		}
		checkResult = ZhaoYanAccountChecker.checkPassword(password);
		if (!checkResult.checkOK) {
			toast(checkResult.message);
			return;
		}

		if (!NetWorkUtil.isNetworkConnected(mContext)) {
			toast("无网络连接");
			return;
		}

		ZhaoYanAccountManager.loginZhaoYanAccount(userName, password,
				new LoginResultListener() {

					@Override
					public void onLoginFail(String message) {
						toast("登录失败：" + message);
					}

					@Override
					public void onLoginSuccess(String message,
							ZhaoYanAccount account) {
						toast("登录成功：" + message);
						account.password = password;
						ZhaoYanAccountManager.saveAccountToLocal(mContext,
								account);
						launchGetGoldActivity();
						finish();
					}

					@Override
					public void onNetworkError(String message) {
						toast("登录失败：" + "无法连接到服务器");
					}
				});
	}

	private void launchGetGoldActivity() {
		Intent intent = new Intent(mContext, GetGoldActivity.class);
		startActivity(intent);
	}

	private void launchZhaoAccountRegister() {
		Intent intent = new Intent(mContext, ZhaoYanRegisterActivity.class);
		startActivity(intent);
	}

	private void toast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		toast.show();
	}

	class LoginReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	}

}
