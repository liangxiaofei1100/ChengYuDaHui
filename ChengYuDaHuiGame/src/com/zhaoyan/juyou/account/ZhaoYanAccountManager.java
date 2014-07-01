package com.zhaoyan.juyou.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.zhaoyan.juyou.account.bae.FindPassword;
import com.zhaoyan.juyou.account.bae.GetUserInfo;
import com.zhaoyan.juyou.account.bae.GoldOperation;
import com.zhaoyan.juyou.account.bae.Login;
import com.zhaoyan.juyou.account.bae.RegisterUser;
import com.zhaoyan.juyou.account.bae.RegisterUserCheckUserName;

public class ZhaoYanAccountManager {

	public static void saveAccountToLocal(Context context,
			ZhaoYanAccount account) {
		// TODO
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"accout", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("username", account.userName);
		editor.putString("password", account.password);
		editor.putString("email", account.email);
		editor.putString("phone", account.phone);
		editor.putInt("gold", account.gold);
		editor.commit();
	}

	public static void deleteLocalAccount(Context context) {
		// TODO
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"accout", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.remove("username");
		editor.remove("password");
		editor.remove("email");
		editor.remove("phone");
		editor.remove("gold");
		editor.commit();
	}

	public static ZhaoYanAccount getAccountFromLocal(Context context) {
		// TODO
		ZhaoYanAccount account = null;
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"accout", Context.MODE_PRIVATE);
		String username = sharedPreferences.getString("username", "");
		String password = sharedPreferences.getString("password", "");
		String email = sharedPreferences.getString("email", "");
		String phone = sharedPreferences.getString("phone", "");
		int gold = sharedPreferences.getInt("gold", 0);
		if (!username.equals("")) {
			account = new ZhaoYanAccount();
			account.userName = username;
			account.password = password;
			account.email = email;
			account.phone = phone;
			account.gold = gold;
		}
		return account;
	}

	public static void updateLocalAccountWithServerAccountInfo(Context context,
			ZhaoYanAccount accountServer) {
		ZhaoYanAccount accountLocal = getAccountFromLocal(context);
		accountLocal.userName = accountServer.userName;
		accountLocal.email = accountServer.email;
		accountLocal.phone = accountServer.phone;
		accountLocal.gold = accountServer.gold;
		saveAccountToLocal(context, accountLocal);
	}

	/**
	 * 
	 * @param account
	 */
	public static void registerCheckZhaoYanAccount(String userName,
			CheckUserNameResultListener listener) {
		RegisterUserCheckUserName checkUserName = new RegisterUserCheckUserName();
		checkUserName.setCheckResultListener(listener);
		checkUserName.checkUserName(userName);
	}

	/**
	 * Register a ZhaoYan account to web server.
	 * 
	 * @param userName
	 * @param password
	 * @param listener
	 */
	public static void registerZhaoYanAccount(String userName, String password,
			RegisterResultListener listener) {
		RegisterUser registerUser = new RegisterUser();
		registerUser.setRegisterResultListener(listener);
		registerUser.registerUser(userName, password);
	}

	/**
	 * Login a ZhaoYan account on web server.
	 * 
	 * @param userName
	 * @param password
	 * @param listener
	 */
	public static void loginZhaoYanAccount(String userName, String password,
			LoginResultListener listener) {
		Login login = new Login();
		login.setLoginResultListener(listener);
		login.login(userName, password);
	}

	/**
	 * Get user info from web server.
	 * 
	 * @param userName
	 * @param listener
	 */
	public static void getZhaoYanAccountInfo(String userName,
			GetUserInfoResultListener listener) {
		GetUserInfo getUserInfo = new GetUserInfo();
		getUserInfo.setGetUserInfoResultListener(listener);
		getUserInfo.getUserInfo(userName);
	}

	public static void findPassword(String userNameOrEmail,
			FindPasswordResultListener listener) {
		FindPassword findPassword = new FindPassword();
		findPassword.setFindPasswordResultListener(listener);
		findPassword.findPassword(userNameOrEmail);
	}

	public static void addGold(String usernameOrEmail, int gold,
			GoldOperationResultListener listener) {
		GoldOperation operation = new GoldOperation();
		operation.setGetUserInfoResultListener(listener);
		operation.addGold(usernameOrEmail, gold);
	}

	public static void subGold(String usernameOrEmail, int gold,
			GoldOperationResultListener listener) {
		GoldOperation operation = new GoldOperation();
		operation.setGetUserInfoResultListener(listener);
		operation.subGold(usernameOrEmail, gold);
	}
}
