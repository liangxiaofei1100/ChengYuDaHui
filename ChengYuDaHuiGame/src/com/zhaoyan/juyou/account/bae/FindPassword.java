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

import com.zhaoyan.juyou.account.FindPasswordResultListener;
import com.zhaoyan.juyou.game.chengyudahui.utils.AsyncTaskUtils;

public class FindPassword {
	private static final String TAG = FindPassword.class.getSimpleName();
	private FindPasswordResultListener mListener;
	private static boolean mIsRunning = false;

	public void findPassword(String username) {
		Log.d(TAG, "mIsRunning = " + mIsRunning);
		if (mIsRunning) {
			return;
		}
		FindPasswordTask task = new FindPasswordTask();
		AsyncTaskUtils.execute(task, username);
	}

	public void setFindPasswordResultListener(
			FindPasswordResultListener listener) {
		mListener = listener;
	}

	private class FindPasswordTask extends AsyncTask<String, Void, Boolean> {
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
				URI uri = new URI(BAEHttpUtils.getGetPasswordURL());
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
				Log.e(TAG, "error. " + e);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Log.e(TAG, "error. " + e);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "error. " + e);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				Log.e(TAG, "error. " + e);
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
			mRespondMessage = getRespondMessage(mRespondMessage);
			if (result) {
				mListener.findPasswordSuccess("找回成功：" + mRespondMessage);
			} else {
				mListener.findPasswordFail("找回失败：" + mRespondMessage);
			}
		}
	}

	private String getRespondMessage(String respondMessage) {
		String message = respondMessage;
		if (respondMessage.startsWith("User not exist.")) {
			message = "账号不存在。";
		} else if ("Email not exist.".equals(respondMessage)) {
			message = "账号未绑定邮箱。";
		} else if ("Send email success.".equals(respondMessage)) {
			message = "已发送到账号绑定邮箱。";
		} else if ("Send email fail".equals(respondMessage)
				|| "Send email again error.".equals(respondMessage)) {
			message = "发送邮件失败。";
		} else if ("Server error.".equals(respondMessage)) {
			message = "服务器错误。";
		}

		return message;
	}
}
