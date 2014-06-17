package com.zhaoyan.juyou.game.chengyudahui.bae;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class UserInfoUtils {

	/**
	 * Parse ZhaoYanUser from json string.
	 * 
	 * @param jsonData
	 * @return
	 */
	public static ZhaoYanUser parseUserInfo(String jsonData) {
		ZhaoYanUser user = new ZhaoYanUser();

		JSONTokener jsonParser = new JSONTokener(jsonData);
		JSONObject userJsonObject = null;

		try {
			userJsonObject = (JSONObject) jsonParser.nextValue();
			user.userName = userJsonObject.getString("userName");
			user.email = userJsonObject.getString("email");
			user.phone = userJsonObject.getString("phone");
			user.gold = userJsonObject.getInt("gold");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return user;
	}
}
