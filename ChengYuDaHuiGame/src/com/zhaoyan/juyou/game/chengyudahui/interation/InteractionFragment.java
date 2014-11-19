package com.zhaoyan.juyou.game.chengyudahui.interation;

import java.util.ArrayList;
import java.util.List;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.MainMenuActivity;
import com.zhaoyan.juyou.game.chengyudahui.adapter.ItemFragmentAdapter;
import com.zhaoyan.juyou.game.chengyudahui.bean.ListItemInfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class InteractionFragment extends Fragment implements OnItemClickListener{
	private static final String TAG = InteractionFragment.class.getSimpleName();
	
	private MainMenuActivity mActivity;
	
	private ListView mListView;
	private ItemFragmentAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity = (MainMenuActivity) getActivity();
		
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
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		//test
		List<ListItemInfo> list = new ArrayList<ListItemInfo>();
		ListItemInfo info = null;
		for (int i = 0; i < 4; i++) {
			info = new ListItemInfo();
			info.setIconId(R.drawable.avantar_test);
			info.setTitle("互动界面测试" + i);
			info.setSummary("这是一个测试Item" + i);
			list.add(info);
		}
		
		mAdapter = new ItemFragmentAdapter(mActivity, list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		ListItemInfo itemInfo = mAdapter.getItem(position);
		Toast.makeText(mActivity, "click:" + itemInfo.getTitle(), Toast.LENGTH_SHORT).show();
	}
}
