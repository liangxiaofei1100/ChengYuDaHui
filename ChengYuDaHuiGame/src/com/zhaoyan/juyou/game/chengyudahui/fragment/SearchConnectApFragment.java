package com.zhaoyan.juyou.game.chengyudahui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhaoyan.communication.connect.ServerCreator;
import com.zhaoyan.communication.provider.ZhaoYanCommunicationData;
import com.zhaoyan.communication.search.ServerSearcher;
import com.zhaoyan.juyou.game.chengyudahui.R;

public class SearchConnectApFragment extends SearchConnectBaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setInitView(R.string.search_ap_tips);
		return view;
	}

	@Override
	protected int getServerUserType() {
		return ZhaoYanCommunicationData.User.TYPE_REMOTE_SEARCH_AP;
	}

	@Override
	protected int getServerSearchType() {
		return ServerSearcher.SERVER_TYPE_AP;
	}

	@Override
	protected int getServerCreateType() {
		return ServerCreator.TYPE_AP;
	}

}
