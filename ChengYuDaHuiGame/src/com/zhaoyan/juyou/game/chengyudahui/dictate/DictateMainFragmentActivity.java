package com.zhaoyan.juyou.game.chengyudahui.dictate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;

public class DictateMainFragmentActivity extends FragmentActivity {

	private static final String TAG = DictateMainFragmentActivity.class
			.getSimpleName();

	private TextView mTitleView;

	private int mPosition = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.knowledge_activity_main);

		mTitleView = (TextView) findViewById(R.id.tv_item_title);

		selectItem(mPosition);
	}

	public void selectItem(int position) {
		Log.d(TAG, "selectItem.position=" + position);
		mPosition = position;
		Fragment fragment = null;
		switch (position) {
		case 0:
			//汉字听写主界面
			fragment = new DictateMainFragment();
			break;
		case 1:
			//听听天故事
			fragment = new StoryMainFragment();
			break;
		case 2:
		case 3:
			Bundle args = new Bundle();
			if (position == 2) {
				//常用字书写
				args.putString("level","中级");
			} else {
				//生僻字书写
				args.putString("level","高级");
			}
			fragment = new DictateItemFragment();
			fragment.setArguments(args);
			break;
		default:
			// default
			fragment = new DictateMainFragment();
			break;
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.fl_knowledge_item, fragment).commit();
	}

	public void setTitle(String title) {
		mTitleView.setText(title);
	}

	public void onClickBack(View view) {
		if (mPosition != 0) {
			selectItem(0);
		} else {
			DictateMainFragmentActivity.this.finish();
		}
	}
}
