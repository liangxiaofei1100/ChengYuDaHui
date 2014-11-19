package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

public class BaseActivity extends ActionBarActivity {
	protected Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}
	
	public void initItemActionBar(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);//invisiable logo
		setTitleColor(Color.WHITE);
	}
	
	public void setTitleColor(int color){
		int titleId = Resources.getSystem().getIdentifier(  
                "action_bar_title", "id", "android");  
		TextView titleView = (TextView) findViewById(titleId); 
		titleView.setTextColor(color);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_MENU == keyCode) {
			try {
				if (getSupportActionBar() == null
						|| !getSupportActionBar().isShowing()) {
					return true;
				}
			} catch (Exception e) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
