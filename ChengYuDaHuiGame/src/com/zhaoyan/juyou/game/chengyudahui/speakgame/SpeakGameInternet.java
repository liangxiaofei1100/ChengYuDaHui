package com.zhaoyan.juyou.game.chengyudahui.speakgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoyan.communication.ProtocolCommunication;
import com.zhaoyan.communication.ipc.aidl.OnCommunicationListenerExternal;
import com.zhaoyan.communication.ipc.aidl.User;
import com.zhaoyan.juyou.game.chengyudahui.MainActivity;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.ChengyuData.ChengyuColums;
import com.zhaoyan.juyou.game.chengyudahui.protocol.pb.SpeakGameProtos.SpeakGameMsg;
import com.zhaoyan.juyou.game.chengyudahui.protocol.pb.SpeakGameProtos.SpeakGameMsg.Command;
import com.zhaoyan.juyou.game.chengyudahui.protocol.pb.SpeakGameProtos.SpeakGameMsg.GameType;
import com.zhaoyan.juyou.game.chengyudahui.protocol.pb.SpeakGameProtos.SpeakGameMsg.RoleType;

public class SpeakGameInternet extends Activity implements OnClickListener,
		OnCommunicationListenerExternal {
	private View mLoadingView, mSelectRoleView, mGameView;
	private TextView infoText, mCountDownText, mGameWordText, mGameWordInfo;
	private Button mRefereeBtn, mActorBtn, mObserverBtn, mGameRightBtn,
			mGameNextBtn;
	private ProtocolCommunication mProtocolCommunication;
	private final int REFEREE_ID = 0, ACTOR_ID = 1, OBSERVER_ID = 2;
	private int roleID = 2;// default role id is observer
	private boolean readyFlag = false, processFlag = true, startFlag = false;;
	private final int APP_ID = 100;
	private com.zhaoyan.communication.UserManager mUserManager;
	private int gamePeopleNumber, readyAckNumber;
	private boolean isMainServer, overFlag;
	private List<RoleType> roleList;
	private List<User> gameUser;
	private Timer mCountDownTimer;
	private Random mRandom;
	private RoleType mRoleType;
	private int GAME_TIME = 300, mRemainderTime, wordId;
	private final int COUNT_DOWN = 5;
	private List<Map<String, Object>> msgInfo;
	private ProcessThread mProcessThread;
	private Map<String, String> mChengyuMap;
	private int mGameScore;
	private int hintNumber;
	private int refereeUserId = -100;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				showSelectRole();
				break;
			case 1:
				switch (roleID) {
				case 0:
					mRefereeBtn.setEnabled(false);
					break;
				case 1:
					mActorBtn.setEnabled(false);
					break;
				case 2:
					mObserverBtn.setEnabled(false);
					break;
				default:
					break;
				}
				madeButtonCliclable(true);
				showToast();
				break;
			case 2:
				mRefereeBtn.setVisibility(View.GONE);
				mActorBtn.setVisibility(View.GONE);
				mObserverBtn.setVisibility(View.GONE);
				infoText.setText("请耐心等待其他人选择 \n 你是" + mRoleType.name());
				infoText.setVisibility(View.VISIBLE);
				break;
			case 3:
				infoText.setText(getString(R.string.start_game));
				infoText.setBackgroundColor(Color.BLUE);
				infoText.setClickable(true);
				infoText.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mHandler.obtainMessage(4, 0, 300).sendToTarget();
						SpeakGameInternet.this.sendMessage(Command.GAMESTART,
								wordId, RoleType.UNKONWN, 0, null);
						startFlag = false;
					}
				});
				break;
			case 4:
				showGameView(msg.arg1, msg.arg2);
				break;
			case COUNT_DOWN:
				mRemainderTime--;
				if (mRemainderTime == 0) {
					if (mCountDownTimer != null)
						mCountDownTimer.cancel();
					if (mRoleType == RoleType.REFEREE)
						mCountDownText.setText("时间到！ 得分： " + mGameScore);
					resetGameView();
					startFlag = false;
					SpeakGameInternet.this.sendMessage(Command.SCORE, wordId,
							RoleType.UNKONWN, mGameScore, null);
				} else {
					mCountDownText.setText(mRemainderTime + "");
				}
				// else {
				// SpeakGameInternet.this.sendMessage(Command.TIME, wordId,
				// RoleType.UNKONWN, mRemainderTime, null);
				// mCountDownText.setText(mRemainderTime + "");
				// }
				break;
			case 6:
				mCountDownText.setText(msg.arg1 + "");
				break;
			case 7:
				mCountDownText.setText("时间到！ 得分： " + mGameScore);
				resetGameView();
				break;
			default:
				break;
			}
		}
	};

	private void startCount() {
		if (!startFlag) {
			startFlag = true;
			countDown();
		}
	}

	private void showGameView(int id, int time) {
		hintNumber = 0;
		mChengyuMap = getChengyuRandom(id);
		if (id == 0) {
			sendMessage(Command.NEXT, wordId, RoleType.UNKONWN, 0, null);
		}
		startCount();
		mSelectRoleView.setVisibility(View.GONE);
		mGameView.setVisibility(View.VISIBLE);
		mGameWordText.setText(mChengyuMap.get(ChengyuColums.NAME));
		if (mRoleType != RoleType.OBSERVER) {
			mGameWordInfo.setText(" ※拼音： "
					+ mChengyuMap.get(ChengyuColums.PINYIN) + "\n ※释义： "
					+ mChengyuMap.get(ChengyuColums.COMMENT) + "\n ※出处： "
					+ mChengyuMap.get(ChengyuColums.ORIGINAL));
		}
		if (mRoleType == RoleType.OBSERVER) {
			mGameNextBtn.setVisibility(View.GONE);
			mGameWordText.setVisibility(View.INVISIBLE);
			mGameRightBtn.setText("提示");
			mGameWordInfo.setVisibility(View.INVISIBLE);
		}
		if (mRoleType == RoleType.ACTOR) {
			mGameRightBtn.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		gamePeopleNumber = getIntent().getIntExtra("number", 0);
		Bundle bundle = getIntent().getBundleExtra("user");
		if (bundle != null)
			gameUser = bundle.getParcelableArrayList("allUser");
		setContentView(R.layout.speak_internet_layout);
		initView();
		mProtocolCommunication = ProtocolCommunication.getInstance();
		mProtocolCommunication.registerOnCommunicationListenerExternal(this,
				APP_ID);
		mUserManager = com.zhaoyan.communication.UserManager.getInstance();
		isMainServer = com.zhaoyan.communication.UserManager
				.isManagerServer(mUserManager.getLocalUser());
		if (isMainServer)
			initRoleList();
		readyFlag = false;
		mProcessThread = new ProcessThread();
		mProcessThread.start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		processFlag = false;
		mProtocolCommunication.unregisterOnCommunicationListenerExternal(this);
		refereeUserId = -100;
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
			mCountDownTimer = null;
		}
		if (roleList != null) {
			roleList.clear();
			roleList = null;
		}
		if (gameUser != null) {
			gameUser.clear();
			gameUser = null;
		}
		if (mChengyuMap != null) {
			mChengyuMap.clear();
			mChengyuMap = null;
		}
		if (msgInfo != null) {
			msgInfo.clear();
			msgInfo = null;
		}
	}

	private void initView() {
		mLoadingView = findViewById(R.id.loading_layout);
		mSelectRoleView = findViewById(R.id.select_role_layout);
		mRefereeBtn = (Button) findViewById(R.id.referee_internet_game);
		mActorBtn = (Button) findViewById(R.id.actor_internet_game);
		mObserverBtn = (Button) findViewById(R.id.observer_internet_game);
		mGameView = findViewById(R.id.internet_game_layout);
		infoText = (TextView) findViewById(R.id.info);
		mCountDownText = (TextView) findViewById(R.id.internet_count_down);
		mGameNextBtn = (Button) findViewById(R.id.internet_speak_game_next);
		mGameRightBtn = (Button) findViewById(R.id.internet_speak_game_right);
		mGameWordText = (TextView) findViewById(R.id.internet_speak_chengyu_game_name);
		mGameWordInfo = (TextView) findViewById(R.id.internet_info_for_chengyu);
		mRefereeBtn.setOnClickListener(this);
		mActorBtn.setOnClickListener(this);
		mObserverBtn.setOnClickListener(this);
		mGameNextBtn.setOnClickListener(this);
		mGameRightBtn.setOnClickListener(this);
	}

	private void showSelectRole() {
		mLoadingView.setVisibility(View.GONE);
		mSelectRoleView.setVisibility(View.VISIBLE);
	}

	private void initRoleList() {
		if (gamePeopleNumber > 1) {
			if (roleList == null)
				roleList = new ArrayList<RoleType>();
			roleList.clear();
			roleList.add(RoleType.REFEREE);
			roleList.add(RoleType.ACTOR);
			if (gamePeopleNumber > 2) {
				for (int i = 2; i < gamePeopleNumber; i++) {
					roleList.add(RoleType.OBSERVER);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		RoleType type;
		switch (v.getId()) {
		case R.id.referee_internet_game:
			roleID = 0;
			type = RoleType.REFEREE;
			break;
		case R.id.actor_internet_game:
			roleID = 1;
			type = RoleType.ACTOR;
			break;
		case R.id.observer_internet_game:
			roleID = 2;
			type = RoleType.OBSERVER;
			break;
		case R.id.internet_speak_game_next:
			if (mRoleType != RoleType.OBSERVER) {
				showGameView(0, -1);
			}
			if (overFlag) {
				finish();
			}
			return;
		case R.id.internet_speak_game_right:
			if (mRoleType == RoleType.REFEREE) {
				mGameScore++;
				showGameView(0, -1);
			} else if (mRoleType == RoleType.OBSERVER) {
				mGameWordInfo.setVisibility(View.VISIBLE);
				if (hintNumber == 0) {
					mGameWordInfo.setText("※释义： "
							+ mChengyuMap.get(ChengyuColums.COMMENT));
				} else if (hintNumber == 1) {
					mGameWordInfo.setText("※释义： "
							+ mChengyuMap.get(ChengyuColums.COMMENT)
							+ "\n ※出处： "
							+ mChengyuMap.get(ChengyuColums.ORIGINAL));
				} else {
					mGameWordText.setVisibility(View.VISIBLE);
				}
				hintNumber++;
			}
			if (overFlag) {
				finish();
			}
			return;
		default:
			type = RoleType.OBSERVER;
			break;
		}
		madeButtonCliclable(false);
		if (isMainServer) {
			if (confirmRole(type)) {
				mRoleType = type;
				mHandler.obtainMessage(2).sendToTarget();
				if (roleList.size() == 0 && mRoleType == RoleType.REFEREE) {
					mHandler.obtainMessage(3).sendToTarget();
				} else {
					sendMessage(Command.ROLYEREADY, 0, RoleType.UNKONWN, 0,
							null);
				}
			} else {
				mHandler.obtainMessage(1).sendToTarget();
			}
		} else {
			sendMessage(Command.ROLE, 0, type, 0, mUserManager.getServer());
		}
	}

	private boolean confirmRole(RoleType type) {
		int num = roleList.indexOf(type);
		if (num > -1) {
			roleList.remove(num);
			// if (roleList.size() == 0) {
			// if (mRoleType == RoleType.REFEREE)
			// mHandler.obtainMessage(3).sendToTarget();
			// else
			// sendMessage(Command.ROLYEREADY, 0, RoleType.UNKONWN, 0,
			// null);
			// }
			return true;
		}
		return false;
	}

	private void madeButtonCliclable(boolean flag) {
		mRefereeBtn.setClickable(flag);
		mActorBtn.setClickable(flag);
		mObserverBtn.setClickable(flag);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!readyFlag) {
			readyFlag = true;
			if (!isMainServer) {
				try {
					Thread.sleep(100);
				} catch (Exception exception) {
				}
				sendMessage(Command.READY, 0, RoleType.UNKONWN, 0,
						mUserManager.getServer());
			}
		}
	}

	@Override
	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return null;
	}

	private void sendMessage(Command cmd, int word, RoleType type, int time,
			User u) {
		Log.e("ArbiterLiu", "" + cmd + "---------" + System.currentTimeMillis());
		SpeakGameMsg.Builder builder = SpeakGameMsg.newBuilder();
		builder.setGame(GameType.SPEAK);
		builder.setCommand(cmd);
		builder.setTime(time);
		builder.setWord(word);
		builder.setType(type);
		if (u != null) {
			mProtocolCommunication.sendMessageToSingle(builder.build()
					.toByteArray(), u, APP_ID);
		} else {
			mProtocolCommunication.sendMessageToAll(builder.build()
					.toByteArray(), APP_ID);
		}

	}

	@Override
	public void onReceiveMessage(byte[] arg0, User arg1) throws RemoteException {
		// TODO Auto-generated method stub
		boolean flag = false;
		SpeakGameMsg msg = SpeakMessageSend.getInstance().parseMsg(arg0);
		Log.e("ArbiterLiu",
				msg.getCommand() + "   " + System.currentTimeMillis());
		if (gameUser != null) {
			try {
				for (User u : gameUser) {
					if (u.getUserID() == arg1.getUserID()) {
						flag = true;
					}
				}
			} catch (Exception exception) {
				return;
			}
			if (!flag)
				return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("msg", msg);
		map.put("user", arg1);
		if (msgInfo == null) {
			msgInfo = new ArrayList<Map<String, Object>>();
		}
		msgInfo.add(map);
	}

	@Override
	public void onUserConnected(User arg0) throws RemoteException {
		// TODO Auto-generated method stub
		int i = -1;
		for (User u : gameUser) {
			i++;
			if (u.getUserID() == arg0.getUserID()) {
				gamePeopleNumber--;
				break;
			}
		}
		gameUser.remove(i);

	}

	@Override
	public void onUserDisconnected(User arg0) throws RemoteException {
		// TODO Auto-generated method stub

	}

	private void processMessage() {
		if (msgInfo == null || msgInfo.size() == 0) {
			return;
		}
		Map<String, Object> map = msgInfo.remove(0);
		SpeakGameMsg msg = (SpeakGameMsg) map.get("msg");
		User arg1 = (User) map.get("user");
		// Log.e("ArbiterLiu",
		// msg.getCommand() + " msgInfo size   " + msgInfo.size());
		if (GameType.SPEAK == msg.getGame()) {
			switch (msg.getCommand().getNumber()) {
			case SpeakGameMsg.Command.READY_VALUE:
				if (isMainServer) {
					readyAckNumber++;
					if (readyAckNumber == (gamePeopleNumber - 1)) {
						sendMessage(Command.READY, 0, RoleType.UNKONWN, 0, null);
						mHandler.sendEmptyMessage(0);
					}
				} else if (com.zhaoyan.communication.UserManager
						.isManagerServer(arg1)) {
					mHandler.sendEmptyMessage(0);
				}
				break;
			case Command.ROLE_VALUE:
				if (isMainServer) {
					RoleType t = msg.getType();
					if (t != null) {
						if (confirmRole(t)) {
							sendMessage(Command.ROLE, 0, t, 0, arg1);
						} else {
							sendMessage(Command.ROLE, 0, RoleType.UNKONWN, 0,
									arg1);
						}
						if (roleList.size() == 0) {
							if (mRoleType == RoleType.REFEREE) {
								mHandler.obtainMessage(3).sendToTarget();
							} else {
								sendMessage(Command.ROLYEREADY, 0,
										RoleType.UNKONWN, 0, null);
							}
						}
					}
				} else {
					RoleType te = msg.getType();
					if (RoleType.UNKONWN == te) {
						mHandler.sendEmptyMessage(1);
					} else {
						mRoleType = te;
						mHandler.sendEmptyMessage(2);
					}
				}
				break;
			case Command.NEXT_VALUE:
				mHandler.obtainMessage(4, msg.getWord(), msg.getTime())
						.sendToTarget();
				break;
			case Command.TIME_VALUE:
				mHandler.obtainMessage(6, msg.getTime(), 0).sendToTarget();
				break;
			case Command.GAMESTART_VALUE:
				if (refereeUserId == -100)
					refereeUserId = arg1.getUserID();
				startFlag = false;
				break;
			case Command.ROLYEREADY_VALUE:
				if (com.zhaoyan.communication.UserManager.isManagerServer(arg1)) {
					if (mRoleType == RoleType.REFEREE) {
						mHandler.sendEmptyMessage(3);
					}
				}
				break;
			case Command.SCORE_VALUE:
				mGameScore = msg.getTime();
				mHandler.sendEmptyMessage(7);
				break;
			default:
				break;
			}
		}
		// Log.e("ArbiterLiu",
		// "------------------process message end----------------------------");
	}

	private void showToast() {
		String temp = null;
		switch (roleID) {
		case 0:
			temp = "裁判员已被选择，请选其他角色";
			break;
		case 1:
			temp = "表演者已被选择，请选其他角色";
			break;
		case 2:
			temp = "观察者已被选择，请选其他角色";
		default:
			break;
		}
		Toast.makeText(this, temp + "", Toast.LENGTH_SHORT).show();
	}

	/** maybe return null ,please check the return value */
	private Map<String, String> getChengyuRandom(int id) {
		Log.e("ArbiterLiu", "id                " + id);
		if (id < 1) {
			if (mRandom == null)
				mRandom = new Random();
			id = Math.abs(mRandom.nextInt()) % MainActivity.DB_NUMBER;
		}
		Cursor cursor = getContentResolver().query(
				ChengyuColums.CONTENT_URI,
				new String[] { ChengyuColums.NAME, ChengyuColums.PINYIN,
						ChengyuColums.COMMENT, ChengyuColums.ORIGINAL,
						ChengyuColums.EXAMPLE, ChengyuColums.ENGLISH,
						ChengyuColums.SIMILAR, ChengyuColums.OPPOSITE },
				"_id = " + id, null, null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put(ChengyuColums.NAME, cursor.getString(0));
				map.put(ChengyuColums.PINYIN, cursor.getString(1));
				map.put(ChengyuColums.COMMENT, cursor.getString(2));
				map.put(ChengyuColums.ORIGINAL, cursor.getString(3));
				map.put(ChengyuColums.EXAMPLE, cursor.getString(4));
				map.put(ChengyuColums.ENGLISH, cursor.getString(5));
				map.put(ChengyuColums.SIMILAR, cursor.getString(6));
				map.put(ChengyuColums.OPPOSITE, cursor.getString(7));
				cursor.close();
				wordId = id;
				return map;
			}
			cursor.close();
		}
		return null;

	}

	private void countDown() {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
			mCountDownTimer = null;
		}
		mRemainderTime = GAME_TIME;
		mCountDownTimer = new Timer();
		mCountDownTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(COUNT_DOWN);
			}
		}, 1000, 1000);

	}

	private class ProcessThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while (processFlag) {
				processMessage();
			}
		}
	}

	private void resetGameView() {
		overFlag = true;
		mGameRightBtn.setText("重来");
		mGameNextBtn.setText("结束");
		mGameNextBtn.setVisibility(View.VISIBLE);
		mGameRightBtn.setVisibility(View.VISIBLE);
	}
}
