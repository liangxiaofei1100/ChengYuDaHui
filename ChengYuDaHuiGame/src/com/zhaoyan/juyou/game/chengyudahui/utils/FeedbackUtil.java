package com.zhaoyan.juyou.game.chengyudahui.utils;

import android.os.AsyncTask;
import android.util.Log;

public class FeedbackUtil {
	private static final String TAG = FeedbackUtil.class.getSimpleName();

	public static interface FeedbackResultListener {
		void onFeedbackResult(boolean isSuccess);
	}

	public static void feedbackChengYuError(final String chengyu,
			final String message, final String username,
			final FeedbackResultListener listener) {
		FeedbackChengYuTask task = new FeedbackChengYuTask(listener);
		AsyncTaskUtils.execute(task, chengyu, message, username);
	}

	private static String getFeedbackChengYuErrorMessage(String chengyu,
			String message, String username) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("我们亲爱的用户 " + username + " 发现了一条成语有错误：<br><br>");
		stringBuilder.append("成语：" + chengyu + "<br><br>");
		stringBuilder.append("问题描述：" + message + "<br><br>");
		return stringBuilder.toString();
	}

	private static class FeedbackChengYuTask extends
			AsyncTask<String, Void, Boolean> {
		private FeedbackResultListener mListener;

		public FeedbackChengYuTask(FeedbackResultListener listener) {
			mListener = listener;
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			String chengyu = arg0[0];
			String message = arg0[1];
			String username = arg0[2];

			SendMailUtils mail = new SendMailUtils();
			mail.setTo("zhaoyantech@163.com");
			mail.setFrom("zhaoyantech_cy@163.com");
			mail.setHost("smtp.163.com");
			mail.setUsername("zhaoyantech_cy@163.com");
			mail.setPassword("zhaoyantech_cy1");
			mail.setSubject("【反馈】【成语大全】成语有错：" + chengyu);
			mail.setContent(getFeedbackChengYuErrorMessage(chengyu, message,
					username));
			boolean result = mail.sendMail();

			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				Log.d(TAG, "feedbackChengYuError success.");
			} else {
				Log.e(TAG, "feedbackChengYuError fail.");
			}

			if (mListener != null) {
				mListener.onFeedbackResult(result);
			}
		}

	}
}
