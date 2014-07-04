package com.zhaoyan.juyou.game.chengyudahui.utils;

import android.util.Log;

public class FeedbackUtil {
	private static final String TAG = FeedbackUtil.class.getSimpleName();

	public static void feedbackChengYuError(final String chengyu,
			final String message, final String username) {

		Thread thread = new Thread() {
			@Override
			public void run() {
				SendMailUtils mail = new SendMailUtils();
				mail.setTo("zhaoyantech@163.com");
				mail.setFrom("zhaoyantech_cy@163.com");
				mail.setHost("smtp.163.com");
				mail.setUsername("zhaoyantech_cy@163.com");
				mail.setPassword("zhaoyantech_cy1");
				mail.setSubject("【反馈】【成语大全】成语有错：" + chengyu);
				mail.setContent(getFeedbackChengYuErrorMessage(chengyu,
						message, username));
				boolean result = mail.sendMail();
				if (result) {
					Log.d(TAG, "feedbackChengYuError success.");
				} else {
					Log.e(TAG, "feedbackChengYuError fail.");
				}
			}
		};
		thread.start();
	}

	private static String getFeedbackChengYuErrorMessage(String chengyu,
			String message, String username) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("我们亲爱的用户 " + username + " 发现了一条成语有错误：<br><br>");
		stringBuilder.append("成语：" + chengyu + "<br><br>");
		stringBuilder.append("问题描述：" + message + "<br><br>");
		return stringBuilder.toString();
	}
}
