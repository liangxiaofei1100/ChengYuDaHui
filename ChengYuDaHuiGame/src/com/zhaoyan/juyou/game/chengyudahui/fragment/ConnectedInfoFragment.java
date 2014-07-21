package com.zhaoyan.juyou.game.chengyudahui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.zhaoyan.common.net.NetWorkUtil;
import com.zhaoyan.communication.ProtocolCommunication;
import com.zhaoyan.communication.SocketCommunicationManager;
import com.zhaoyan.communication.SocketPort;
import com.zhaoyan.communication.UserHelper;
import com.zhaoyan.communication.UserInfo;
import com.zhaoyan.communication.connect.ServerCreator;
import com.zhaoyan.communication.ipc.CommunicationManager;
import com.zhaoyan.communication.ipc.aidl.OnCommunicationListenerExternal;
import com.zhaoyan.communication.ipc.aidl.User;
import com.zhaoyan.communication.provider.ZhaoYanCommunicationData;
import com.zhaoyan.communication.qrcode.ServerInfoMessage;
import com.zhaoyan.communication.search.SearchUtil;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.QRCodeDisplayActivity;
import com.zhaoyan.juyou.game.chengyudahui.adapter.ConnectedUserAdapter;
import com.zhaoyan.juyou.game.chengyudahui.protocol.pb.SpeakGameProtos.SpeakGameMsg;
import com.zhaoyan.juyou.game.chengyudahui.protocol.pb.SpeakGameProtos.SpeakGameMsg.Command;
import com.zhaoyan.juyou.game.chengyudahui.protocol.pb.SpeakGameProtos.SpeakGameMsg.GameType;
import com.zhaoyan.juyou.game.chengyudahui.protocol.pb.SpeakGameProtos.SpeakGameMsg.RoleType;
import com.zhaoyan.juyou.game.chengyudahui.speakgame.SpeakGameInternet;
import com.zhaoyan.juyou.game.chengyudahui.speakgame.SpeakMessageSend;

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
		if (!com.zhaoyan.communication.UserManager
				.isManagerServer(com.zhaoyan.communication.UserManager
						.getInstance().getLocalUser())) {
			mStartGame.setVisibility(View.GONE);
		}
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
			SpeakGameMsg msg = SpeakMessageSend.getInstance().getSendMessage(
					GameType.SPEAK, Command.START, 0, 0, RoleType.UNKONWN);
			mProtocolCommunication.sendMessageToAll(msg.toByteArray(), 100);
			ArrayList<User> temp = new ArrayList<User>();
			for (java.util.Map.Entry<Integer, User> entry : com.zhaoyan.communication.UserManager
					.getInstance().getAllUser().entrySet()) {
				temp.add(entry.getValue());
			}
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList("allUser", temp);
			intent.putExtra("user", bundle);
			intent.putExtra("number", temp.size());
			if (mode != null && "speak".equals(mode)) {
				intent.setClass(getActivity(), SpeakGameInternet.class);
			} else {

			}
			getActivity().startActivity(intent);
			break;
		case R.id.btn_ci_qrcode:
			launchQRCodeDisplay();
			break;
		default:
			break;
		}
	}

	private void launchQRCodeDisplay() {
		Intent intent = new Intent();
		intent.setClass(mContext, QRCodeDisplayActivity.class);

		UserInfo userInfo = UserHelper.getServerUserInfo(mContext);
		String ip = userInfo.getIpAddress();
		String ssid = userInfo.getSsid();

		ServerInfoMessage serverInfoMessage = new ServerInfoMessage(ssid, ip,
				SocketPort.COMMUNICATION_SERVER_PORT,userInfo.getNetworkType());
		String qrcode = serverInfoMessage.getQRCodeString();

		intent.putExtra(QRCodeDisplayActivity.EXTRA_CONTENT, qrcode);
		startActivity(intent);
	}

	private void initView(View rootView) {
		mDisconnectButton = (Button) rootView
				.findViewById(R.id.btn_ci_disconnect);
		mStartGame = (Button) rootView.findViewById(R.id.btn_ci_start);
		mDisconnectButton.setOnClickListener(this);
		mStartGame.setOnClickListener(this);

		Button qrcodeButton = (Button) rootView
				.findViewById(R.id.btn_ci_qrcode);
		qrcodeButton.setOnClickListener(this);
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
		SpeakGameMsg msg = SpeakMessageSend.getInstance().parseMsg(arg0);
		if (GameType.SPEAK == msg.getGame()) {
			if (Command.START == msg.getCommand()) {
				Intent intent = new Intent();
				intent.setClass(this.mContext, SpeakGameInternet.class);
				this.mContext.startActivity(intent);
			}
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
