package com.zhaoyan.juyou.game.chengyudahui.speakgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
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
	private View mLoadingView, mSelectRoleView, mGameView, mRuleSettingView,
			mLoadingText, mGameNextBtn, mGameRightBtn, rightLayout, nextLayout,
			mBackView;
	private TextView infoText, mCountDownText, mGameWordText, mGameWordInfo,
			mRigntTv, mNextTv;
	private Button mRefereeBtn, mActorBtn, mObserverBtn, mRuleStartBtn;
	private EditText mTimeEditText, mRightNumberEditText, mWrongNumberEditText,
			mPassNumberEditText;
	private ProtocolCommunication mProtocolCommunication;
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
	private final int COUNT_DOWN = 5, SELECT_ROLE = 0, ROLE_SELECTED = 1,
			ROLE_CONFIRMED = 2, READY_START = 3, NEXT_VALUE = 4,
			TIME_UDATE = 6, GAME_OVER = 7, RULE_INIT = 100;
	private List<Map<String, Object>> msgInfo;
	private ProcessThread mProcessThread;
	private Map<String, String> mChengyuMap;
	private int mGameScore, mGamePass, mGameWrong;
	private int hintNumber;
	private int refereeUserId = -100;
	private int mGameTime, mRightNumber, mWrongNumber, mPassNumber;
	private Cursor chengyuCursor;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SELECT_ROLE:
				showSelectRole();
				break;
			case ROLE_SELECTED:
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
			case ROLE_CONFIRMED:
				mRefereeBtn.setVisibility(View.GONE);
				mActorBtn.setVisibility(View.GONE);
				mObserverBtn.setVisibility(View.GONE);
				infoText.setText("请耐心等待其他人选择 \n 你是" + mRoleType.name());
				infoText.setVisibility(View.VISIBLE);
				break;
			case READY_START:
				infoText.setText(getString(R.string.start_game));
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
			case NEXT_VALUE:
				if (mRoleType == RoleType.REFEREE) {
					mGamePass++;
					if (mPassNumber > 0 && mGamePass == mPassNumber) {
						// TODO Game Over
						gameOver();
					} else
						showGameView(0, -1);
				} else
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
				break;
			case TIME_UDATE:
				mCountDownText.setText(msg.arg1 + "");
				break;
			case GAME_OVER:
				mCountDownText.setText("游戏结束： " + mGameScore);
				resetGameView();
				break;
			case RULE_INIT:
				ruleSetting();
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
		mChengyuMap = getChengyuRandomByLimit(id);
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
		if (mRoleType == RoleType.REFEREE) {
			mNextTv.setText("错误");
		}
		if (mRoleType == RoleType.OBSERVER) {
			nextLayout.setVisibility(View.GONE);
			mGameWordText.setVisibility(View.INVISIBLE);
			mRigntTv.setText("提示");
			mRigntTv.setVisibility(View.INVISIBLE);
		}
		if (mRoleType == RoleType.ACTOR) {
			mRigntTv.setText("略过");
			nextLayout.setVisibility(View.GONE);
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
		if (chengyuCursor != null) {
			chengyuCursor.close();
			chengyuCursor = null;
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
		mGameNextBtn = findViewById(R.id.internet_speak_game_next);
		mGameRightBtn = findViewById(R.id.internet_speak_game_right);
		mRigntTv = (TextView) findViewById(R.id.tv_right_speak_internet);
		mNextTv = (TextView) findViewById(R.id.tv_next_speak_internet);
		rightLayout = findViewById(R.id.internet_right_layout);
		nextLayout = findViewById(R.id.internet_next_layout);
		mGameWordText = (TextView) findViewById(R.id.internet_speak_chengyu_game_name);
		mGameWordInfo = (TextView) findViewById(R.id.internet_info_for_chengyu);
		mRuleSettingView = findViewById(R.id.setting_rule_speak);
		mRuleStartBtn = (Button) findViewById(R.id.rule_start);
		mTimeEditText = (EditText) findViewById(R.id.speak_time_setting_edit);
		mRightNumberEditText = (EditText) findViewById(R.id.speak_right_number_edit);
		mWrongNumberEditText = (EditText) findViewById(R.id.speak_wrong_number_edit);
		mPassNumberEditText = (EditText) findViewById(R.id.speak_pass_number_edit);
		mLoadingText = findViewById(R.id.loading_text);
		mBackView = findViewById(R.id.iv_back_speak_in);
		mRefereeBtn.setOnClickListener(this);
		mActorBtn.setOnClickListener(this);
		mObserverBtn.setOnClickListener(this);
		mGameNextBtn.setOnClickListener(this);
		mGameRightBtn.setOnClickListener(this);
		mRuleStartBtn.setOnClickListener(this);
		mBackView.setOnClickListener(this);
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
			if (mRoleType == RoleType.REFEREE) {
				mGameWrong++;
				if (mWrongNumber > 1 && mWrongNumber == mGameWrong) {
					// TODO GAME OVER
					gameOver();
				} else {
					showGameView(0, -1);
				}
			}
			if (overFlag) {
				finish();
			}
			return;
		case R.id.internet_speak_game_right:
			if (mRoleType == RoleType.REFEREE) {
				mGameScore++;
				if (mGameScore == mRightNumber) {
					// TODO Game Over
					gameOver();
				} else
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
			} else if (mRoleType == RoleType.ACTOR) {
				sendMessage(Command.NEXT, -1, mRoleType, -1, null);
				// mGamePass++;
				// if (mPassNumber > 0 && mGamePass == mPassNumber) {
				// // TODO Game Over
				// gameOver();
				// } else
				// showGameView(0, -1);
			}
			if (overFlag) {
				finish();
			}
			return;
		case R.id.rule_start:
			initRule();
			return;
		case R.id.iv_back_speak_in:
			finish();
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
					Thread.sleep(1000);
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
		if (GameType.SPEAK == msg.getGame()) {
			switch (msg.getCommand().getNumber()) {
			case SpeakGameMsg.Command.READY_VALUE:
				if (isMainServer) {
					readyAckNumber++;
					if (readyAckNumber == (gamePeopleNumber - 1)) {
						mHandler.sendEmptyMessage(RULE_INIT);
					}
				} else if (com.zhaoyan.communication.UserManager
						.isManagerServer(arg1)) {
					mHandler.sendEmptyMessage(SELECT_ROLE);
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
								mHandler.obtainMessage(READY_START)
										.sendToTarget();
							} else {
								sendMessage(Command.ROLYEREADY, 0,
										RoleType.UNKONWN, 0, null);
							}
						}
					}
				} else {
					RoleType te = msg.getType();
					if (RoleType.UNKONWN == te) {
						mHandler.sendEmptyMessage(ROLE_SELECTED);
					} else {
						mRoleType = te;
						mHandler.sendEmptyMessage(ROLE_CONFIRMED);
					}
				}
				break;
			case Command.NEXT_VALUE:
				if (msg.getWord() == -1 && mRoleType == RoleType.REFEREE) {
					mHandler.obtainMessage(NEXT_VALUE, msg.getWord(),
							msg.getTime()).sendToTarget();
				} else if (msg.getWord() > -1) {
					mHandler.obtainMessage(NEXT_VALUE, msg.getWord(),
							msg.getTime()).sendToTarget();
				}
				break;
			case Command.TIME_VALUE:
				mHandler.obtainMessage(TIME_UDATE, msg.getTime(), 0)
						.sendToTarget();
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
				mHandler.sendEmptyMessage(GAME_OVER);
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

	/**
	 * maybe return null ,please check the return value
	 * */
	private Map<String, String> getChengyuRandom(int id) {
		if (id < 1) {
			if (mRandom == null)
				mRandom = new Random();
			id = Math.abs(mRandom.nextInt()) % MainActivity.DB_NUMBER;
		}
		Cursor cursor = getContentResolver().query(
				ChengyuColums.CONTENT_URI,
				new String[] { ChengyuColums.NAME, ChengyuColums.PINYIN,
						ChengyuColums.COMMENT, ChengyuColums.ORIGINAL,
						ChengyuColums.EXAMPLE }, "_id = " + id, null, null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put(ChengyuColums.NAME, cursor.getString(0));
				map.put(ChengyuColums.PINYIN, cursor.getString(1));
				map.put(ChengyuColums.COMMENT, cursor.getString(2));
				map.put(ChengyuColums.ORIGINAL, cursor.getString(3));
				map.put(ChengyuColums.EXAMPLE, cursor.getString(4));
				cursor.close();
				wordId = id;
				return map;
			}
			cursor.close();
		}
		return null;

	}

	private Map<String, String> getChengyuRandomByLimit(int id) {
		if (mRandom == null)
			mRandom = new Random();
		if (id < 1) {
			if (chengyuCursor == null) {
				chengyuCursor = getContentResolver().query(
						ChengyuColums.CONTENT_URI,
						new String[] { ChengyuColums.NAME,
								ChengyuColums.PINYIN, ChengyuColums.COMMENT,
								ChengyuColums.ORIGINAL, ChengyuColums.EXAMPLE,
								"_id" }, ChengyuColums.CAICI + " = 1", null,
						null);
				if (chengyuCursor == null) {
					Log.e(SpeakGameInternet.class.getSimpleName(),
							"can not find the caici word");
					return null;
				}
			}
			int num = Math.abs(mRandom.nextInt()) % (chengyuCursor.getCount());
			chengyuCursor.move(num);
			Map<String, String> map = new HashMap<String, String>();
			map.put(ChengyuColums.NAME, chengyuCursor.getString(0));
			map.put(ChengyuColums.PINYIN, chengyuCursor.getString(1));
			map.put(ChengyuColums.COMMENT, chengyuCursor.getString(2));
			map.put(ChengyuColums.ORIGINAL, chengyuCursor.getString(3));
			map.put(ChengyuColums.EXAMPLE, chengyuCursor.getString(4));
			wordId = chengyuCursor.getInt(5);
			chengyuCursor.close();
			chengyuCursor = null;
			return map;
		} else {
			return getChengyuRandom(id);
		}
	}

	private void countDown() {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
			mCountDownTimer = null;
		}
		if (mGameTime > 1) {
			mRemainderTime = mGameTime;
		} else
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
		mRigntTv.setText("重来");
		mNextTv.setText("结束");
		nextLayout.setVisibility(View.VISIBLE);
		rightLayout.setVisibility(View.VISIBLE);
	}

	private void ruleSetting() {
		mLoadingText.setVisibility(View.GONE);
		mRuleSettingView.setVisibility(View.VISIBLE);
	}

	private void initRule() {
		mGameTime = getNumber(mTimeEditText);
		mRightNumber = getNumber(mRightNumberEditText);
		mWrongNumber = getNumber(mWrongNumberEditText);
		mPassNumber = getNumber(mPassNumberEditText);
		sendMessage(Command.READY, 0, RoleType.UNKONWN, 0, null);
		mHandler.sendEmptyMessage(SELECT_ROLE);
	}

	private int getNumber(final EditText s) {
		if (s != null) {
			String temp = s.getText().toString();
			if (temp != null)
				try {
					int n = Integer.valueOf(temp);
					return n;
				} catch (NumberFormatException e) {
					Log.e("ArbiterLiu", "It is not number " + s);
				}
		}
		return -1;

	}

	private void gameOver() {
		if (mCountDownTimer != null)
			mCountDownTimer.cancel();
		resetGameView();
		startFlag = false;
		SpeakGameInternet.this.sendMessage(Command.SCORE, wordId,
				RoleType.UNKONWN, mGameScore, null);
	}
}
