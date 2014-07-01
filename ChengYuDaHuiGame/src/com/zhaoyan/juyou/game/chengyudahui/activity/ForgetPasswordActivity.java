package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.common.net.NetWorkUtil;
import com.zhaoyan.juyou.account.FindPasswordResultListener;
import com.zhaoyan.juyou.account.ZhaoYanAccountChecker;
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

public class ForgetPasswordActivity extends Activity implements OnClickListener {
	private static final String TAG = ForgetPasswordActivity.class
			.getSimpleName();

	public static final String EXTRA_USERNAME = "user_name";

	private Context mContext;
	private EditText mAccountNameEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle("密码找回");
		setContentView(R.layout.zhaoyan_forget_password);
		initView();
	}

	private void initView() {
		Intent intent = getIntent();
		String userName = intent.getStringExtra(EXTRA_USERNAME);

		mAccountNameEditText = (EditText) findViewById(R.id.et_account);
		mAccountNameEditText.setText(userName);

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
		ZhaoYanAccountManager.findPassword(userName,
				new FindPasswordResultListener() {

					@Override
					public void findPasswordSuccess(String message) {
						toast(message);
					}

					@Override
					public void findPasswordFail(String message) {
						toast(message);
					}
				});
	}

	private void toast(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

}
