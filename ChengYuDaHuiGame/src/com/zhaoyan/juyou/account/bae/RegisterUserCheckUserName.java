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

import com.zhaoyan.juyou.account.CheckUserNameResultListener;
import com.zhaoyan.juyou.game.chengyudahui.utils.AsyncTaskUtils;

public class RegisterUserCheckUserName {

	private static final String TAG = RegisterUserCheckUserName.class
			.getSimpleName();
	private CheckUserNameResultListener mListener;
	private static boolean mIsRunning = false;

	public void checkUserName(String username) {
		Log.d(TAG, "mIsRunning = " + mIsRunning);
		if (mIsRunning) {
			return;
		}

		CheckUserNameTask task = new CheckUserNameTask();
		AsyncTaskUtils.execute(task, username);
	}

	public void setCheckResultListener(CheckUserNameResultListener listener) {
		mListener = listener;

	}

	private class CheckUserNameTask extends AsyncTask<String, Void, Boolean> {
		private String mRespondMessage = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mIsRunning = true;
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			Log.d(TAG, "start...");
			boolean result = false;
			String username = arg0[0];
			try {
				URI uri = new URI(BAEHttpUtils.getRegisterURL());
				HttpPost httpPost = new HttpPost(uri);
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("action", "checkUserName"));
				list.add(new BasicNameValuePair("username", username));

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
				Log.e(TAG, "Register error", e);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Log.e(TAG, "Register error", e);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Register error", e);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				Log.e(TAG, "Register error", e);
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Log.d(TAG, "end. result = " + result);
			mIsRunning = false;
			if (mListener == null) {
				return;
			}

			if (result) {
				mListener.checkPass(mRespondMessage);
			} else {
				mListener.checkFail(getRespondMessage(mRespondMessage));
			}
		}
	}

	private String getRespondMessage(String respondMessage) {
		String message = respondMessage;
		if ("User name is already exist.".equals(respondMessage)) {
			message = "账号已存在，请重新输入";
		} else if ("User name is empty!".equals(respondMessage)) {
			message = "请输入账号";
		}

		return message;
	}

}
