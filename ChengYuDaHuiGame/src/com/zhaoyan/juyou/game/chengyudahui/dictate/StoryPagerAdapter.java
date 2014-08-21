package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.download.GetAppListener;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;
import com.zhaoyan.juyou.game.chengyudahui.view.Effectstype;
import com.zhaoyan.juyou.game.chengyudahui.view.NiftyDialogBuilder;

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
	//
	private List<StoryListAdapter> mListAdapters = new ArrayList<StoryListAdapter>();
	
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
//		Log.d(TAG, String.valueOf(mPageNum));
		
		if (Math.ceil(mDataList.size() / pageRows) == 0) {
			mPageNum = 1;
		}
		//get page total number
		
		//将数据分页
		mAllPageList = new ArrayList<List<StoryInfo>>();
		for (int i = 0; i < mPageNum; i++) {
//			Log.d(TAG, String.valueOf(i));
			//获取每一页的数据
			List<StoryInfo> item = new ArrayList<StoryInfo>();
			for(int k = pos; k < mDataList.size(); k++){
				mDataList.get(k).setPosition(k);
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
			setItemPage(storysList, j);
			
			View viewPager = LayoutInflater.from(context).inflate(
					R.layout.story_list, null);
			ListView mList = (ListView) viewPager.findViewById(R.id.story_listview);
			final StoryListAdapter myadapter=new StoryListAdapter(context, storysList);
			mListAdapters.add(myadapter);
			mList.setAdapter(myadapter);
			mListViewPager.add(viewPager);
			mList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					final StoryInfo info  = mAllPageList.get(mCurrentPage).get(position);
					final Bundle bundle = new Bundle(2);
					if (info.getLocalPath() == null) {
						final NiftyDialogBuilder dialogBuilder = new NiftyDialogBuilder(context, R.style.dialog_untran);
						dialogBuilder.withTitle(info.getTitle())
						.withTitleColor("#000000")
						.withDividerColor("#11000000")
						.withMessage("该故事尚未下载，是否下载\n下载需要50金币\n文件大小：" + Utils.getFormatSize(info.getSize()))
						.isCancelableOnTouchOutside(true) 
						.withDuration(50)
						.withEffect(Effectstype.FadeIn) 
						.withTipMessage(null)
						.withButton1Text("取消") 
						.withButton2Text("下载")
						.setButton1Click(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogBuilder.dismiss();
							}
						})
						.setButton2Click(new OnClickListener() {
							@Override
							public void onClick(View v) {
								//download
								dialogBuilder.dismiss();
								bundle.putInt(StoryListener.CALLBACK_FLAG, StoryListener.MSG_START_DOWNLOAD);
								bundle.putParcelable(StoryListener.KEY_ITEM_STORYINFO, info);
								notifyActivityStateChanged(bundle);
							}
						})
						.show();
					} else {
						bundle.putInt(StoryListener.CALLBACK_FLAG, StoryListener.MSG_START_PLAY);
						bundle.putParcelable(StoryListener.KEY_ITEM_STORYINFO, info);
						notifyActivityStateChanged(bundle);
					}
//					info.setSelect(true);
//					myadapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	/**
	 * 给当前list中的内容标记上是哪一页的
	 * @param infos
	 * @param page
	 */
	private void setItemPage(List<StoryInfo> infos , int page){
		for (StoryInfo storyInfo : infos) {
			storyInfo.setPage(page);
		}
	}
	
	public void update(){
		for (StoryListAdapter adapter : mListAdapters) {
			adapter.notifyDataSetChanged();
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
//		Log.d(TAG, "startUpdate:" + arg0);
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
	
	private static class Record{
		int mHashCode;
		StoryListener mCallBack;
	}
	
	private ArrayList<Record> mRecords = new ArrayList<Record>();
	public void registerKeyListener(StoryListener callBack){
		synchronized (mRecords) {
			//register callback in adapter,if the callback is exist,just replace the event
			Record record = null;
			int hashCode = callBack.hashCode();
			final int n = mRecords.size();
			for(int i = 0; i < n ; i++){
				record = mRecords.get(i);
				if (hashCode == record.mHashCode) {
					return;
				}
			}
			
			record = new Record();
			record.mHashCode = hashCode;
			record.mCallBack = callBack;
			mRecords.add(record);
		}
	}
	
	private void notifyActivityStateChanged(Bundle bundle){
		if (!mRecords.isEmpty()) {
			Log.d(TAG, "notifyActivityStateChanged.clients = " + mRecords.size());
			synchronized (mRecords) {
				Iterator<Record> iterator = mRecords.iterator();
				while (iterator.hasNext()) {
					Record record = iterator.next();
					
					StoryListener listener = record.mCallBack;
					if (listener == null) {
						iterator.remove();
						return;
					}
					listener.onCallBack(bundle);
				}
			}
		}
	}
	
	public void unregisterMyKeyListener(StoryListener callBack){
		remove(callBack.hashCode());
	}
	
	private void remove(int hashCode){
		synchronized (mRecords) {
			Iterator<Record> iterator =mRecords.iterator();
			while(iterator.hasNext()){
				Record record = iterator.next();
				if (record.mHashCode == hashCode) {
					iterator.remove();
				}
			}
		}
	}
	
}
