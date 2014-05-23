package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.communication.ProtocolCommunication;
import com.zhaoyan.communication.ipc.aidl.OnCommunicationListenerExternal;
import com.zhaoyan.communication.ipc.aidl.User;
import com.zhaoyan.communication.util.Log;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

public class CommunicationTestActivity extends Activity implements OnCommunicationListenerExternal{
	private static final String TAG = CommunicationTestActivity.class.getSimpleName();
	private ProtocolCommunication mProtocolCommunication;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProtocolCommunication = ProtocolCommunication.getInstance();
		mProtocolCommunication.registerOnCommunicationListenerExternal(this, 234);
		
		Intent intent = new Intent();
		intent.setClass(this, ConnectFriendsActivity.class);
		startActivity(intent);
	}

	@Override
	public IBinder asBinder() {
		return null;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mProtocolCommunication.sendMessageToAll("123".getBytes(), 234);
	}

	@Override
	public void onReceiveMessage(byte[] arg0, User arg1) throws RemoteException {
		Log.d(TAG, "onReceiveMessage " + arg0 + ", user " + arg1);
	}

	@Override
	public void onUserConnected(User arg0) throws RemoteException {
		
	}

	@Override
	public void onUserDisconnected(User arg0) throws RemoteException {
		
	}
}
