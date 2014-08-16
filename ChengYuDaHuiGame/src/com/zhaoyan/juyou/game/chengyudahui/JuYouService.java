package com.zhaoyan.juyou.game.chengyudahui;

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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class JuYouService extends Service {
	private static final String TAG = JuYouService.class.getSimpleName();
	private Context mContext;
	public static final String ACTION_START_COMMUNICATION = "com.zhaoyan.juyou.game.chengyudahui.JuYouService.ACTION_START_COMMUNICATION";
	public static final String ACTION_STOP_COMMUNICATION = "com.zhaoyan.juyou.game.chengyudahui.JuYouService.ACTION_STOP_COMMUNICATION";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		Log.d(TAG, "onCreate()");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			Log.d(TAG, "onStartCommand action = " + intent.getAction());
			handlerIntent(intent);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void handlerIntent(Intent intent) {
		String action = intent.getAction();
		if (ACTION_START_COMMUNICATION.equals(action)) {
			startCommunication();
		} else if (ACTION_STOP_COMMUNICATION.equals(action)) {
			stopCommunication();
		}
	}

	private void startCommunication() {
		// Start save log to file.
		Log.startSaveToFile();
		// Initialize TrafficStatics
		TrafficStatics.getInstance().init(mContext);
		// Initialize SocketCommunicationManager
		SocketCommunicationManager.getInstance().init(mContext);
		// Initialize ProtocolCommunication
		ProtocolCommunication.getInstance().init(mContext);
		
		startFileTransferServer(mContext);
	}

	private void stopCommunication() {
		logout(mContext);
		stopServerSearch(mContext);
		stopServerCreator(mContext);
		stopCommunication(mContext);
		stopFileTransferService(mContext);
		// Release ProtocolCommunication
		ProtocolCommunication.getInstance().release();
		// Release SocketCommunicationManager
		SocketCommunicationManager.getInstance().release();
		// Release TrafficStatics
		TrafficStatics.getInstance().quit();
		// Stop record log and close log file.
		Log.stopAndSave();
		releaseStaticInstance(mContext);
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
	
	private static void startFileTransferServer(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, FileTransferService.class);
		context.startService(intent);
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
