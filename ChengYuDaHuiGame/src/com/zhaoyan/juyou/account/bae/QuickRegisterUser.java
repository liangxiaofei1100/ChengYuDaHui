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

import com.zhaoyan.juyou.account.QuickRegisterUserResultListener;
import com.zhaoyan.juyou.game.chengyudahui.utils.AsyncTaskUtils;

public class QuickRegisterUser {
	private static final String TAG = QuickRegisterUser.class.getSimpleName();
	private QuickRegisterUserResultListener mListener;
	private static boolean mIsRunning = false;

	public void quickRegisterUser(String password) {
		Log.d(TAG, "mIsRunning = " + mIsRunning);
		if (mIsRunning) {
			return;
		}

		QuickRegisterTask task = new QuickRegisterTask();
		AsyncTaskUtils.execute(task, password);
	}

	public void setRegisterResultListener(
			QuickRegisterUserResultListener listener) {
		mListener = listener;
	}

	private class QuickRegisterTask extends AsyncTask<String, Void, Boolean> {
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
			String password = arg0[0];
			try {
				URI uri = new URI(BAEHttpUtils.getQuickRegisterURL());
				HttpPost httpPost = new HttpPost(uri);
				List<NameValuePair> list = new ArrayList<NameValuePair>();
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
				Log.e(TAG, "error", e);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Log.e(TAG, "error", e);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "error", e);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				Log.e(TAG, "error", e);
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
				String username = mRespondMessage.substring("username:"
						.length());
				mListener.onSuccess(username);
			} else {
				mRespondMessage = getRespondMessage(mRespondMessage);
				mListener.onFail(mRespondMessage);
			}
		}
	}

	private String getRespondMessage(String respondMessage) {
		String message = respondMessage;
		if ("Quick register fail.".equals(respondMessage)) {
			message = "注册失败，请重试。";
		}
		return message;
	}

}
