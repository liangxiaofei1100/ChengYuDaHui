package com.zhaoyan.juyou.game.chengyudahui.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {

	public static boolean isServiceRunning(Context context,
			String serviceClassName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = am.getRunningServices(100);
		for (RunningServiceInfo info : list) {
			if (info.service.getClassName().equals(serviceClassName)) {
				return true;
			}
		}
		return false;
	}
}
