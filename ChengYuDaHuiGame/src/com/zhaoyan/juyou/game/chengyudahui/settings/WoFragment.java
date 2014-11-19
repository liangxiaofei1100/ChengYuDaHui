package com.zhaoyan.juyou.game.chengyudahui.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.MainMenuActivity;

public class WoFragment extends Fragment {
	private static final String TAG = "WoFragment";
	
	private MainMenuActivity mActivity = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainMenuActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.fragment_wo, container, false);
		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

}
