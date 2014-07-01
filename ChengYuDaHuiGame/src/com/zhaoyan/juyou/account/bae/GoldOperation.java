package com.zhaoyan.juyou.account.bae;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.zhaoyan.juyou.account.GoldOperationResultListener;
import com.zhaoyan.juyou.game.chengyudahui.utils.AsyncTaskUtils;

public class GoldOperation {
	private static final String TAG = GoldOperation.class.getSimpleName();
	private GoldOperationResultListener mListener;

	public void subGold(String usernameOrEmail, int gold) {
		if (gold < 0) {
			throw new IllegalArgumentException("Gold must > 0");
		}
		GoldOperationTask task = new GoldOperationTask();
		AsyncTaskUtils.execute(task, "sub", usernameOrEmail,
				String.valueOf(gold));
	}

	public void addGold(String usernameOrEmail, int gold) {
		GoldOperationTask task = new GoldOperationTask();
		AsyncTaskUtils.execute(task, "add", usernameOrEmail,
				String.valueOf(gold));
	}

	public void setGetUserInfoResultListener(
			GoldOperationResultListener listener) {
		mListener = listener;
	}

	private class GoldOperationTask extends AsyncTask<String, Void, Boolean> {
		private String mRespondMessage = "";

		@Override
		protected Boolean doInBackground(String... arg0) {
			Log.d(TAG, "start...");
			boolean result = false;
			String action = arg0[0];
			String usernameOrEmail = arg0[1];
			String gold = arg0[2];

			try {
				URI uri = new URI(BAEHttpUtils.getGetGoldOperationURL());
				HttpPost httpPost = new HttpPost(uri);
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("action", action));
				list.add(new BasicNameValuePair("usernameOrEmail",
						usernameOrEmail));
				list.add(new BasicNameValuePair("gold", gold));

				httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
				HttpResponse response = new DefaultHttpClient()
						.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == 200) {
					result = true;
				} else {
					result = false;
				}

				HttpEntity entity = response.getEntity();
				mRespondMessage = EntityUtils.toString(entity, HTTP.UTF_8);
				Log.d(TAG, "result = " + result + ", respond = "
						+ mRespondMessage);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				Log.e(TAG, "Login error", e);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Log.e(TAG, "Login error", e);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Login error", e);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				Log.e(TAG, "Login error", e);
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Log.d(TAG, "end. result = " + result);
			mRespondMessage = getRespondMessage(mRespondMessage);

			if (result) {
				mListener.onGoldOperationSuccess(mRespondMessage);
			} else {
				mListener.onGoldOperationFail(mRespondMessage);
			}
		}
	}

	private String getRespondMessage(String respondMessage) {
		String message = respondMessage;
		if ("User not exist.".equals(respondMessage)) {
			message = "操作失败，账号不存在";
		} else if (respondMessage.startsWith("add gold:")) {
			message = "恭喜，成功领取金币："
					+ respondMessage.substring("add gold:".length()) + "个！";
		} else if (respondMessage.equals("add gold fail.")) {
			message = "领取金币失败，服务器错误";
		} else if (respondMessage.startsWith("sub gold:")) {
			message = "使用金币：" + respondMessage.substring("sub gold:".length())
					+ "个。";
		} else if (respondMessage.equals("sub gold fail.")) {
			message = "使用金币失败，服务器错误";
		}

		return message;
	}
}
