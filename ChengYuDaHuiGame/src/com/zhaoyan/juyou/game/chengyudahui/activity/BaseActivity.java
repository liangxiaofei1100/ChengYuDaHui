package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.WindowManager;

public class BaseActivity extends ActionBarActivity {
	protected Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		return super.onOptionsItemSelected(item);
	}

}
