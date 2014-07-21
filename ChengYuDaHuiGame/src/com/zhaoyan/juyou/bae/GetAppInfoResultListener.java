package com.zhaoyan.juyou.bae;

public interface GetAppInfoResultListener {

	void onSuccesss(String appInfoJson);

	void onFail(String message);
}
