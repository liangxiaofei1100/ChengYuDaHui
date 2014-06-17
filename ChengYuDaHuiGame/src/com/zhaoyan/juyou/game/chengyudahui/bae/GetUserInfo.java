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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.zhaoyan.juyou.game.chengyudahui.utils.AsyncTaskUtils;

public class GetUserInfo {
	private static final String TAG = GetUserInfo.class.getSimpleName();
	private GetUserInfoResultListener mGetUserInfoResultListener;
	private static boolean mIsRunning = false;

	public void getUserInfo(String username) {
		Log.d(TAG, "mIsRunning = " + mIsRunning);
		if (mIsRunning) {
			return;
		}
		GetUserInfoTask task = new GetUserInfoTask();
		AsyncTaskUtils.execute(task, username);
	}

	public void setGetUserInfoResultListener(GetUserInfoResultListener listener) {
		mGetUserInfoResultListener = listener;

	}

	private class GetUserInfoTask extends AsyncTask<String, Void, Boolean> {
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
				URI uri = new URI(BAEHttpUtils.getGetUserInfoURL());
				HttpPost httpPost = new HttpPost(uri);
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("usernameOrEmail", username));

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
			mIsRunning = false;
			if (mGetUserInfoResultListener == null) {
				return;
			}

			if (result) {
				ZhaoYanUser user = UserInfoUtils.parseUserInfo(mRespondMessage);
				if (TextUtils.isEmpty(user.userName)) {
					mGetUserInfoResultListener.onGetUserInfoFail("读取用户信息错误");
				} else {
					mGetUserInfoResultListener.onGetUserInfoSuccess(user);
				}
			} else {
				mGetUserInfoResultListener
						.onGetUserInfoFail(getRespondMessage(mRespondMessage));
			}
		}
	}

	private String getRespondMessage(String respondMessage) {
		String message = respondMessage;
		if ("User not exist.".equals(respondMessage)) {
			message = "账号不存在";
		}

		return message;
	}

	public interface GetUserInfoResultListener {

		void onGetUserInfoSuccess(ZhaoYanUser user);

		void onGetUserInfoFail(String message);
	}

}
