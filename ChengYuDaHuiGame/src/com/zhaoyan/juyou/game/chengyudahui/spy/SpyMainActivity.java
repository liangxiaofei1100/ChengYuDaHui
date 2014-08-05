package com.zhaoyan.juyou.game.chengyudahui.spy;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dreamlink.communication.aidl.HostInfo;
import com.dreamlink.communication.aidl.User;
import com.dreamlink.communication.lib.CommunicationManager;
import com.dreamlink.communication.lib.util.AppUtil;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;

public class SpyMainActivity extends Activity implements OnItemClickListener, OnClickListener {

	private static final String TAG = SpyMainActivity.class.getSimpleName();
	
	private int mAppId;
	
	private SpyService mService = null;
	private boolean mIsServiceBinder = false;
	
	private CommunicationManager mCommunicationManager;
	
	private Button mCreateGameBtn,mSearchGameBtn,mCancelGameBtn,mStartGameBtn;
	private ListView mUserListView;
	private TextView mTipView;
	
	private TextView mListMsgView;
	
	private UserAdapter mUserAdapter;
	private HostAdapter mHostAdapter;
	
	private List<User> mUserList = new ArrayList<User>();
	private List<HostInfo> mHostList = new ArrayList<HostInfo>();
	
	private SpyListener mSpyListener = new SpyListener() {
		@Override
		public void onCallBack(Bundle bundle) {
			int flag = bundle.getInt(SpyListener.CALLBACK_FLAG);
			Log.d(TAG, "onCallBack.flag:" + flag);
			//remove tag message first,avoid too many same messages in queue.
			Message msg = mHandler.obtainMessage(flag);
			msg.setData(bundle);
			mHandler.removeMessages(flag);
			mHandler.sendMessage(msg);
		}
	};
	
	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "onServiceDisconnected");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected start");
			mService = ((SpyService.ServiceBinder) service).getService();
			if (null == mService) {
				Log.e(TAG, "onServiceConnected.Error:cannot get service");
				finish();
				return;
			}
			
			mService.registerSpyListener(mSpyListener);
			mService.connect();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spy_main);
		
		mAppId = AppUtil.getAppID(this);
		Log.d(TAG, "mAppId="+ mAppId);
		
		mCommunicationManager = new CommunicationManager(getApplicationContext());
		
		//bind service
		Intent intent = new Intent(SpyMainActivity.this, SpyService.class);
		intent.putExtra(SpyListener.KEY_APPID, mAppId);
		mIsServiceBinder = bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
		if (!mIsServiceBinder) {
			Log.e(TAG, "Error:cannot bind SpyService");
			finish();
			return;
		}
		
		mCreateGameBtn = (Button) findViewById(R.id.btn_create_game);
		mSearchGameBtn = (Button) findViewById(R.id.btn_search_game);
		mCancelGameBtn = (Button) findViewById(R.id.btn_cancel_game);
		mStartGameBtn  = (Button) findViewById(R.id.btn_start_game);
		mCreateGameBtn.setOnClickListener(this);
		mSearchGameBtn.setOnClickListener(this);
		mCancelGameBtn.setOnClickListener(this);
		mStartGameBtn.setOnClickListener(this);
		
		mUserListView = (ListView) findViewById(R.id.lv_user_list);
		mTipView = (TextView) findViewById(R.id.tv_tip_msg);
		mTipView.setText("尚无人创建主机");
		mUserListView.setEmptyView(mTipView);
		
		mListMsgView = (TextView) findViewById(R.id.tv_list_msg);
		
		mHostAdapter = new HostAdapter(SpyMainActivity.this, mHostList);
		mUserListView.setAdapter(mHostAdapter);
		mUserListView.setOnItemClickListener(this);
		
		mUserAdapter = new UserAdapter(SpyMainActivity.this, mUserList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_create_game:
			Log.d(TAG, "create game");
			//创建游戏主机
			mService.createHost();
			break;
		case R.id.btn_search_game:
			//查询一下是否有人建主机了
			Log.d(TAG, "search game");
			mService.searchHost();
			break;
		case R.id.btn_cancel_game:
			Log.d(TAG, "cancel game");
			mService.cancelGame();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int status = mService.getCurrentStatus();
		if (status != SpyListener.STATUS_INIT) {
			return ;
		}
		HostInfo hostInfo = mHostList.get(position);
		Log.d(TAG, "onItemClick.name=" + hostInfo.ownerName);
		//加入游戏
		mService.joinGame(hostInfo);
	}
	
	
	/**
	 * Handler to update ui
	 */
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Log.d(TAG, "handlerMessge.what=" + msg.what);
			Bundle bundle;
			switch (msg.what) {
			case SpyListener.MSG_UPDATE_HOST_UI:
				Log.d(TAG, "==========update host ui===========");
				bundle = msg.getData();
				setTitle("主机列表");
				mHostList.clear();
				mHostList = bundle.getParcelableArrayList(SpyListener.KEY_HOSTLIST);
				Log.d(TAG, "handler.mHostList.size=" + mHostList.size());
				for (int i = 0; i < mHostList.size(); i++) {
					Log.d(TAG, "MSG_HOST.info.name=" + mHostList.get(i).ownerName);
				}
				mHostAdapter.setData(mHostList);
				break;
			case SpyListener.MSG_UPDATE_USER_UI:
				Log.d(TAG, "==========update user ui===========");
				setTitle("玩家列表");
				bundle = msg.getData();
				mUserList.clear();
				mUserList = bundle.getParcelableArrayList(SpyListener.KEY_USERLIST);
				Log.d(TAG, "handler.mUserList.size=" + mUserList.size());
				mUserAdapter.setData(mUserList);
				mUserListView.setAdapter(mUserAdapter);
				
				int status = mService.getCurrentStatus();
				if (status == SpyListener.STATUS_CREATED) {
					mStartGameBtn.setVisibility(View.VISIBLE);
				} else {
					mStartGameBtn.setVisibility(View.GONE);
				}
				
				mCancelGameBtn.setVisibility(View.VISIBLE);
				mCreateGameBtn.setVisibility(View.GONE);
				mSearchGameBtn.setVisibility(View.GONE);
				break;
				
			case SpyListener.MSG_EXIT_GAME:
				Log.d(TAG, "==========exit game===========");
				setTitle("主机列表");
				mUserList.clear();
				mHostList.clear();
				mStartGameBtn.setVisibility(View.GONE);
				mCancelGameBtn.setVisibility(View.GONE);
				mCreateGameBtn.setVisibility(View.VISIBLE);
				mSearchGameBtn.setVisibility(View.VISIBLE);
				mHostAdapter.setData(mHostList);
				mUserListView.setAdapter(mHostAdapter);
				break;
				
			case SpyListener.MSG_NO_USER_CONNECTED:
				Log.d(TAG, "==========no user connected===========");
				new AlertDialog.Builder(SpyMainActivity.this)
						.setTitle("提示")
						.setMessage("聚游尚未连接,请先连接!")
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										SpyMainActivity.this.finish();
									}
								}).setCancelable(false).show();
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		unbindService(mServiceConnection);
	}


}
