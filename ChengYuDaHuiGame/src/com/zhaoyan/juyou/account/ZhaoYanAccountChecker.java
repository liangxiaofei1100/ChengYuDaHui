package com.zhaoyan.juyou.account;

import android.text.TextUtils;

public class ZhaoYanAccountChecker {
	public static final int ACCOUNT_MAX_LENGTH = 30;
	public static final int PASSWORD_MAX_LENGTH = 30;

	public static CheckResult checkPassword(String password) {
		CheckResult result = new CheckResult();
		result.checkOK = true;
		if (TextUtils.isEmpty(password)) {
			result.message = "请输入密码";
			result.checkOK = false;
		} else if (password.length() > PASSWORD_MAX_LENGTH) {
			result.message = "密码长度过长";
			result.checkOK = false;
		}
		return result;
	}

	public static CheckResult checkUserName(String userName) {
		CheckResult result = new CheckResult();

		result.checkOK = true;
		if (TextUtils.isEmpty(userName)) {
			result.message = "请输入账号";
			result.checkOK = false;
		} else if (userName.length() > ACCOUNT_MAX_LENGTH) {
			result.message = "账号长度过长";
			result.checkOK = false;
		}
		return result;
	}

	public static class CheckResult {
		public boolean checkOK = false;
		public String message = "";
	}
}
