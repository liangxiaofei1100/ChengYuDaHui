package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.common.net.NetWorkUtil;
import com.zhaoyan.juyou.account.ModifyAccountInfoResultListener;
import com.zhaoyan.juyou.account.ZhaoYanAccount;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.game.chengyudahui.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ZhaoYanModifyPaswordActivity extends Activity implements
		OnClickListener {
	private Context mContext;
	private EditText mOriginPassword;
	private EditText mNewPassword;
	private EditText mNewPasswordAgain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle("修改密码");
		setContentView(R.layout.zhaoyan_modify_password);
		initView();
		checkQuickRegisterAccount();
	}

	private void checkQuickRegisterAccount() {
		ZhaoYanAccount account = ZhaoYanAccountManager
				.getAccountFromLocal(mContext);
		if (ZhaoYanAccountManager.isQuickRegisterAccount(mContext, account)) {
			View view = findViewById(R.id.ll_password);
			view.setVisibility(View.GONE);
			mOriginPassword.setText(account.password);
		}
	}

	private void initView() {
		mOriginPassword = (EditText) findViewById(R.id.et_original_password);
		mNewPassword = (EditText) findViewById(R.id.et_new_password);
		mNewPasswordAgain = (EditText) findViewById(R.id.et_new_password_agin);

		Button modifyButton = (Button) findViewById(R.id.btn_modify_password);
		modifyButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_modify_password:
			modifyPassword();
			break;

		default:
			break;
		}
	}

	private void modifyPassword() {
		final String originalPassword = mOriginPassword.getText().toString();
		if (TextUtils.isEmpty(originalPassword)) {
			toast("请输入原密码。");
			return;
		}

		final String password = mNewPassword.getText().toString();
		if (!password.equals(mNewPasswordAgain.getText().toString())) {
			toast("两次输入密码不一致，请重新输入");
		}
		if (!NetWorkUtil.isNetworkConnected(mContext)) {
			toast("无网络连接");
			return;
		}
		final ZhaoYanAccount account = ZhaoYanAccountManager
				.getAccountFromLocal(mContext);
		if (account == null) {
			toast("修改密码失败，未登录。");
			return;
		}

		ZhaoYanAccountManager.modifyPassword(account.userName,
				originalPassword, password,
				new ModifyAccountInfoResultListener() {

					@Override
					public void onSuccess(String message) {
						toast(message);
						account.password = password;
						ZhaoYanAccountManager.saveAccountToLocal(mContext,
								account);
						finish();
					}

					@Override
					public void onFail(String message) {
						toast(message);
					}
				});
	}

	private void toast(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

}
