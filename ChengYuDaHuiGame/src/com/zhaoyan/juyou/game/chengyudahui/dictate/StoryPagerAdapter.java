package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoyan.juyou.game.chengyudahui.R;

public class StoryPagerAdapter extends PagerAdapter implements OnItemClickListener{
	Context context;
	
	List<View> mListViewPager = new ArrayList<View>(); 
	List<String> list = new ArrayList<String>();
	List<List<String>> lcontant = null;
	int pageNum = 1;
//	CustomIndicator mCustomIndicator;
	int pageRows=5;

	public StoryPagerAdapter(final Context context, List<String> kf,int rows) {
//		this.mCustomIndicator=customIndicator;
		this.pageRows=rows;
		int count = 0;  //ѭ������
		int pos = 0;		//��ǰλ��
		
		this.context = context;
		this.list = kf;
		//����ҳ��
		pageNum = (int) Math.ceil(list.size() / pageRows);
		int a=list.size() % pageRows;
		if (a>0) {
			pageNum=pageNum+1;
		}
		
		
//		mCustomIndicator.setCount(pageNum);
		Log.d("hx2", String.valueOf(pageNum));
		if (Math.ceil(kf.size() / pageRows) == 0) {
			pageNum = 1;
		}
		lcontant = new ArrayList<List<String>>();
		for (int i = 0; i < pageNum; i++) {
			Log.d("hx2", String.valueOf(i));
			List<String> item = new ArrayList<String>();
			for(int k = pos;k<kf.size();k++){
				count++;
				pos = k;
				item.add(kf.get(k));
				if(count == pageRows){
					count = 0;
					pos = pos+1;
					break;
				}
			}
			lcontant.add(item);
		}

		for (int j = 0; j < pageNum; j++) {
			View viewPager = LayoutInflater.from(context).inflate(
					R.layout.story_list, null);
			ListView mList = (ListView) viewPager.findViewById(R.id.story_listview);
			final StoryListAdapter myadapter=new StoryListAdapter(context, lcontant.get(j));
			mList.setAdapter(myadapter);
			mListViewPager.add(viewPager);
			mList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Toast.makeText(context, "click:"+ position, Toast.LENGTH_LONG).show();
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
	}

	@Override
	public void destroyItem(View container, int position, Object arg2) {
		ViewPager pViewPager = ((ViewPager) container);
		pViewPager.removeView(mListViewPager.get(position));
	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
