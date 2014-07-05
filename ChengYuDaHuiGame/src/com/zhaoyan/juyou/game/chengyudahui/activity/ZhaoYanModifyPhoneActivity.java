package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhaoyan.common.net.NetWorkUtil;
import com.zhaoyan.juyou.account.ModifyAccountInfoResultListener;
import com.zhaoyan.juyou.account.ZhaoYanAccount;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.game.chengyudahui.R;

public class ZhaoYanModifyPhoneActivity extends Activity implements
		OnClickListener {
	private Context mContext;
	private EditText mPasswordEditText;
	private EditText mPhoneEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle("修改电话号码");
		setContentView(R.layout.zhaoyan_modify_phone);
		initView();
		checkQuickRegisterAccount();
	}

	private void checkQuickRegisterAccount() {
		ZhaoYanAccount account = ZhaoYanAccountManager
				.getAccountFromLocal(mContext);
		if (ZhaoYanAccountManager.isQuickRegisterAccount(mContext, account)) {
			View view = findViewById(R.id.ll_password);
			view.setVisibility(View.GONE);
			mPasswordEditText.setText(account.password);
		}
	}

	private void initView() {
		mPasswordEditText = (EditText) findViewById(R.id.et_password);
		mPhoneEditText = (EditText) findViewById(R.id.et_new_phone);

		Button modifyButton = (Button) findViewById(R.id.btn_modify_phone);
		modifyButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_modify_phone:
			modifyPhone();
			break;

		default:
			break;
		}
	}

	private void modifyPhone() {
		final String password = mPasswordEditText.getText().toString();
		if (TextUtils.isEmpty(password)) {
			toast("请输入密码。");
			return;
		}

		final String phone = mPhoneEditText.getText().toString();
		if (TextUtils.isEmpty(phone)) {
			toast("请输入电话号码。");
			return;
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
		ZhaoYanAccountManager.modifyPhone(account.userName, password, phone,
				new ModifyAccountInfoResultListener() {

					@Override
					public void onSuccess(String message) {
						toast(message);
						account.phone = phone;
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
