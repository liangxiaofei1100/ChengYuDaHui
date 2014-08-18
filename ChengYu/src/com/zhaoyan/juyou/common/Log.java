package com.zhaoyan.juyou.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	public static void d(String tag, String message) {
		System.out
				.println("[" + getCurrentTime() + " - " + tag + "]" + message);
	}

	public static void e(String tag, String message) {
		System.err
				.println("[" + getCurrentTime() + " - " + tag + "]" + message);
	}

	private static String getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		return format.format(date);
	}
}
