package com.zhaoyan.juyou.game.chengyudahui.speakgame;

import com.zhaoyan.communication.ProtocolCommunication;
import com.zhaoyan.communication.ipc.aidl.OnCommunicationListenerExternal;
import com.zhaoyan.communication.ipc.aidl.User;
import com.zhaoyan.juyou.game.chengyudahui.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SpeakGameInternet extends Activity implements OnClickListener,
		OnCommunicationListenerExternal {
	private View mLoadingView, mSelectRoleView, mGameView;
	private Button mRefereeBtn, mActorBtn, mObserverBtn;
	private ProtocolCommunication mProtocolCommunication;
	private final int REFEREE_ID = 0, ACTOR_ID = 1, OBSERVER_ID = 2;
	private int roleID = 2;// default role id is observer
	private boolean readyFlag = false;
	private final int APP_ID = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speak_internet_layout);
		initView();
		mProtocolCommunication = ProtocolCommunication.getInstance();
		mProtocolCommunication.registerOnCommunicationListenerExternal(this,
				APP_ID);
	}

	private void initView() {
		mLoadingView = findViewById(R.id.loading_layout);
		mSelectRoleView = findViewById(R.id.select_role_layout);
		mRefereeBtn = (Button) findViewById(R.id.referee_internet_game);
		mActorBtn = (Button) findViewById(R.id.actor_internet_game);
		mObserverBtn = (Button) findViewById(R.id.observer_internet_game);
		mGameView = findViewById(R.id.internet_game_layout);
		mRefereeBtn.setOnClickListener(this);
		mActorBtn.setOnClickListener(this);
		mObserverBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.referee_internet_game:
			roleID = REFEREE_ID;
			break;
		case R.id.actor_internet_game:
			roleID = ACTOR_ID;
			break;
		case R.id.observer_internet_game:
			roleID = OBSERVER_ID;
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!readyFlag) {
			readyFlag = true;
			mProtocolCommunication.sendMessageToAll("speak:ready".getBytes(),
					APP_ID);
		}
	}

	@Override
	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onReceiveMessage(byte[] arg0, User arg1) throws RemoteException {
		// TODO Auto-generated method stub
		String temp = new String(arg0);
		if (temp != null) {
			int num = temp.indexOf(":");
			String gameName = temp.substring(0, num);
			String cmd = temp.substring(num);
		}
	}

	@Override
	public void onUserConnected(User arg0) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserDisconnected(User arg0) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
