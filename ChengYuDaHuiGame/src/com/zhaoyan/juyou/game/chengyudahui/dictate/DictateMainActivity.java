package com.zhaoyan.juyou.game.chengyudahui.dictate;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.DictateData.DictateColums;

import android.R.mipmap;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class DictateMainActivity extends Activity implements OnClickListener {
	private Button juniorBtn, middleBtn, seniorBtn;
	private GridView mGridView;
	private String mLevelString;
	private TextView mJifenTv, mGoldTv;
	private ImageView mBackIv;
	private ItemAdapter mItemAdapter;
	private View levelLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dictate_main);
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mLevelString == null) {
			if (mGridView.getVisibility() != View.GONE) {
				mGridView.setVisibility(View.GONE);
				levelLayout.setVisibility(View.VISIBLE);
			}
		} else {
			if (mGridView.getVisibility() == View.VISIBLE) {
				prepareGridView(mLevelString);
			}
		}
	}

	private void initView() {
		mJifenTv = (TextView) findViewById(R.id.tv_jifen_dictate);
		mGoldTv = (TextView) findViewById(R.id.tv_gold_dictate);
		mBackIv = (ImageView) findViewById(R.id.iv_back_dictate);
		mGridView = (GridView) findViewById(R.id.item_select_dictate);
		levelLayout = findViewById(R.id.level_select_dictate);
		juniorBtn = (Button) findViewById(R.id.junior_level);
		middleBtn = (Button) findViewById(R.id.middle_level);
		seniorBtn = (Button) findViewById(R.id.senior_level);
		mBackIv.setOnClickListener(this);
		mItemAdapter = new ItemAdapter(this, null);
		juniorBtn.setOnClickListener(this);
		middleBtn.setOnClickListener(this);
		seniorBtn.setOnClickListener(this);
		mGridView.setAdapter(mItemAdapter);
		mGridView.setOnItemClickListener(new ItemClick());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back_dictate:
			finish();
			return;
		case R.id.junior_level:
			mLevelString = "初级";
			Intent intent = new Intent();
			intent.setClass(this, StoryMainActivity.class);
			startActivity(intent);
			return;
		case R.id.middle_level:
			mLevelString = "中级";
			break;
		case R.id.senior_level:
			mLevelString = "高级";
			break;
		default:
			return;
		}
		prepareGridView(mLevelString);
		levelLayout.setVisibility(View.GONE);
		mGridView.setVisibility(View.VISIBLE);
	}

	@SuppressLint("NewApi")
	private void prepareGridView(String level) {
		Cursor cursor;
		if (level == null)
			level = "高级";
		if (!level.equals("高级")) {
			cursor = getContentResolver().query(
					DictateColums.CONTENT_URI,
					new String[] { "_id", DictateColums.NAME,
							DictateColums.PINYIN, DictateColums.COMMENT,
							DictateColums.DICTATE, DictateColums.ORIGINAL,
							DictateColums.EXAMPLE, DictateColums.IMG_DES,
							DictateColums.LEVEL, DictateColums.ALLUSION,
							DictateColums.RESULT },
					DictateColums.LEVEL + " != '高级'", null, null);
		} else {
			cursor = getContentResolver().query(
					DictateColums.CONTENT_URI,
					new String[] { "_id", DictateColums.NAME,
							DictateColums.PINYIN, DictateColums.COMMENT,
							DictateColums.DICTATE, DictateColums.ORIGINAL,
							DictateColums.EXAMPLE, DictateColums.IMG_DES,
							DictateColums.LEVEL, DictateColums.ALLUSION,
							DictateColums.RESULT },
					DictateColums.LEVEL + " = '" + level + "'", null, null);
		}
		mItemAdapter.swapCursor(cursor);

	}

	private class ItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(DictateMainActivity.this, DictateActivity.class);
			intent.putExtra("level", mLevelString);
			intent.putExtra("index", arg2);
			DictateMainActivity.this.startActivity(intent);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (mGridView.getVisibility() == View.VISIBLE) {
				mGridView.setVisibility(View.GONE);
				levelLayout.setVisibility(View.VISIBLE);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
