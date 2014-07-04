package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.common.net.NetWorkUtil;
import com.zhaoyan.juyou.account.ModifyAccountInfoResultListener;
import com.zhaoyan.juyou.account.ZhaoYanAccount;
import com.zhaoyan.juyou.account.ZhaoYanAccountChecker;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.game.chengyudahui.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ZhaoYanRegisterAccoutInfoActivity extends Activity implements
		OnClickListener {
	public static final String EXTRA_USER_NAME = "user_name";
	private Context mContext;
	private EditText mEmailEditText;

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
		final String email = mEmailEditText.getText().toString();
		if (TextUtils.isEmpty(email)) {
			launchGetGoldActivity();
			finish();
		} else {
			ZhaoYanAccountChecker.CheckResult checkResult = ZhaoYanAccountChecker
					.checkEmail(email);
			if (!checkResult.checkOK) {
				toast(checkResult.message);
				return;
			}
			if (!NetWorkUtil.isNetworkConnected(mContext)) {
				toast("无网络连接");
				return;
			}
			ZhaoYanAccount account = ZhaoYanAccountManager
					.getAccountFromLocal(mContext);
			if (account == null) {
				toast("绑定邮箱失败，请先登录。");
				return;
			}

			ZhaoYanAccountManager.modifyEmail(account.userName,
					account.password, email,
					new ModifyAccountInfoResultListener() {

						@Override
						public void onSuccess(String message) {
							toast(message);
							launchGetGoldActivity();
							finish();
						}

						@Override
						public void onFail(String message) {
							toast(message);
						}
					});
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			launchGetGoldActivity();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void launchGetGoldActivity() {
		Intent intent = new Intent(mContext, GetGoldActivity.class);
		startActivity(intent);
	}

	private void toast(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

}
