package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.zhaoyan.juyou.game.chengyudahui.R;

public class BaseFragmentActivity extends FragmentActivity {

	// title view
	protected View mCustomTitleView;
	protected TextView mTitleNameView;

	protected void initTitle(int titleName) {
		mCustomTitleView = findViewById(R.id.title);

		// title name view
		mTitleNameView = (TextView) mCustomTitleView
				.findViewById(R.id.tv_title_name);
		mTitleNameView.setText(titleName);

	}

}
