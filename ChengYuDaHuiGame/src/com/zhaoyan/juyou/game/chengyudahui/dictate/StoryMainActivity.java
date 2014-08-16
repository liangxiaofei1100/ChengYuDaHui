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
	
	private List<StoryInfo> mList = new ArrayList<StoryInfo>();
	
	private ViewPager mDirectionalViewPager;
	private StoryPagerAdapter mPagerAdapter;
	private CustomIndicator mIndicator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.story_main);
		setTitle("天天听故事");
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		StoryInfo info = null;
		for (int i = 0; i < 55; i++) {
			info = new StoryInfo();
			info.setTitle("TEST" + i);
			mList.add(info);
		}
		
		mIndicator = (CustomIndicator) findViewById(R.id.story_tv_count);
		
		mDirectionalViewPager = (ViewPager) findViewById(R.id.story_viewpager);
		mPagerAdapter = new StoryPagerAdapter(this, mList, mIndicator, 6);
		mDirectionalViewPager.setAdapter(mPagerAdapter);
		mDirectionalViewPager.setOnPageChangeListener(listener);
		
	}
	
	private OnPageChangeListener listener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			mIndicator.setCurrentPosition(arg0);
			mPagerAdapter.setCurrentPage(arg0);
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
