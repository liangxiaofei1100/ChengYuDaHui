package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.zhaoyan.juyou.game.chengyudahui.R;

public class StoryMainActivity extends ActionBarActivity {
	private static final String TAG = StoryMainActivity.class.getSimpleName();
	
	private List<String> mList = new ArrayList<String>();
	
	private ViewPager mDirectionalViewPager;
	private StoryPagerAdapter mPagerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.story_main);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		for (int i = 0; i < 300; i++) {
			mList.add("TEST" + i);
		}
		
		mDirectionalViewPager = (ViewPager) findViewById(R.id.story_dvp);
		mPagerAdapter = new StoryPagerAdapter(this, mList,10);
		mDirectionalViewPager.setAdapter(mPagerAdapter);
		mDirectionalViewPager.setOnPageChangeListener(listener);
	}
	
	private OnPageChangeListener listener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
//			indicator.setCurrentPosition(arg0);
			System.out.println(arg0 + "");
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
