package com.zhaoyan.juyou.game.chengyudahui.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.zhaoyan.common.net.NetWorkUtil;
import com.zhaoyan.communication.ProtocolCommunication;
import com.zhaoyan.communication.SocketCommunicationManager;
import com.zhaoyan.communication.connect.ServerCreator;
import com.zhaoyan.communication.ipc.CommunicationManager;
import com.zhaoyan.communication.ipc.aidl.OnCommunicationListenerExternal;
import com.zhaoyan.communication.ipc.aidl.User;
import com.zhaoyan.communication.provider.ZhaoYanCommunicationData;
import com.zhaoyan.communication.search.SearchUtil;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.adapter.ConnectedUserAdapter;
import com.zhaoyan.juyou.game.chengyudahui.speakgame.SpeakGameInternet;

public class ConnectedInfoFragment extends ListFragment implements
		OnClickListener, LoaderCallbacks<Cursor>,
		OnCommunicationListenerExternal {
	private static final String TAG = "ConnectedInfoFragment";
	private Button mDisconnectButton, mStartGame;
	private Context mContext;
	private ServerCreator mServerCreator;
	public String mode;
	private ListView mListView;
	private ConnectedUserAdapter mAdapter;
	private ProtocolCommunication mProtocolCommunication;

	protected static final String[] PROJECTION = {
			ZhaoYanCommunicationData.User._ID,
			ZhaoYanCommunicationData.User.USER_NAME,
			ZhaoYanCommunicationData.User.USER_ID,
			ZhaoYanCommunicationData.User.HEAD_ID,
			ZhaoYanCommunicationData.User.THIRD_LOGIN,
			ZhaoYanCommunicationData.User.HEAD_DATA,
			ZhaoYanCommunicationData.User.IP_ADDR,
			ZhaoYanCommunicationData.User.STATUS,
			ZhaoYanCommunicationData.User.TYPE,
			ZhaoYanCommunicationData.User.SSID,
			ZhaoYanCommunicationData.User.SIGNATURE };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		mContext = getActivity();
		mServerCreator = ServerCreator.getInstance(mContext);

		View rootView = inflater.inflate(R.layout.connected_info, container,
				false);
		initView(rootView);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mListView = getListView();
		mAdapter = new ConnectedUserAdapter(mContext, null, true);
		mListView.setAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
		mProtocolCommunication = ProtocolCommunication.getInstance();
		mProtocolCommunication.registerOnCommunicationListenerExternal(this,
				100);
	}

	@Override
	public void onDestroyView() {
		Log.d(TAG, "onDestroyView");
		super.onDestroyView();
		mAdapter.swapCursor(null);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		String selection = ZhaoYanCommunicationData.User.STATUS + "="
				+ ZhaoYanCommunicationData.User.STATUS_SERVER_CREATED + " or "
				+ ZhaoYanCommunicationData.User.STATUS + "="
				+ ZhaoYanCommunicationData.User.STATUS_CONNECTED;
		return new CursorLoader(mContext,
				ZhaoYanCommunicationData.User.CONTENT_URI, PROJECTION,
				selection, null,
				ZhaoYanCommunicationData.User.SORT_ORDER_DEFAULT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ci_disconnect:
			disconnect();
			break;
		case R.id.btn_ci_start:
			mProtocolCommunication.sendMessageToAll("start".getBytes(), 100);
			Intent intent = new Intent();
			if (mode != null && "speak".equals(mode)) {
				intent.setClass(getActivity(), SpeakGameInternet.class);
			} else {

			}
			getActivity().startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void initView(View rootView) {
		mDisconnectButton = (Button) rootView
				.findViewById(R.id.btn_ci_disconnect);
		mStartGame = (Button) rootView.findViewById(R.id.btn_ci_start);
		mDisconnectButton.setOnClickListener(this);
		mStartGame.setOnClickListener(this);
	}

	private void disconnect() {
		ProtocolCommunication protocolCommunication = ProtocolCommunication
				.getInstance();
		protocolCommunication.logout();

		// Stop search if this is server.
		// Stop all communication if this is client.
		if (mServerCreator.isCreated()) {
			mServerCreator.stopServer();
		} else {
			SocketCommunicationManager manager = SocketCommunicationManager
					.getInstance();
			manager.closeAllCommunication();
		}

		// close WiFi AP if WiFi AP is enabled.
		if (NetWorkUtil.isWifiApEnabled(mContext)) {
			NetWorkUtil.setWifiAPEnabled(mContext, null, false);
		}
		// disconnect current network if connected to the WiFi AP created by
		// our application.
		SearchUtil.clearWifiConnectHistory(mContext);
	}

	@Override
	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onReceiveMessage(byte[] arg0, User arg1) throws RemoteException {
		// TODO Auto-generated method stub

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
