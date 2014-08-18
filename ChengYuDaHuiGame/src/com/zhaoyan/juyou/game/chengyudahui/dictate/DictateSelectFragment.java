package com.zhaoyan.juyou.game.chengyudahui.dictate;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.DictateData.DictateColums;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

@SuppressLint("ValidFragment") public class DictateSelectFragment extends Fragment implements
		OnItemClickListener {
	private GridView mGridView;
	private static final String TAG = DictateSelectFragment.class
			.getSimpleName();
	private ItemAdapter mItemAdapter;
	private DictateMainFragmentActivity mActivity;
	private String mLevel;
	private int level;

	public DictateSelectFragment(int level){
		this.level=level;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mActivity.setTitle("汉字听写");
		if (mItemAdapter == null) {
			mItemAdapter = new ItemAdapter(mActivity, null);
			mGridView.setAdapter(mItemAdapter);
		}
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (DictateMainFragmentActivity) activity;

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setLevel(level);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.dictate_iten_select, null);
		mGridView = (GridView) rootView
				.findViewById(R.id.item_select_dictate_fra);
		return rootView;
	}

	@SuppressLint("NewApi")
	private void setLevel(int level) {
		Cursor cursor;
		if (level == 1) {
			mLevel = "高级";
			cursor = mActivity.getContentResolver().query(
					DictateColums.CONTENT_URI,
					new String[] { "_id", DictateColums.NAME,
							DictateColums.PINYIN, DictateColums.COMMENT,
							DictateColums.DICTATE, DictateColums.ORIGINAL,
							DictateColums.EXAMPLE, DictateColums.IMG_DES,
							DictateColums.LEVEL, DictateColums.ALLUSION,
							DictateColums.RESULT },
					DictateColums.LEVEL + " = '" + mLevel + "'", null, null);
		} else {
			mLevel = "低级";
			cursor = mActivity.getContentResolver().query(
					DictateColums.CONTENT_URI,
					new String[] { "_id", DictateColums.NAME,
							DictateColums.PINYIN, DictateColums.COMMENT,
							DictateColums.DICTATE, DictateColums.ORIGINAL,
							DictateColums.EXAMPLE, DictateColums.IMG_DES,
							DictateColums.LEVEL, DictateColums.ALLUSION,
							DictateColums.RESULT },
					DictateColums.LEVEL + " != '高级'", null, null);
		}
		mItemAdapter.swapCursor(cursor);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(mActivity, DictateActivity.class);
		intent.putExtra("level", mLevel);
		intent.putExtra("index", arg2);
		mActivity.startActivity(intent);
	}
}
