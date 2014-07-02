package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.common.net.NetWorkUtil;
import com.zhaoyan.juyou.account.CheckUserNameResultListener;
import com.zhaoyan.juyou.account.ZhaoYanAccountChecker;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.game.chengyudahui.R;

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
import android.widget.Toast;

public class ZhaoYanRegisterActivity extends Activity implements
		OnClickListener {
	private Context mContext;
	private EditText mAccountNameEditText;
	private BroadcastReceiver mLoginReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle("账号注册");
		setContentView(R.layout.zhaoyan_register_account);
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

	private void initView() {
		mAccountNameEditText = (EditText) findViewById(R.id.et_account);
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
		final String userName = mAccountNameEditText.getText().toString();
		ZhaoYanAccountChecker.CheckResult checkResult = ZhaoYanAccountChecker
				.checkUserName(userName);
		if (!checkResult.checkOK) {
			toast(checkResult.message);
			return;
		}
		if (!NetWorkUtil.isNetworkConnected(mContext)) {
			toast("无网络连接");
			return;
		}
		ZhaoYanAccountManager.registerCheckZhaoYanAccount(userName,
				new CheckUserNameResultListener() {

					@Override
					public void checkPass(String message) {
						launchSetPassword(userName);
					}

					@Override
					public void checkFail(String message) {
						toast("注册失败：" + message);
					}
				});
	}

	protected void launchSetPassword(String userName) {
		Intent intent = new Intent(mContext,
				ZhaoYanRegisterPaswordActivity.class);
		intent.putExtra(ZhaoYanRegisterPaswordActivity.EXTRA_USER_NAME,
				userName);
		startActivity(intent);
	}

	private void toast(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

	class LoginReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	}

}
