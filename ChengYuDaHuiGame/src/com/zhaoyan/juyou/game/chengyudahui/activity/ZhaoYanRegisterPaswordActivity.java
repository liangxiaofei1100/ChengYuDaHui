package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.common.net.NetWorkUtil;
import com.zhaoyan.juyou.account.LoginResultListener;
import com.zhaoyan.juyou.account.RegisterResultListener;
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
import android.widget.EditText;
import android.widget.Toast;

public class ZhaoYanRegisterPaswordActivity extends Activity implements
		OnClickListener {
	public static final String EXTRA_USER_NAME = "user_name";

	public static final String ACTION_REGISTER_LOGIN = "com.zhaoyan.juyou.game.chengyudahui.activity.ZhaoYanRegisterPaswordActivity.ACTION_REGISTER_LOGIN";
	private Context mContext;
	private EditText mPassword1;
	private EditText mPassword2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle("账号注册");
		setContentView(R.layout.zhaoyan_register_password);
		initView();
	}

	private void initView() {
		mPassword1 = (EditText) findViewById(R.id.et_password);
		mPassword2 = (EditText) findViewById(R.id.et_password2);

		Button nextButton = (Button) findViewById(R.id.btn_next);
		nextButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_next:
			next();
			break;

		default:
			break;
		}
	}

	private void next() {
		final String password = mPassword1.getText().toString();
		if (!password.equals(mPassword2.getText().toString())) {
			toast("两次输入密码不一致，请重新输入");
		}
		if (!NetWorkUtil.isNetworkConnected(mContext)) {
			toast("无网络连接");
			return;
		}
		final String userName = getIntent().getStringExtra(EXTRA_USER_NAME);
		ZhaoYanAccountManager.registerZhaoYanAccount(userName, password,
				new RegisterResultListener() {

					@Override
					public void onRegisterSccess(String message) {
						toast("恭喜您，注册成功.");
						login(userName, password);
					}

					@Override
					public void onRegisterFail(String message) {
						toast("注册失败：" + message);
					}
				});
	}

	protected void login(final String userName, final String password) {
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
						// Notify ZhaoYanRegisterActivity
						Intent intent = new Intent(ACTION_REGISTER_LOGIN);
						sendBroadcast(intent);

						launchAccountInfoModify(userName);
						finish();
					}

					@Override
					public void onNetworkError(String message) {
						toast("登录失败：" + "无法连接到服务器");
					}
				});
	}

	protected void launchAccountInfoModify(String userName) {
		Intent intent = new Intent(mContext,
				ZhaoYanRegisterAccoutInfoActivity.class);
		intent.putExtra(ZhaoYanRegisterAccoutInfoActivity.EXTRA_USER_NAME,
				userName);
		startActivity(intent);
	}

	private void toast(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

}
