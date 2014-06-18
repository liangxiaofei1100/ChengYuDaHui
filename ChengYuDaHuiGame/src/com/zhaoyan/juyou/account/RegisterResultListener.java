package com.zhaoyan.juyou.account;

public interface RegisterResultListener {
	void onRegisterSccess(String message);

	void onRegisterFail(String message);
}
