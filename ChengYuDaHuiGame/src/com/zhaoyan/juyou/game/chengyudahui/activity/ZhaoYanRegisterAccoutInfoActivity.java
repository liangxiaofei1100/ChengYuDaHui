package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.common.net.NetWorkUtil;
import com.zhaoyan.juyou.account.RegisterResultListener;
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
import android.widget.TextView;
import android.widget.Toast;

public class ZhaoYanRegisterAccoutInfoActivity extends Activity implements
		OnClickListener {
	public static final String EXTRA_USER_NAME = "user_name";
	private Context mContext;
	private EditText mEmailEditText;
	private EditText mPhoneEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle("账号注册");
		setContentView(R.layout.zhaoyan_register_ok_account_info_modify);
		initView();
	}

	private void initView() {
		mEmailEditText = (EditText) findViewById(R.id.et_email);
		mPhoneEditText = (EditText) findViewById(R.id.et_phone);

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
		// final String password = mPassword1.getText().toString();
		// if (!password.equals(mPassword2.getText().toString())) {
		// toast("两次输入密码不一致，请重新输入");
		// }
		// if (!NetWorkUtil.isNetworkConnected(mContext)) {
		// toast("无网络连接");
		// return;
		// }
		// final String userName = getIntent().getStringExtra(EXTRA_USER_NAME);
		// ZhaoYanAccountManager.registerZhaoYanAccount(userName, password,
		// new RegisterResultListener() {
		//
		// @Override
		// public void onRegisterSccess(String message) {
		// toast("注册成功.");
		// launchAccountInfoModify(userName);
		// }
		//
		// @Override
		// public void onRegisterFail(String message) {
		// toast("注册失败：" + message);
		// }
		// });
		launchGetGoldActivity();
	}

	private void launchGetGoldActivity() {
		Intent intent = new Intent(mContext, GetGoldActivity.class);
		startActivity(intent);
		finish();
	}

	private void toast(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

}
