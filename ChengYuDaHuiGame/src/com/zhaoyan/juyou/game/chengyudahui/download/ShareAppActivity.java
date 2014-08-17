package com.zhaoyan.juyou.game.chengyudahui.download;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.zhaoyan.common.file.APKUtil;
import com.zhaoyan.common.util.PreferencesUtils;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.bae.GetAppInfoBae;
import com.zhaoyan.juyou.bae.GetAppInfoResultListener;
import com.zhaoyan.juyou.game.chengyudahui.R;

public class ShareAppActivity extends ActionBarActivity {
	private static final String TAG = ShareAppActivity.class.getSimpleName();
	private Context mContext;

	private ListView mListView;
	private ShareAppAdapter mAdapter;
	private List<AppInfo> mAppList = new ArrayList<AppInfo>();

	private Toast mToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("应用分享");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_share_app);

		mContext = this;
		initView();
	}

	private void initView() {
		mListView = (ListView) findViewById(android.R.id.list);
		View emptyView = findViewById(R.id.tv_empty);
		mListView.setEmptyView(emptyView);

		mAdapter = new ShareAppAdapter(mAppList, mContext);
		mListView.setAdapter(mAdapter);
		// mAdapter.registerKeyListener(getAppListener);
		queryAppInfos();
		//
		// mListView.setOnItemClickListener(this);

	}

	/**
	 * query appinfos from cloud
	 */
	public void queryAppInfos() {
		GetAppInfoBae getAppInfoBae = new GetAppInfoBae();
		getAppInfoBae.getAppInfos(new GetAppInfoResultListener() {
			@Override
			public void onSuccesss(String appInfoJson) {
				Log.d(TAG, "query.onSuccess");
				if (appInfoJson == null || appInfoJson.isEmpty()) {
					Toast.makeText(getApplicationContext(),
							"onSuccess.Data is null", Toast.LENGTH_SHORT)
							.show();
				}
				try {
					JSONArray array = new JSONArray(appInfoJson);
					JSONObject jsonObject = null;
					AppInfo appInfo = null;
					for (int i = 0; i < array.length(); i++) {
						jsonObject = array.getJSONObject(i);
						appInfo = AppInfo.parseJson(jsonObject);

						String packagename = appInfo.getPackageName();
						boolean isInstalled = APKUtil.isAppInstalled(
								getApplicationContext(), packagename);
						String serverVersion = appInfo.getVersion();
						String localVersion = APKUtil.getInstalledAppVersion(
								getApplicationContext(), packagename);
						boolean isVersionEqual = serverVersion
								.equals(localVersion);
						Log.d(TAG, "serverVersion:" + serverVersion
								+ ",localVersion:" + localVersion);
						Log.d(TAG, "isVersionEqual:" + isVersionEqual);

						String localPath = PreferencesUtils.getString(
								getApplicationContext(), packagename, null);

						if (isInstalled) {
							if (!isVersionEqual) {
								appInfo.setStatus(Conf.NEED_UDPATE);
							} else {
								appInfo.setStatus(Conf.INSTALLED);
							}
						} else if (localPath != null) {
							appInfo.setStatus(Conf.DOWNLOADED);
							appInfo.setAppLocalPath(localPath);
						}

						if (appInfo.getStatus() == Conf.INSTALLED || appInfo.getStatus() == Conf.DOWNLOADED) {
							mAppList.add(appInfo);
						}
					}
				} catch (JSONException e) {
					Log.e(TAG, "Json error:" + e.toString());
					e.printStackTrace();
				}
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFail(String message) {
				Log.e(TAG, "query.onFail:" + message);
				toast("读取应用失败");
			}
		});
	}

	private void toast(String message) {
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
		mToast.show();
	}

}
