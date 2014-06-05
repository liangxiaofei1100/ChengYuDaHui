package com.zhaoyan.juyou.game.chengyudahui.speakgame;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.zhaoyan.juyou.game.chengyudahui.MainActivity;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.ChengyuData.ChengyuColums;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SpeakGameActivity extends Activity implements OnClickListener {
	private Button mLocalBtn, mInterBtn, mStartGameBtn, mLocalRightBtn,
			mLocalChangeBtn;
	private View mModeSelectView, mLocalGameView, mInterGameView;
	private int mGameMode = -1;// if 0 ,local mode; else if 1 ,Internet mode
	private Random mRandom;
	private TextView mLocalChengyuName, mLocalCountDown, mLocalInfoForChengyu;
	private int mGameSocre, mRemainderTime;
	private Timer mCountDownTimer;
	private final int COUNT_DOWN = 0, GAME_TIME = 300;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speak_game_layout);
		mLocalBtn = (Button) findViewById(R.id.local_mode);
		mInterBtn = (Button) findViewById(R.id.internet_mode);
		mModeSelectView = findViewById(R.id.mode_select_layout);
		mLocalGameView = findViewById(R.id.local_game_layout);
		mInterGameView = findViewById(R.id.internet_game_layout);
		mStartGameBtn = (Button) findViewById(R.id.speak_game_start);
		mLocalChangeBtn = (Button) findViewById(R.id.speak_game_next);
		mLocalChengyuName = (TextView) findViewById(R.id.speak_chengyu_game_name);
		mLocalRightBtn = (Button) findViewById(R.id.speak_game_right);
		mLocalCountDown = (TextView) findViewById(R.id.count_down);
		mLocalInfoForChengyu = (TextView) findViewById(R.id.info_for_chengyu);

		mLocalBtn.setOnClickListener(this);
		mInterBtn.setOnClickListener(this);
		mStartGameBtn.setOnClickListener(this);
		mLocalChangeBtn.setOnClickListener(this);
		mLocalRightBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.local_mode:
			mModeSelectView.setVisibility(View.GONE);
			mStartGameBtn.setVisibility(View.VISIBLE);
			mGameMode = 0;
			break;
		case R.id.internet_mode:
			mModeSelectView.setVisibility(View.GONE);
			mStartGameBtn.setVisibility(View.VISIBLE);
			mGameMode = 1;
			break;
		case R.id.speak_game_start:// if mode==-1,change it to be 0,for local
									// mode
			mStartGameBtn.setVisibility(View.GONE);
			if (mGameMode != 0 && mGameMode != 1) {
				mGameMode = 0;
			}
			startGame(mGameMode);
			break;
		case R.id.speak_game_next:
			initGameWord();
			break;
		case R.id.speak_game_right:
			mGameSocre++;
			initGameWord();
			break;
		default:
			break;
		}

	}

	private void startGame(int gameMode) {
		mGameSocre = 0;
		switch (gameMode) {
		case 0:
			mLocalGameView.setVisibility(View.VISIBLE);
			initGameWord();
			countDown();
			break;
		case 1:

			break;
		default:
			break;
		}

	}

	private void initGameWord() {
		Map<String, String> map = getChengyuRandom();
		mLocalChengyuName.setTag(map);
		mLocalChengyuName.setText(map.get(ChengyuColums.NAME));
		if (mGameMode == 0)
			mLocalInfoForChengyu.setText(" ※拼音： "
					+ map.get(ChengyuColums.PINYIN) + "\n ※释义： "
					+ map.get(ChengyuColums.COMMENT) + "\n ※出处： "
					+ map.get(ChengyuColums.ORIGINAL));
	}

	/** maybe return null ,please check the return value */
	private Map<String, String> getChengyuRandom() {
		if (mRandom == null)
			mRandom = new Random();
		int id = Math.abs(mRandom.nextInt()) % MainActivity.DB_NUMBER;
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

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case COUNT_DOWN:
				mRemainderTime--;
				if (mRemainderTime < 1) {
					if (mCountDownTimer != null) {
						mCountDownTimer.cancel();
						mCountDownTimer = null;
					}
					mLocalCountDown.setText("时间到！ 得分： " + mGameSocre);
				} else {
					mLocalCountDown.setText("" + mRemainderTime);
				}
				break;

			default:
				break;
			}
		}

	};
}
