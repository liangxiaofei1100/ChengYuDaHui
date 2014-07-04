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

import com.zhaoyan.juyou.account.ModifyAccountInfoResultListener;
import com.zhaoyan.juyou.game.chengyudahui.utils.AsyncTaskUtils;

public class ModifyAccountInfo {
	private static final String TAG = ModifyAccountInfo.class.getSimpleName();
	private ModifyAccountInfoResultListener mListener;

	public void modifyEmail(String username, String password, String email) {
		ModifyAccountInfoTask task = new ModifyAccountInfoTask();
		AsyncTaskUtils.execute(task, "email", username, password, email);
	}

	public void modifyPhone(String username, String password, String phone) {
		ModifyAccountInfoTask task = new ModifyAccountInfoTask();
		AsyncTaskUtils.execute(task, "phone", username, password, phone);
	}

	public void modifyPassword(String username, String password,
			String newPassword) {
		ModifyAccountInfoTask task = new ModifyAccountInfoTask();
		AsyncTaskUtils.execute(task, "password", username, password,
				newPassword);
	}

	public void setModiyAccountInfoResultListener(
			ModifyAccountInfoResultListener listener) {
		mListener = listener;
	}

	private class ModifyAccountInfoTask extends
			AsyncTask<String, Void, Boolean> {
		private String mRespondMessage = "";

		protected Boolean doInBackground(String... arg0) {
			Log.d(TAG, "start...");
			boolean result = false;
			String action = arg0[0];
			String username = arg0[1];
			String password = arg0[2];
			try {
				URI uri = new URI(BAEHttpUtils.getModifyAccountInfoURL());
				HttpPost httpPost = new HttpPost(uri);
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("action", action));
				list.add(new BasicNameValuePair("username", username));

				if ("email".equals(action)) {
					String email = arg0[3];
					list.add(new BasicNameValuePair("password", password));
					list.add(new BasicNameValuePair("email", email));
				} else if ("password".equals(action)) {
					String newPassword = arg0[3];
					list.add(new BasicNameValuePair("oldPassword", password));
					list.add(new BasicNameValuePair("newPassword", newPassword));
				} else if ("phone".equals(action)) {
					String phone = arg0[3];
					list.add(new BasicNameValuePair("password", password));
					list.add(new BasicNameValuePair("phone", phone));
				}

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
			if (mListener == null) {
				return;
			}

			mRespondMessage = getRespondMessage(mRespondMessage);

			if (result) {
				mListener.onSuccess(mRespondMessage);
			} else {
				mListener.onFail(mRespondMessage);
			}
		}
	}

	private String getRespondMessage(String respondMessage) {
		String message = respondMessage;
		if (respondMessage.startsWith("User not exist.")) {
			message = "修改信息失败，用户名错误。";
		} else if ("password dismatch.".equals(respondMessage)) {
			message = "修改信息失败，密码错误。";
		} else if ("password modify sucess.".equals(respondMessage)) {
			message = "修改密码成功。";
		} else if ("email modify sucess.".equals(respondMessage)) {
			message = "修改邮箱成功。";
		} else if ("email modify fail.".equals(respondMessage)) {
			message = "修改邮箱失败，服务器错误。";
		} else if ("phone modify sucess.".equals(respondMessage)) {
			message = "修改电话号码成功。";
		} else if ("phone modify fail.".equals(respondMessage)) {
			message = "修改电话号码失败，服务器错误。";
		}

		return message;
	}

}
