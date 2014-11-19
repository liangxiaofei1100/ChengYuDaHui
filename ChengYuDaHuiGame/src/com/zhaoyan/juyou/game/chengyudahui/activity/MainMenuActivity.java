package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhaoyan.juyou.account.ZhaoYanAccount;
import com.zhaoyan.juyou.account.ZhaoYanAccountManager;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.fragment.TestFragment;
import com.zhaoyan.juyou.game.chengyudahui.study.StudyFragment;

/**
 * Main menu of the App.
 */
public class MainMenuActivity extends BackgroundMusicBaseActivity {
	private static final String TAG = MainMenuActivity.class.getSimpleName();
	private Context mContext;
	
	private Button[] mTabButtons;
	private int mTabIndex;
	private int mCurrentTabIndex;

	private ViewPager mViewPager;
	private MainPagerAdapter mPagerAdapter;
	
	private TextView mTitleLabelView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		mContext = this;

		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ZhaoYanAccount account = ZhaoYanAccountManager
				.getAccountFromLocal(mContext);
	}
	
	public void setTitleLabel(String title){
		mTitleLabelView.setText(title);
	}
	
	public void setTitleLabel(int resId){
		mTitleLabelView.setText(resId);
	}

	private void initView() {
		mTitleLabelView = (TextView) findViewById(R.id.tv_title_label);
		
		mTabButtons = new Button[4];
		mTabButtons[0] = (Button) findViewById(R.id.tab_btn_study);
		mTabButtons[1] = (Button) findViewById(R.id.tab_btn_interaction);
		mTabButtons[2] = (Button) findViewById(R.id.tab_btn_friend);
		mTabButtons[3] = (Button) findViewById(R.id.tab_btn_me);
		mTabButtons[0].setSelected(true);
		mTitleLabelView.setText(R.string.main_tab_set_study);
		
		mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
		mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(new MainPageChangeListener());
		mViewPager.setCurrentItem(0);
	}
	
	public void onTabSelect(View view) {
		switch (view.getId()) {
		case R.id.tab_btn_study:
			mTabIndex = 0;
			break;
		case R.id.tab_btn_interaction:
			mTabIndex = 1;
			break;
		case R.id.tab_btn_friend:
			mTabIndex = 2;
			break;
		case R.id.tab_btn_me:
			mTabIndex = 3;
			break;
		}
		
		if (mCurrentTabIndex != mTabIndex) {
			//tab
			mViewPager.setCurrentItem(mTabIndex, false);
		}
		
		mTabButtons[mCurrentTabIndex].setSelected(false);
		mTabButtons[mTabIndex].setSelected(true);
		mCurrentTabIndex = mTabIndex;
	}
	
	private class MainPagerAdapter extends FragmentPagerAdapter{

		public MainPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case 0:
				return new StudyFragment();
			case 1:
			case 2:
			case 3:
				return TestFragment.newInstance(arg0);
			}
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 4;
		}
		
	}
	
	private class MainPageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageSelected(int position) {
			mTabIndex = position;
			mTabButtons[mCurrentTabIndex].setSelected(false);
			mTabButtons[mTabIndex].setSelected(true);
			mCurrentTabIndex = mTabIndex;
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			moveTaskToBack(false);
			mBackgroundMusicManager.stop();
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBackgroundMusicManager.stop();
	}
}
