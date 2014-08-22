package com.zhaoyan.juyou.game.chengyudahui.knowledge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.BackgroundMusicBaseActivity;

public class KnowledgeMainActivity extends BackgroundMusicBaseActivity {
	private static final String TAG = KnowledgeMainActivity.class.getSimpleName();
	
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
			fragment = new KnowledgeMainFragment();
			break;
		case 1:
			fragment = new GuessPictureFragment();
			break;

		default:
			//default
			fragment = new ChengYuJieLongFragment();
			break;
		}
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.fl_knowledge_item, fragment).commit();
	}
	
	public void setTitle(String title){
		mTitleView.setText(title);
	}
	
	public void onClickBack(View view){
		if (mPosition != 0) {
			selectItem(0);
		} else {
			KnowledgeMainActivity.this.finish();
		}
	}

}
