package com.zhaoyan.juyou.account;

public interface QuickRegisterUserResultListener {

	void onSuccess(String username);

	void onFail(String message);
}
