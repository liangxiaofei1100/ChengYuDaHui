package com.zhaoyan.juyou.game.chengyudahui.utils;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class DeviceInfoUtil {

	public static String getIMEI(Context context) {
		String imei = "";
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			imei = tm.getDeviceId();
		}
		return imei;
	}

	public static String getAndroidID(Context context) {
		return Secure
				.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

}
