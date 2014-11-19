package com.zhaoyan.juyou.game.chengyudahui.friend;

import java.util.ArrayList;
import java.util.List;

import com.zhaoyan.communication.ipc.aidl.User;
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

public class FriendFragment extends Fragment implements OnItemClickListener{
	private static final String TAG = FriendFragment.class.getSimpleName();
	
	private MainMenuActivity mActivity;
	
	private ListView mListView;
	private FriendAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity = (MainMenuActivity) getActivity();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_friend_list, null);
		mListView = (ListView) rootView.findViewById(R.id.lv_friend);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		//test
		List<User> list = new ArrayList<User>();
		User user = null;
		for (int i = 0; i < 12; i++) {
			user = new User();
			user.setUserName("用户" + i);
			list.add(user);
		}
		
		mAdapter = new FriendAdapter(mActivity, list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		User user = mAdapter.getItem(position);
		Toast.makeText(mActivity, "click:" + user.getUserName(), Toast.LENGTH_SHORT).show();
	}
}
