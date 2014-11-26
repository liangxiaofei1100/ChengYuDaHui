package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.view.ActionBar;
import com.zhaoyan.juyou.game.chengyudahui.view.ActionBar.OnActionBarListener;

public class BaseZyActivity extends Activity {
	protected Context mContext;
	private ActionBar mActionBar;
	private Toast mToast = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
	}
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(R.layout.base_layout);
		if (layoutResID > 0)
			LayoutInflater.from(this).inflate(layoutResID,
					(ViewGroup) findViewById(R.id.root_layout));
		
		mActionBar = (ActionBar) findViewById(R.id.zy_action_bar);
		mActionBar.setOnActionBarListener(new OnActionBarListener() {
			@Override
			public void onActionBarItemClicked(int position) {
				if (position == ActionBar.OnActionBarListener.HOME_ITEM) {
					finish();
					return;
				}
			}
		});
	}
	
	public ActionBar getZyActionBar(){
		return mActionBar;
	}
	
	public void showToast(String message) {
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
		mToast.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
}
