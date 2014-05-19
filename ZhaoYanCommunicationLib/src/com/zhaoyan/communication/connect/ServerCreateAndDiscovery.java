package com.zhaoyan.communication.connect;

import android.content.Context;
import android.content.Intent;

import com.zhaoyan.communication.SocketCommunicationManager;
import com.zhaoyan.communication.UserManager;
import com.zhaoyan.communication.provider.ZhaoYanCommunicationData;
import com.zhaoyan.communication.search.DiscoveryService;
import com.zhaoyan.communication.util.Log;

public class ServerCreateAndDiscovery extends Thread {
	private static final String TAG = "ServerCreateAndDiscovery";
	private Context mContext;
	private DiscoveryService mDiscoveryService;
	private boolean mStop = false;
	private int mServerType;

	public ServerCreateAndDiscovery(Context context, int serverType) {
		mContext = context;
		mServerType = serverType;
	}

	@Override
	public void run() {
		startServerAndDiscovery();
	}
	
	private void startServerAndDiscovery() {
		SocketCommunicationManager communicationManager = SocketCommunicationManager
				.getInstance();
		communicationManager.startServer(mContext);

		mDiscoveryService = DiscoveryService.getInstance(mContext);
		mDiscoveryService.startDiscoveryService();

		// Let server thread start first.
		int waitTime = 0;
		try {
			while (!mStop && !communicationManager.isServerSocketStarted()
					&& waitTime < 5000) {
				Thread.sleep(200);
			}
		} catch (InterruptedException e) {
			// ignore
		}

		// If every thing is OK, the server is started.
		if (communicationManager.isServerSocketStarted()) {
			int networkType = 0;
			switch (mServerType) {
			case ServerCreator.TYPE_AP:
				networkType = ZhaoYanCommunicationData.User.NETWORK_AP;
				break;
			case ServerCreator.TYPE_LAN:
				networkType = ZhaoYanCommunicationData.User.NETWORK_WIFI;
				break;
			default:
				break;
			}
			UserManager.getInstance().addLocalServerUser(networkType);
			mContext.sendBroadcast(new Intent(
					ServerCreator.ACTION_SERVER_CREATED));
		} else {
			Log.e(TAG, "createServerAndStartDiscoveryService timeout");
		}
	}

	public void stopServerAndDiscovery() {
		mStop = true;
		UserManager userManager = UserManager.getInstance();
		userManager.resetLocalUser();
		
		SocketCommunicationManager communicationManager = SocketCommunicationManager
				.getInstance();
		communicationManager.closeAllCommunication();
		communicationManager.stopServer();

		if (mDiscoveryService != null) {
			mDiscoveryService.stopSearch();
			mDiscoveryService = null;
		}

	}
}
