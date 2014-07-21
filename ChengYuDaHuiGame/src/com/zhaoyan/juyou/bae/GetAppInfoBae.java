package com.zhaoyan.juyou.bae;

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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.zhaoyan.common.util.AsyncTaskUtils;
import com.zhaoyan.juyou.account.bae.BAEHttpUtils;

public class GetAppInfoBae {
	private static final String TAG = GetAppInfoBae.class.getSimpleName();
	private GetAppInfoResultListener mListener;

	public void getAppInfos(GetAppInfoResultListener listener) {
		mListener = listener;
		GetAppInfoTask task = new GetAppInfoTask();
		AsyncTaskUtils.execute(task);
	}

	private class GetAppInfoTask extends AsyncTask<String, Void, Boolean> {
		private String mRespondMessage = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			Log.d(TAG, "start...");
			boolean result = false;

			try {
				URI uri = new URI(BAEHttpUtils.getGetAppInfoURL());
				HttpPost httpPost = new HttpPost(uri);
				List<NameValuePair> list = new ArrayList<NameValuePair>();

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
				Log.e(TAG, "error. ", e);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Log.e(TAG, "error. ", e);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "error. ", e);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				Log.e(TAG, "error. ", e);
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

			if (result) {
				mListener.onSuccesss(mRespondMessage);
			} else {
				mListener.onFail(getRespondMessage(mRespondMessage));
			}
		}
	}

	private String getRespondMessage(String respondMessage) {
		String message = respondMessage;

		return message;
	}
}
