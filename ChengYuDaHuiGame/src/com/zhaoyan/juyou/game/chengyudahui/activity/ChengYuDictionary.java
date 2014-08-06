package com.zhaoyan.juyou.game.chengyudahui.activity;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.ChengyuData.ChengyuColums;

public class ChengYuDictionary extends Activity {
	private static final String TAG = ChengYuDictionary.class.getSimpleName();
	private static final int CHENGYU_TOTAL_NUMBER = 20000;

	private Context mContext;
	private TextView mChengYuNameTextView;
	private TextView mChengYuPinYinTextView;
	private TextView mChengYuCommentTextView;
	private TextView mChengYuOriginalTextView;
	private TextView mChengYuExampleTextView;

	private ImageView mPreviousImageView;
	private ImageView mNextImageView;

	private ChengyuQuery mChengyuQuery;
	private static final int TOKEN_SINGLE_QUERY = 1;
	private static final String[] PROJECTION = { ChengyuColums._ID,
			ChengyuColums.NAME, ChengyuColums.PINYIN, ChengyuColums.COMMENT,
			ChengyuColums.ORIGINAL, ChengyuColums.EXAMPLE };

	private int mCurrentChengYuId = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chengyu_dictionary);
		mContext = this;

		initView();
		updatePrievousAndNextButton();

		mChengyuQuery = new ChengyuQuery(getContentResolver());
		queryChengYu(mCurrentChengYuId);
	}

	private void queryChengYu(int id) {
		Log.d(TAG, "queryChengYu id = " + id);
		String selection = ChengyuColums._ID + "=" + id;
		mChengyuQuery.startQuery(TOKEN_SINGLE_QUERY, null,
				ChengyuColums.CONTENT_URI, PROJECTION, selection, null, null);
	}

	private void initView() {
		mChengYuNameTextView = (TextView) findViewById(R.id.tv_chengyu_name);
		mChengYuPinYinTextView = (TextView) findViewById(R.id.tv_chengyu_pinyin);
		mChengYuCommentTextView = (TextView) findViewById(R.id.tv_chengyu_comment);
		mChengYuOriginalTextView = (TextView) findViewById(R.id.tv_chengyu_original);
		mChengYuExampleTextView = (TextView) findViewById(R.id.tv_chengyu_example);

		mPreviousImageView = (ImageView) findViewById(R.id.iv_previous);
		mNextImageView = (ImageView) findViewById(R.id.iv_next);
	}

	private void updatePrievousAndNextButton() {
		if (mCurrentChengYuId <= 1) {
			mPreviousImageView.setVisibility(View.INVISIBLE);
		} else {
			mPreviousImageView.setVisibility(View.VISIBLE);
		}

		if (mCurrentChengYuId >= CHENGYU_TOTAL_NUMBER) {
			mNextImageView.setVisibility(View.INVISIBLE);
		} else {
			mNextImageView.setVisibility(View.VISIBLE);
		}
	}

	public void nextChengYu(View view) {
		if (mCurrentChengYuId >= CHENGYU_TOTAL_NUMBER) {
			return;
		}

		mCurrentChengYuId++;
		queryChengYu(mCurrentChengYuId);

		updatePrievousAndNextButton();
	}

	/**
	 * Previous
	 * 
	 * @param view
	 */
	public void previousChengYu(View view) {
		if (mCurrentChengYuId <= 1) {
			return;
		}
		mCurrentChengYuId--;
		queryChengYu(mCurrentChengYuId);

		updatePrievousAndNextButton();
	}

	private class ChengyuQuery extends AsyncQueryHandler {

		public ChengyuQuery(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			super.onQueryComplete(token, cookie, cursor);
			if (TOKEN_SINGLE_QUERY == token) {
				updateChengYu(cursor);
			}

		}

		private void updateChengYu(Cursor cursor) {
			if (cursor != null) {
				try {
					cursor.moveToFirst();
					String name = cursor.getString(cursor
							.getColumnIndex(ChengyuColums.NAME));
					String pinyin = cursor.getString(cursor
							.getColumnIndex(ChengyuColums.PINYIN));
					String comment = cursor.getString(cursor
							.getColumnIndex(ChengyuColums.COMMENT));
					String original = cursor.getString(cursor
							.getColumnIndex(ChengyuColums.ORIGINAL));
					String example = cursor.getString(cursor
							.getColumnIndex(ChengyuColums.EXAMPLE));

					mChengYuNameTextView.setText(name);
					mChengYuPinYinTextView.setText(pinyin);
					mChengYuCommentTextView.setText(comment);
					mChengYuOriginalTextView.setText(original);
					mChengYuExampleTextView.setText(example);
				} catch (Exception e) {
					Log.e(TAG, "updateChengYu " + e);
				} finally {
					cursor.close();
				}
			}
		}

	}
}
