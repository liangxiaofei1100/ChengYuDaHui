package com.zhaoyan.juyou.game.chengyudahui.speakgame;

import com.zhaoyan.juyou.game.chengyudahui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SpeakGameActivity extends Activity implements OnClickListener {
	private Button mLocalBtn, mInterBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speak_game_layout);
		mLocalBtn = (Button) findViewById(R.id.local_mode);
		mInterBtn = (Button) findViewById(R.id.internet_mode);
		mLocalBtn.setOnClickListener(this);
		mInterBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.local_mode:

			break;
		case R.id.internet_mode:
			
			break;

		default:
			break;
		}

	}

}
