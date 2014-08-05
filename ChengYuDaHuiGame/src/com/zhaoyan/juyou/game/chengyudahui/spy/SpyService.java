package com.zhaoyan.juyou.game.chengyudahui.spy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.dreamlink.communication.aidl.HostInfo;
import com.dreamlink.communication.aidl.User;
import com.dreamlink.communication.lib.CommunicationManager;
import com.dreamlink.communication.lib.CommunicationManager.OnConnectionChangeListener;
import com.dreamlink.communication.lib.CommunicationManager.PlatformCallback;
import com.zhaoyan.communication.util.Log;

public class SpyService extends Service implements OnConnectionChangeListener, PlatformCallback {
	private static final String TAG = SpyService.class.getSimpleName();
	private ArrayList<Record> mRecords = new ArrayList<SpyService.Record>();
	
	private HostInfo mCurrentHostInfo = null;
	
	private int mAppId = 0;
	private CommunicationManager mCommunicationManager;
	
	private SpyServiceHandler mServiceHandler;
	
	private final IBinder mBinder = new ServiceBinder();
	
	private int mStatus = SpyListener.STATUS_INIT;
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind start");
		if (intent != null) {
			mAppId = intent.getExtras().getInt(SpyListener.KEY_APPID);
		}
		Log.d(TAG, "onBind end");
		return mBinder;
	}
	
	public class ServiceBinder extends Binder{
		/**
		 * get Spy Service instance
		 * @return service instance
		 */
		SpyService getService(){
			return SpyService.this;
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate start");
		HandlerThread handlerThread = new HandlerThread("SpyServiceThread");
		handlerThread.start();
		mServiceHandler = new SpyServiceHandler(handlerThread.getLooper());
		
		mCommunicationManager = new CommunicationManager(getApplicationContext());
		mCommunicationManager.registerPlatformCallback(this);
		Log.d(TAG, "onCreate end");
	}
	
	private static class Record{
		int mHashCode;
		SpyListener mCallBack;
	}
	
	/**
	 * register Spy Listener,activity get service state should call this method register SpyListener
	 * @param callBack {@link SpyListener}
	 */
	public void registerSpyListener(SpyListener callBack){
		synchronized (mRecords) {
			Record record = null;
			int hashCode = callBack.hashCode();
			final int n = mRecords.size();
			for (int i = 0; i < n; i++) {
				record = mRecords.get(i);
				if (hashCode == record.mHashCode) {
					return;
				}
			}
			record = new Record();
			record.mHashCode = hashCode;
			record.mCallBack = callBack;
			mRecords.add(record);
		}
	}
	
	/**
	 * call back from service to activity
	 * @param bundle
	 */
	private void notifiyActivityStateChanaged(Bundle bundle){
		if (!mRecords.isEmpty()) {
			Log.d(TAG, "notifiyActivityStateChanaged:clients=" + mRecords.size());
			synchronized (mRecords) {
				Iterator<Record> iterator = mRecords.iterator();
				while (iterator.hasNext()) {
					Record record = iterator.next();
					SpyListener listener = record.mCallBack;
					if (listener == null) {
						iterator.remove();
						return;
					}
					listener.onCallBack(bundle);
				}
			}
		}
	}
	
	/**
	 * unregister spy listener
	 * @param callback
	 */
	public void unregisterSpyListener(SpyListener callback){
		remove(callback.hashCode());
	}
	
	/**
	 * remove call back according hash code
	 * @param hashCode
	 */
	private void remove(int hashCode){
		synchronized (mRecords) {
			Iterator<Record> iterator = mRecords.iterator();
			while (iterator.hasNext()) {
				Record record = iterator.next();
				if (record.mHashCode == hashCode) {
					iterator.remove();
				}
			}
		}
	}
	
	public void connect(){
		boolean ret = mCommunicationManager.connectCommunicatonService(this, mAppId);
		Log.d(TAG, "onCreate.ret=" + ret);
	}
	
	/**
	 * create host 
	 */
	public void createHost(){
		Log.d(TAG, "createHost");
		mCommunicationManager.createHost("Spy", "com.zhaoyan.game.spy", 16, mAppId);
	}
	
	/**
	 * search host
	 */
	public void searchHost(){
		Log.d(TAG, "searchHost");
		mCommunicationManager.getAllHost(mAppId);
	}
	
	public int getCurrentStatus(){
		return mStatus;
	}
	
	public void joinGame(HostInfo hostInfo){
		Log.d(TAG, "JoinGame.hostname:" + hostInfo.ownerName);
		mCurrentHostInfo = hostInfo;
		mCommunicationManager.joinGroup(hostInfo);
	}
	
	/**
	 * cancel game
	 */
	public void cancelGame(){
		mStatus = SpyListener.STATUS_INIT;
		if (mCurrentHostInfo != null) {
			Log.d(TAG, "cancelGame.exit group");
			mCommunicationManager.exitGroup(mCurrentHostInfo);
			Bundle bundle = new Bundle(1);
			bundle.putInt(SpyListener.CALLBACK_FLAG, SpyListener.MSG_EXIT_GAME);
			notifiyActivityStateChanaged(bundle);
		} else {
			Log.e(TAG, "mcurrent host info is null");
		}
	}

	@Override
	public void hostHasCreated(HostInfo hostInfo) {
		Log.d(TAG, "hostHasCreated:" + hostInfo.ownerName);
		mStatus = SpyListener.STATUS_CREATED;
		mCurrentHostInfo = hostInfo;
		//更新玩家列表UI,此时玩家列表只有主机一人
		Bundle bundle = new Bundle(2);
		bundle.putInt(SpyListener.CALLBACK_FLAG, SpyListener.MSG_UPDATE_USER_UI);
		User localUser = mCommunicationManager.getLocalUser();
		ArrayList<User> userList = new ArrayList<User>();
		userList.add(localUser);
		bundle.putParcelableArrayList(SpyListener.KEY_USERLIST, userList);
		notifiyActivityStateChanaged(bundle);
	}

	@Override
	public void joinGroupResult(HostInfo hostInfo, boolean flag) {
		// TODO Auto-generated method stub
		Log.d(TAG, "joinGroupResult:" + hostInfo.ownerName + ",flag:" + flag);
		if (flag) {
			mStatus = SpyListener.STATUS_JOINED;
		}
	}

	@Override
	public void groupMemberUpdate(int hostId, ArrayList<User> userList) {
		Log.d(TAG, "groupMemberUpdate:" + hostId + ",user.size=" + userList.size());
		//update user list ui
		Bundle bundle = new Bundle(2);
		bundle.putInt(SpyListener.CALLBACK_FLAG, SpyListener.MSG_UPDATE_USER_UI);
		bundle.putParcelableArrayList(SpyListener.KEY_USERLIST, userList);
		notifiyActivityStateChanaged(bundle);
	}

	@Override
	public void hostInfoChange(List<HostInfo> hostList) {
		Log.d(TAG, "hostInfoChange:" + hostList.size());
		for (int i = 0; i < hostList.size(); i++) {
			Log.d(TAG, "owner.name=" + hostList.get(i).ownerName);
		}
		Log.d(TAG, "hostInfoChange.status=" + mStatus);
		if (mStatus == SpyListener.STATUS_CREATED || mStatus == SpyListener.STATUS_JOINED) {
			return ;
		}
		
		Bundle bundle = new Bundle(2);
		bundle.putInt(SpyListener.CALLBACK_FLAG, SpyListener.MSG_UPDATE_HOST_UI);
		bundle.putParcelableArrayList(SpyListener.KEY_HOSTLIST, (ArrayList<HostInfo>)hostList);
		notifiyActivityStateChanaged(bundle);
	}

	@Override
	public void hasExitGroup(int hostId) {
		Log.d(TAG, "hasExitGroup:" + hostId);
		mStatus = SpyListener.STATUS_INIT;
		Bundle bundle = new Bundle(1);
		bundle.putInt(SpyListener.CALLBACK_FLAG, SpyListener.MSG_EXIT_GAME);
		notifiyActivityStateChanaged(bundle);
	}

	@Override
	public void receiverMessage(byte[] data, User sendUser, boolean allFlag,
			HostInfo info) {
		// TODO Auto-generated method stub
		Log.d(TAG, "receiverMessage,sendUser:" + sendUser.getUserName());
	}

	@Override
	public void startGroupBusiness(HostInfo hostInfo) {
		// TODO Auto-generated method stub
		Log.d(TAG, "startGroupBusiness:" + hostInfo.ownerName);
	}

	@Override
	public void onCommunicationDisconnected() {
		Log.d(TAG, "onCommunicationDisconnected");
		cancelGame();
	}

	@Override
	public void onCommunicationConnected() {
		Log.d(TAG, "onCommunicationConnected");
		//判断聚游是否连接
		List<User> userList = mCommunicationManager.getAllUser();
		Log.d(TAG, "userlist.size=" + userList.size());
		if (userList == null || userList.size() == 0) {
			Log.d(TAG, "no user connected");
			Bundle bundle = new Bundle(1);
			bundle.putInt(SpyListener.CALLBACK_FLAG, SpyListener.MSG_NO_USER_CONNECTED);
			notifiyActivityStateChanaged(bundle);
			return;
		}
		// 查询一下是否有人建主机了
		mCommunicationManager.getAllHost(mAppId);
	}
	
	class SpyServiceHandler extends Handler{
		public SpyServiceHandler(Looper looper){
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		cancelGame();
		mCommunicationManager.disconnectCommunicationService();
	}

}
