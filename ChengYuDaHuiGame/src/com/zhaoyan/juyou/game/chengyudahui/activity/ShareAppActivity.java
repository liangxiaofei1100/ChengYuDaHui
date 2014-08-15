package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class ShareAppActivity extends ActionBarActivity {
	private static final String TAG = ShareAppActivity.class.getSimpleName();
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("应用分享");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mContext = this;
		

	}
}
