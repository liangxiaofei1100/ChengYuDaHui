package com.zhaoyan.juyou.account;

public interface GetUserInfoResultListener {
	void onGetUserInfoSuccess(ZhaoYanAccount user);

	void onGetUserInfoFail(String message);
}
