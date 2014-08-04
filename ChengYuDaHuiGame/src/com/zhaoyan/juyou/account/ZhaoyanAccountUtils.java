package com.zhaoyan.juyou.account;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.zhaoyan.communication.util.Log;

public class ZhaoyanAccountUtils {
	private static final String TAG = ZhaoyanAccountUtils.class.getSimpleName();

	/**
	 * Parse ZhaoYanUser from json string.
	 * 
	 * @param jsonData
	 * @return
	 */
	public static ZhaoYanAccount parseUserInfo(String jsonData) {
		ZhaoYanAccount user = new ZhaoYanAccount();

		JSONTokener jsonParser = new JSONTokener(jsonData);
		JSONObject userJsonObject = null;

		try {
			userJsonObject = (JSONObject) jsonParser.nextValue();
			user.userName = userJsonObject.getString("userName");
			user.email = userJsonObject.getString("email");
			user.phone = userJsonObject.getString("phone");
			user.jifen = userJsonObject.getInt("jifen");
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "parseUserInfo error" + e);
		}

		return user;
	}
}
