package com.zhaoyan.juyou.account.bae;

public class BAEHttpUtils {
	public static final String URL_SEVLET_PATH = "http://test20140527.duapp.com/servlet/";

	public static String getRegisterURL() {
		return URL_SEVLET_PATH + "RegisterAction";
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
}
