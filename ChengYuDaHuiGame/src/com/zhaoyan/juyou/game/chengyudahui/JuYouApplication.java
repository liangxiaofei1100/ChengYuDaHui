package com.zhaoyan.juyou.game.chengyudahui;

import android.content.Context;
import android.content.Intent;

import com.baidu.frontia.FrontiaApplication;
import com.zhaoyan.common.net.NetWorkUtil;
import com.zhaoyan.communication.FileTransferService;
import com.zhaoyan.communication.ProtocolCommunication;
import com.zhaoyan.communication.SocketCommunicationManager;
import com.zhaoyan.communication.TrafficStatics;
import com.zhaoyan.communication.UserManager;
import com.zhaoyan.communication.connect.ServerConnector;
import com.zhaoyan.communication.connect.ServerCreator;
import com.zhaoyan.communication.search.SearchUtil;
import com.zhaoyan.communication.search.ServerSearcher;
import com.zhaoyan.communication.util.Log;

public class JuYouApplication extends FrontiaApplication {
	private static final String TAG = "JuYouApplication";
	private static boolean mIsInit = false;

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
		if (mIsInit) {
			return;
		}
		Log.d(TAG, "initApplication");
		mIsInit = true;

		// Start save log to file.
		Log.startSaveToFile();
		// Initialize TrafficStatics
		TrafficStatics.getInstance().init(context);
		// Initialize SocketCommunicationManager
		SocketCommunicationManager.getInstance().init(context);
		// Initialize ProtocolCommunication
		ProtocolCommunication.getInstance().init(context);
	}

	public static synchronized void quitApplication(Context context) {
		if (!mIsInit) {
			return;
		}
		Log.d(TAG, "quitApplication");
		mIsInit = false;
		logout(context);
		stopServerSearch(context);
		stopServerCreator(context);
		stopCommunication(context);
		stopFileTransferService(context);
		// Release ProtocolCommunication
		ProtocolCommunication.getInstance().release();
		// Release SocketCommunicationManager
		SocketCommunicationManager.getInstance().release();
		// Release TrafficStatics
		TrafficStatics.getInstance().quit();
		// Stop record log and close log file.
		Log.stopAndSave();
		releaseStaticInstance(context);
	}

	private static void logout(Context context) {
		ProtocolCommunication protocolCommunication = ProtocolCommunication
				.getInstance();
		protocolCommunication.logout();
	}

	private static void releaseStaticInstance(Context context) {
		ServerConnector serverConnector = ServerConnector.getInstance(context);
		serverConnector.release();
	}

	private static void stopServerCreator(Context context) {
		ServerCreator serverCreator = ServerCreator.getInstance(context);
		serverCreator.stopServer();
		serverCreator.release();
	}

	private static void stopServerSearch(Context context) {
		ServerSearcher serverSearcher = ServerSearcher.getInstance(context);
		serverSearcher.stopSearch(ServerSearcher.SERVER_TYPE_ALL);
		serverSearcher.release();
	}

	private static void stopFileTransferService(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, FileTransferService.class);
		context.stopService(intent);
	}

	private static void stopCommunication(Context context) {
		UserManager.getInstance().resetLocalUser();
		SocketCommunicationManager manager = SocketCommunicationManager
				.getInstance();
		manager.closeAllCommunication();
		manager.stopServer();

		// Disable wifi AP.
		NetWorkUtil.setWifiAPEnabled(context, null, false);
		// Clear wifi connect history.
		SearchUtil.clearWifiConnectHistory(context);
	}
}
