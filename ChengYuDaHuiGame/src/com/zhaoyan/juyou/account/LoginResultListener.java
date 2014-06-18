package com.zhaoyan.juyou.account;

public interface LoginResultListener {
	void onLoginSuccess(String message, ZhaoYanAccount account);

	void onLoginFail(String message);
	
	void onNetworkError(String message);
}
