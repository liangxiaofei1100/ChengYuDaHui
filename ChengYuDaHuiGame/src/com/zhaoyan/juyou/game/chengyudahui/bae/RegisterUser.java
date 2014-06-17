package com.zhaoyan.juyou.game.chengyudahui.bae;

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

import com.zhaoyan.juyou.game.chengyudahui.utils.AsyncTaskUtils;

import android.os.AsyncTask;
import android.util.Log;

public class RegisterUser {
	private static final String TAG = RegisterUser.class.getSimpleName();
	private RegisterResultListener mRegisterResultListener;
	private static boolean mIsRunning = false;

	public void registerUser(String username, String password) {
		Log.d(TAG, "mIsRunning = " + mIsRunning);
		if (mIsRunning) {
			return;
		}

		RegisterTask task = new RegisterTask();
		AsyncTaskUtils.execute(task, username, password);
	}

	public void setRegisterResultListener(
			RegisterResultListener registerResultListener) {
		mRegisterResultListener = registerResultListener;

	}

	private class RegisterTask extends AsyncTask<String, Void, Boolean> {
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
			String password = arg0[1];
			try {
				URI uri = new URI(BAEHttpUtils.getRegisterURL());
				HttpPost httpPost = new HttpPost(uri);
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("username", username));
				list.add(new BasicNameValuePair("password", password));

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
			if (mRegisterResultListener == null) {
				return;
			}

			if (result) {
				mRegisterResultListener
						.onRegisterSccess(getRespondMessage(mRespondMessage));
			} else {
				mRegisterResultListener
						.onRegisterFail(getRespondMessage(mRespondMessage));
			}
		}
	}

	private String getRespondMessage(String respondMessage) {
		String message = respondMessage;
		if ("User name is already exist.".equals(respondMessage)) {
			message = "账号已存在";
		} else if ("Unkown error.".equals(respondMessage)) {
			message = "未知错误";
		}

		return message;
	}

	public interface RegisterResultListener {
		void onRegisterSccess(String message);

		void onRegisterFail(String message);
	}

}
