package com.zhaoyan.juyou.game.chengyudahui.knowledge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;

public class KnowledgeMainActivity extends FragmentActivity {
	private static final String TAG = KnowledgeMainActivity.class.getSimpleName();
	
	private TextView mTitleView;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.knowledge_activity_main);
		
		mTitleView = (TextView) findViewById(R.id.tv_item_title);
		
		selectItem(0);
	}
	
	public void selectItem(int position) {
		Log.d(TAG, "selectItem.position=" + position);
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
			fragment = new GuessPictureFragment();
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
		KnowledgeMainActivity.this.finish();
	}

}
