package com.zhaoyan.juyou.game.chengyudahui;

import android.content.Context;
import android.content.Intent;

import com.baidu.frontia.FrontiaApplication;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.push.PushUtils;
import com.zhaoyan.juyou.game.chengyudahui.utils.ServiceUtil;

public class JuYouApplication extends FrontiaApplication {
	private static final String TAG = "JuYouApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		initApplication(getApplicationContext());
	}

	/**
	 * Notice, call this not only in application's {@link #onCreate()}, but also
	 * in the first activity's onCreate(). Because application's
	 * {@link #onCreate()} will not be call every time when we launch first
	 * activity.
	 * 
	 * @param context
	 */
	public static synchronized void initApplication(Context context) {
		Log.d(TAG, "initApplication");

		// bind baidu push service
		PushUtils.startBind(context);

		if (ServiceUtil.isServiceRunning(context, JuYouService.class.getName())) {
			Log.d(TAG, "Service is already running.");
		} else {
			Log.d(TAG, "Service is not running.");
			Intent intent = new Intent(JuYouService.ACTION_START_COMMUNICATION);
			intent.setClass(context, JuYouService.class);
			context.startService(intent);
		}
	}

	public static synchronized void quitApplication(Context context) {
		Log.d(TAG, "quitApplication");
		Intent intent = new Intent(JuYouService.ACTION_STOP_COMMUNICATION);
		intent.setClass(context, JuYouService.class);
		context.startService(intent);
	}
}
