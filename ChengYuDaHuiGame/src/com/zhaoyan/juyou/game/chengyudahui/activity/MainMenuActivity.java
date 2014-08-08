package com.zhaoyan.juyou.game.chengyudahui.activity;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.account.ZhaoYanAccount;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.dictate.DictateActivity;
import com.zhaoyan.juyou.game.chengyudahui.knowledge.KnowledgeMainActivity;
import com.zhaoyan.juyou.game.chengyudahui.speakgame.SpeakGameActivity;
import com.zhaoyan.juyou.game.chengyudahui.spy.SpyMainActivity;
import com.zhaoyan.juyou.game.chengyudahui.study.StudyActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Main menu of the App.
 * 
 */
public class MainMenuActivity extends Activity {
	private static final String TAG = MainMenuActivity.class.getSimpleName();
	private Context mContext;

	private TextView mGoldTextView;
	private TextView mJifenTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		mContext = this;

		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ZhaoYanAccount account = ZhaoYanAccountManager
				.getAccountFromLocal(mContext);
		if (account != null) {
			mGoldTextView.setText(String.valueOf(account.gold));
			mJifenTextView.setText(String.valueOf(account.jifen));	
		}
	}

	private void initView() {
		mGoldTextView = (TextView) findViewById(R.id.tv_gold);
		mJifenTextView = (TextView) findViewById(R.id.tv_jifen);
	}

	public void launchChengYuStudy(View view) {
		Log.d(TAG, "launchChengYuStudy");
		Intent intent = new Intent(mContext, ChengYuDictionary.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
	}

	public void launchSpeakGuessGame(View view) {
		Log.d(TAG, "launchSpeakGuessGame");
		Intent intent = new Intent(mContext, SpeakGameActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
	}

	public void launchKnowledgeGame(View view) {
		Log.d(TAG, "launchKnowledgeGame");
		Intent intent = new Intent(mContext, KnowledgeMainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
	}
	public void launchChengyuDictatet(View view){
		Log.d(TAG, "launchChengyuDictatet");
		Intent intent = new Intent(mContext, DictateActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
	}

	public void launchChengYuDaHui(View view) {
		Log.d(TAG, "launchChengYuDaHui");
		Intent intent = new Intent(mContext, SpeakGameActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
	}
	
	public void launchSpyGame(View view){
		Log.d(TAG, "launchSpyGame");
		Intent intent = new Intent(mContext, SpyMainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
	}

	public void launchJifen(View view) {
		Log.d(TAG, "launchJifen");
		Intent intent = new Intent(mContext, GetGoldActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
	}
}
