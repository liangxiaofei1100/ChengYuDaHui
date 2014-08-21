package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.zhaoyan.juyou.game.chengyudahui.R;

public class SettingActivity extends BackgroundMusicBaseActivity {
	private static final String TAG = SettingActivity.class.getSimpleName();
	public static final String PREFERENCE_SETTING_NAME = "setting";
	public static final String PREFERENCE_KEY_BACKGROUND_MUSIC = "music";
	private CheckBox mMusicCheckBox;
	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_setting);

		mSharedPreferences = getSharedPreferences(PREFERENCE_SETTING_NAME,
				Context.MODE_PRIVATE);
		initView();
	}

	private void initView() {
		mMusicCheckBox = (CheckBox) findViewById(R.id.cb_music);
		if (isBackgroundMusicEnabled()) {
			mMusicCheckBox.setChecked(true);
		} else {
			mMusicCheckBox.setChecked(false);
		}
		mMusicCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean checked) {
						setBackgroundMusicEnable(checked);
					}

				});
	}

	public void settingMusic(View view) {
		if (mMusicCheckBox.isChecked()) {
			mMusicCheckBox.setChecked(false);
		} else {
			mMusicCheckBox.setChecked(true);
		}
	}

	private boolean isBackgroundMusicEnabled() {
		return mSharedPreferences.getBoolean(PREFERENCE_KEY_BACKGROUND_MUSIC,
				true);
	}

	private void setBackgroundMusicEnable(boolean enable) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(PREFERENCE_KEY_BACKGROUND_MUSIC, enable);
		editor.apply();
	}
}
