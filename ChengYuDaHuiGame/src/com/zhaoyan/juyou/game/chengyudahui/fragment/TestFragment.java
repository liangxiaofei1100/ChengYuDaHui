package com.zhaoyan.juyou.game.chengyudahui.fragment;

import com.zhaoyan.juyou.game.chengyudahui.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestFragment extends Fragment{
	
	private int num;
	
	public static TestFragment newInstance(int num){
		TestFragment testFragment = new TestFragment();
		Bundle args = new Bundle();
		args.putInt("num", num);
		testFragment.setArguments(args);
		
		return testFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		num = getArguments() != null ? getArguments().getInt("num") : -1;
		System.out.println("onCreate.num:" + num);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("onResume.num:" + num);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		System.out.println("onCreateView.num:" + num);
		View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_test, null);
		TextView textView = (TextView) rootView.findViewById(R.id.tv_fragment_test);
		textView.setText(num + "");
		return rootView;
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		System.out.println("onDestroyView.num:" + num);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("onPause.num:" + num);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("onStop.num:" + num);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("onDestroy.num:" + num);
	}
}
