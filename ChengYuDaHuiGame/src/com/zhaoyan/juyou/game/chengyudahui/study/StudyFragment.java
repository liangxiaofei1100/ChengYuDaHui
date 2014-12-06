package com.zhaoyan.juyou.game.chengyudahui.study;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.MainMenuActivity;
import com.zhaoyan.juyou.game.chengyudahui.adapter.ItemFragmentAdapter;
import com.zhaoyan.juyou.game.chengyudahui.bean.ListItemInfo;
import com.zhaoyan.juyou.game.chengyudahui.study.story.StoryMainActivity;
import com.zhaoyan.juyou.game.chengyudahui.study.write.WriteMainActivity;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;

public class StudyFragment extends Fragment implements OnItemClickListener{
	private static final String TAG = StudyFragment.class.getSimpleName();
	
	private MainMenuActivity mActivity;
	
	private ListView mListView;
	private ItemFragmentAdapter mAdapter;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MainMenuActivity) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_list, null);
		mListView = (ListView) rootView.findViewById(R.id.lv_listview);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//test
		List<ListItemInfo> list = new ArrayList<ListItemInfo>();
		ListItemInfo info = null;
		
		Resources res = getResources();
		String[] items = res.getStringArray(R.array.study_items);
		TypedArray dra= res.obtainTypedArray(R.array.study_item_icons);
		String[] itemHints = res.getStringArray(R.array.study_items_hints);
		for (int i = 0; i < 4; i++) {
			info = new ListItemInfo();
			info.setIconId(dra.getResourceId(i, 0));
			info.setTitle(items[i]);
			info.setSummary(itemHints[i]);
			list.add(info);
		}
		dra.recycle();
		
		mAdapter = new ItemFragmentAdapter(mActivity, list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (Utils.isFasDoubleClick()) {
			return;
		}
		ListItemInfo itemInfo = mAdapter.getItem(position);
		Intent intent = new Intent();
		switch (position) {
		case 0:
			intent.setClass(mActivity, StoryMainActivity.class);
			break;
		case 1:
			intent.setClass(mActivity, WriteMainActivity.class);
			break;
		case 2:
			break;
		case 3:
			break;
		}
		
		try {
			intent.putExtra("title", itemInfo.getTitle());
			startActivity(intent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
