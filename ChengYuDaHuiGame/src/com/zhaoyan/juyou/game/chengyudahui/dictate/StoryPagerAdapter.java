package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;

public class StoryPagerAdapter extends PagerAdapter implements OnItemClickListener{
	private static final String TAG = StoryPagerAdapter.class.getSimpleName();
	
	private Context context;
	
	private List<View> mListViewPager = new ArrayList<View>(); 
	//所有数据源
	private List<StoryInfo> mDataList = new ArrayList<StoryInfo>();
	
	//保存所有页的数据
	private List<List<StoryInfo>> mAllPageList = null;
	//每一页的数据
	private List<StoryInfo> mPageList = null;
	
	private int mPageNum = 1;
	private CustomIndicator mCustomIndicator;
	private int pageRows=5;
	
	private int mCurrentPage = 0;

	public StoryPagerAdapter(final Context context, List<StoryInfo> data, CustomIndicator indicator, int rows) {
		this.mCustomIndicator=indicator;
		this.pageRows=rows;
		
		int count = 0;  
		int pos = 0;	
		
		this.context = context;
		this.mDataList = data;

		//get page total number
		mPageNum = (int) Math.ceil(mDataList.size() / pageRows);
		
		int a = mDataList.size() % pageRows;
		if (a > 0) {
			mPageNum = mPageNum + 1;
		}
		
		
		mCustomIndicator.setCount(mPageNum);
		mCurrentPage = 0;
		Log.d(TAG, String.valueOf(mPageNum));
		
		if (Math.ceil(mDataList.size() / pageRows) == 0) {
			mPageNum = 1;
		}
		//get page total number
		
		//将数据分页
		mAllPageList = new ArrayList<List<StoryInfo>>();
		for (int i = 0; i < mPageNum; i++) {
			Log.d(TAG, String.valueOf(i));
			//获取每一页的数据
			List<StoryInfo> item = new ArrayList<StoryInfo>();
			for(int k = pos; k < mDataList.size(); k++){
				count++;
				pos = k;
				item.add(mDataList.get(k));
				if(count == pageRows){
					count = 0;
					pos = pos+1;
					break;
				}
			}
			mAllPageList.add(item);
		}

		List<StoryInfo> storysList = null;
		for (int j = 0; j < mPageNum; j++) {
			storysList = mAllPageList.get(j);
			
			View viewPager = LayoutInflater.from(context).inflate(
					R.layout.story_list, null);
			ListView mList = (ListView) viewPager.findViewById(R.id.story_listview);
			final StoryListAdapter myadapter=new StoryListAdapter(context, storysList);
			mList.setAdapter(myadapter);
			mListViewPager.add(viewPager);
			mList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					StoryInfo info  = mAllPageList.get(mCurrentPage).get(position);
					info.setTitle("ssssssssssss");
					Toast.makeText(context, info.getTitle(), Toast.LENGTH_SHORT).show();
					myadapter.notifyDataSetChanged();
				}
			});
		}

	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return mListViewPager.size();
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(mListViewPager.get(position));
		return mListViewPager.get(position);

	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		Log.d(TAG, "startUpdate:" + arg0);
	}

	@Override
	public void destroyItem(View container, int position, Object arg2) {
		ViewPager pViewPager = ((ViewPager) container);
		pViewPager.removeView(mListViewPager.get(position));
	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub
//		Log.d(TAG, "finishUpdate:" + arg0);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onItemClick:" + position);
	}
	
	public void setCurrentPage(int page){
		mCurrentPage = page;
	}
	
}
