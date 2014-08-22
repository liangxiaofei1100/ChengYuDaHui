package com.zhaoyan.juyou.game.chengyudahui.dictate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.DictateData.DictateColums;

public class DictateItemFragment extends Fragment {
	private static final String TAG = DictateItemFragment.class.getSimpleName();

	private DictateMainFragmentActivity mActivity;

	private GridView mGridView;
	private ItemAdapter mItemAdapter;
	private String mLevelString;

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
		prepareGridView(mLevelString);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mLevelString = getArguments().getString("level");
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.dictate_item_main, null);
		mGridView = (GridView) rootView.findViewById(R.id.item_select_dictate);
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mActivity.setTitle("汉字书写");
		mItemAdapter = new ItemAdapter(getActivity(), null);
		mGridView.setAdapter(mItemAdapter);
		mGridView.setOnItemClickListener(new ItemClick());
	}

	@SuppressLint("NewApi")
	private void prepareGridView(String level) {
		Log.d(TAG, "prepareGridView.level:" + level);
		Cursor cursor;
		if (level == null)
			level = "高级";
		if (!level.equals("高级")) {
			cursor = getActivity().getContentResolver().query(
					DictateColums.CONTENT_URI,
					new String[] { "_id", DictateColums.NAME,
							DictateColums.PINYIN, DictateColums.COMMENT,
							DictateColums.DICTATE, DictateColums.ORIGINAL,
							DictateColums.EXAMPLE, DictateColums.IMG_DES,
							DictateColums.LEVEL, DictateColums.ALLUSION,
							DictateColums.RESULT },
					DictateColums.LEVEL + " != '高级'", null, null);
		} else {
			cursor = getActivity().getContentResolver().query(
					DictateColums.CONTENT_URI,
					new String[] { "_id", DictateColums.NAME,
							DictateColums.PINYIN, DictateColums.COMMENT,
							DictateColums.DICTATE, DictateColums.ORIGINAL,
							DictateColums.EXAMPLE, DictateColums.IMG_DES,
							DictateColums.LEVEL, DictateColums.ALLUSION,
							DictateColums.RESULT },
					DictateColums.LEVEL + " = '" + level + "'", null, null);
		}
		mItemAdapter.changeCursor(cursor);

	}

	private class ItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(getActivity(), DictateActivity.class);
			intent.putExtra("level", mLevelString);
			intent.putExtra("index", arg2);
			mActivity.startActivity(intent);
		}

	}
}
