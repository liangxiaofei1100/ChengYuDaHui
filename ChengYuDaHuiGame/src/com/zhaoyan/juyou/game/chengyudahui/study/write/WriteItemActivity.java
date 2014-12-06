package com.zhaoyan.juyou.game.chengyudahui.study.write;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.BaseZyActivity;
import com.zhaoyan.juyou.game.chengyudahui.db.DictateData.DictateColums;
import com.zhaoyan.juyou.game.chengyudahui.view.ActionBar;

public class WriteItemActivity extends BaseZyActivity implements OnItemClickListener{
	private static final String TAG = WriteItemActivity.class.getSimpleName();
	
	private GridView mGridView;
	private ItemAdapter mAdapter;
	private String mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dictate_item_main);
		
		ActionBar actionBar = getZyActionBar();
		actionBar.setActionHomeAsUpEnable(true);
		
		Intent intent = getIntent();
		int level = 0;
		if (intent != null) {
			mTitle = intent.getStringExtra("title");
			actionBar.setTitle(mTitle);
			level = intent.getIntExtra("level", 0);
		}
		
		mGridView = (GridView) findViewById(R.id.item_select_dictate);
		mAdapter = new ItemAdapter(this, null);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
		
		prepareGridView(level);
	}
	
	private void prepareGridView(int level) {
		Log.d(TAG, "prepareGridView.level:" + level);
		Cursor cursor = null;
		switch (level) {
		case WriteMainActivity.LEVEL_0:
		case WriteMainActivity.LEVEL_1:
			cursor = getContentResolver().query(
					DictateColums.CONTENT_URI,
					new String[] { "_id", DictateColums.NAME,
							DictateColums.PINYIN, DictateColums.COMMENT,
							DictateColums.DICTATE, DictateColums.ORIGINAL,
							DictateColums.EXAMPLE, DictateColums.IMG_DES,
							DictateColums.LEVEL, DictateColums.ALLUSION,
							DictateColums.RESULT },
					DictateColums.LEVEL + " != '高级'", null, null);
			break;
		case WriteMainActivity.LEVEL_2:
			cursor = getContentResolver().query(
					DictateColums.CONTENT_URI,
					new String[] { "_id", DictateColums.NAME,
							DictateColums.PINYIN, DictateColums.COMMENT,
							DictateColums.DICTATE, DictateColums.ORIGINAL,
							DictateColums.EXAMPLE, DictateColums.IMG_DES,
							DictateColums.LEVEL, DictateColums.ALLUSION,
							DictateColums.RESULT },
					DictateColums.LEVEL + " = '" + mTitle + "'", null, null);
			break;
		}
		mAdapter.changeCursor(cursor);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.setClass(this, DictateActivity.class);
		intent.putExtra("level", mTitle);
		intent.putExtra("index", position);
		startActivity(intent);
	}
}
