package com.zhaoyan.juyou.account.bae;

public class BAEHttpUtils {
	public static final String URL_SEVLET_PATH = "http://zhaoyantech.duapp.com/servlet/";

	public static String getRegisterURL() {
		return URL_SEVLET_PATH + "RegisterAction";
	}

	public static String getQuickRegisterURL() {
		return URL_SEVLET_PATH + "QuickRegisterAction";
	}

	public static String getLoginURL() {
		return URL_SEVLET_PATH + "LoginAction";
	}

	public static String getGetUserInfoURL() {
		return URL_SEVLET_PATH + "GetUserInfoAction";
	}

	public static String getGetPasswordURL() {
		return URL_SEVLET_PATH + "GetPasswordAction";
	}

	public static String getGetGoldOperationURL() {
		return URL_SEVLET_PATH + "GoldAction";
	}

	public static String getModifyAccountInfoURL() {
		return URL_SEVLET_PATH + "ModifyUserInfoAction";
	}
	
	public static String getGetAppInfoURL() {
		return URL_SEVLET_PATH + "Get3rdAppInfoAction";
	}
}
