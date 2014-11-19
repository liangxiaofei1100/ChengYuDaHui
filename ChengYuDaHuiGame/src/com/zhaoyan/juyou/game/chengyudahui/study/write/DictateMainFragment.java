package com.zhaoyan.juyou.game.chengyudahui.study.write;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;

public class DictateMainFragment extends Fragment implements OnClickListener{
	private static final String TAG = DictateMainFragment.class.getSimpleName();
	
	private Button juniorBtn, middleBtn, seniorBtn;
	
	private DictateMainFragmentActivity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach:" + activity);
		mActivity = (DictateMainFragmentActivity) activity;
		super.onAttach(activity);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dictate_fragment_main, null);
		initView(rootView);
		return rootView;
	}
	
	private void initView(View rootView) {
		juniorBtn = (Button) rootView.findViewById(R.id.junior_level);
		middleBtn = (Button) rootView.findViewById(R.id.middle_level);
		seniorBtn = (Button) rootView.findViewById(R.id.senior_level);
		juniorBtn.setOnClickListener(this);
		middleBtn.setOnClickListener(this);
		seniorBtn.setOnClickListener(this);
	}

	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity.setTitle("汉字听写");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.junior_level:
			mActivity.selectItem(1);
			return;
		case R.id.middle_level:
			mActivity.selectItem(2);
			break;
		case R.id.senior_level:
			mActivity.selectItem(3);
			break;
		default:
			return;
		}
	}

}
