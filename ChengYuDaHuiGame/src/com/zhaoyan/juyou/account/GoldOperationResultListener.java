package com.zhaoyan.juyou.account;

public interface GoldOperationResultListener {
	void onGoldOperationSuccess(String message);

	void onGoldOperationFail(String message);
}
