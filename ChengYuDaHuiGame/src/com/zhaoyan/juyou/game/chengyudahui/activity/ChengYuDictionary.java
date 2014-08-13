package com.zhaoyan.juyou.game.chengyudahui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.ChengYu;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.ChengyuData.ChengyuColums;

public class ChengYuDictionary extends Activity implements OnItemClickListener {
	private static final String TAG = ChengYuDictionary.class.getSimpleName();
	private static final int CHENGYU_TOTAL_NUMBER = 29349;

	private Context mContext;
	private AutoCompleteTextView mSearchEditText;
	private View mClearTextView;

	private ViewPager mViewPager;
	private PagerAdapter mPagerAdapter;
	private List<ChengYu> mChengYuList;
	private int mCurrentViewPageIndex = 0;

	private TextView mChengYuNameTextView;
	private TextView mChengYuIndexTextView;

	private ImageView mPreviousImageView;
	private ImageView mNextImageView;

	private ChengyuQuery mChengyuQuery;
	private static final int TOKEN_SINGLE_QUERY = 1;
	private static final int TOKEN_HEAD_QUERY = 2;
	private static final int TOKEN_TAIL_QUERY = 3;
	private static final int TOKEN_SEARCHBOX_QUERY = 4;

	private static final String[] PROJECTION = { ChengyuColums._ID,
			ChengyuColums.NAME, ChengyuColums.PINYIN, ChengyuColums.COMMENT,
			ChengyuColums.ORIGINAL, ChengyuColums.EXAMPLE };

	private int mInitChengYuId = 1;
	private int mCurrentChengYuId = mInitChengYuId;
	private int mPageHeadChengYuId = mCurrentChengYuId;
	private int mPageTailChengYuId = mCurrentChengYuId;
	private static final int PRELOAD_NUMBER_HEAD = 10;
	private static final int PRELOAD_NUMBER_TAIL = 10;

	private SimpleCursorAdapter mSearchListAdapter;
	private Cursor mSearchCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chengyu_dictionary);
		mContext = this;
		mChengyuQuery = new ChengyuQuery(getContentResolver());
		mChengYuList = new ArrayList<ChengYu>();

		initView();

		movetoChengYu(mInitChengYuId);
		updatePrievousAndNextButton();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// release search cursor
		Cursor cursor = mSearchListAdapter.swapCursor(null);
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}

	private void initView() {
		// Search views
		mSearchEditText = (AutoCompleteTextView) findViewById(R.id.et_search);
		mSearchEditText.addTextChangedListener(new SearchTextWatcher());
		mSearchListAdapter = new SearchAdapter(mContext);
		mSearchEditText.setAdapter(mSearchListAdapter);
		mSearchEditText.setOnItemClickListener(this);
		mSearchEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					// search
					searchChengYu();
					return true;
				}
				return false;
			}
		});
		mClearTextView = findViewById(R.id.iv_clear_text);
		mSearchEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				searchChengYu();
			}
		});
		// ChengYu views
		mChengYuNameTextView = (TextView) findViewById(R.id.tv_chengyu_name);
		mChengYuIndexTextView = (TextView) findViewById(R.id.tv_chengyu_index);
		mPreviousImageView = (ImageView) findViewById(R.id.iv_previous);
		mNextImageView = (ImageView) findViewById(R.id.iv_next);
		mViewPager = (ViewPager) findViewById(R.id.vp_chengyu);
		mPagerAdapter = new ChengYuViewPagerAdapter();
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(new ChengYuPagerChangeListener());
	}

	/**
	 * Move to chengyu with the id.
	 * 
	 * @param id
	 */
	private void movetoChengYu(int id) {
		Log.d(TAG, "movetoChengYu id = " + id);
		mCurrentChengYuId = id;
		mPageHeadChengYuId = mCurrentChengYuId;
		mPageTailChengYuId = mCurrentChengYuId;
		String selection = ChengyuColums._ID + ">="
				+ (id - PRELOAD_NUMBER_TAIL) + " and " + ChengyuColums._ID
				+ "<=" + (id + PRELOAD_NUMBER_HEAD);
		mChengyuQuery.startQuery(TOKEN_SINGLE_QUERY, null,
				ChengyuColums.CONTENT_URI, PROJECTION, selection, null, null);
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

		mCurrentViewPageIndex++;
		mViewPager.setCurrentItem(mCurrentViewPageIndex, false);
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

		mCurrentViewPageIndex--;
		mViewPager.setCurrentItem(mCurrentViewPageIndex, false);
	}

	public void back(View view) {
		finish();
	}

	/**
	 * feed back ChengYu errors.
	 * 
	 * @param view
	 */
	public void feedback(View view) {
		// TODO
	}

	public void clearText(View view) {
		mSearchEditText.setText("");
	}

	private void loadNextPage() {
		if (mCurrentViewPageIndex == mChengYuList.size() - 2) {
			Log.d(TAG, "loadNextPage");
			if (mPageHeadChengYuId >= CHENGYU_TOTAL_NUMBER) {
				// The last data.
				return;
			}

			// Load next data;
			String selection = ChengyuColums._ID + ">" + mPageHeadChengYuId
					+ " and " + ChengyuColums._ID + "<="
					+ (mPageHeadChengYuId + PRELOAD_NUMBER_HEAD);
			mChengyuQuery.startQuery(TOKEN_HEAD_QUERY, null,
					ChengyuColums.CONTENT_URI, PROJECTION, selection, null,
					null);
		}
	}

	private void loadPrePage() {
		if (mCurrentViewPageIndex == 1) {
			Log.d(TAG, "loadPrePage");
			if (mPageTailChengYuId <= 1) {
				// The first data.
				return;
			}

			// load previous data
			String selection = ChengyuColums._ID + "<" + mPageTailChengYuId
					+ " and " + ChengyuColums._ID + ">="
					+ (mPageTailChengYuId - PRELOAD_NUMBER_TAIL);
			mChengyuQuery.startQuery(TOKEN_TAIL_QUERY, null,
					ChengyuColums.CONTENT_URI, PROJECTION, selection, null,
					null);
		}
	}

	private void searchChengYu() {
		String selection = ChengyuColums.NAME + " like '"
				+ mSearchEditText.getText().toString() + "%'";
		mChengyuQuery.startQuery(TOKEN_SEARCHBOX_QUERY, null,
				ChengyuColums.CONTENT_URI, PROJECTION, selection, null, null);
	}

	private class SearchTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() == 0) {
				mClearTextView.setVisibility(View.GONE);
			} else {
				mClearTextView.setVisibility(View.VISIBLE);

				searchChengYu();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	}

	class ChengYuViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mChengYuList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ChengYu chengYu = mChengYuList.get(position);

			View view = LayoutInflater.from(mContext).inflate(
					R.layout.chengyu_content, null);
			TextView pinyin = (TextView) view
					.findViewById(R.id.tv_chengyu_pinyin);
			TextView commentTitle = (TextView) view
					.findViewById(R.id.tv_chengyu_comment_title);
			TextView comment = (TextView) view
					.findViewById(R.id.tv_chengyu_comment);
			TextView originalTitle = (TextView) view
					.findViewById(R.id.tv_chengyu_original_title);
			TextView original = (TextView) view
					.findViewById(R.id.tv_chengyu_original);
			TextView exampleTitle = (TextView) view
					.findViewById(R.id.tv_chengyu_example_title);
			TextView example = (TextView) view
					.findViewById(R.id.tv_chengyu_example);

			pinyin.setText(chengYu.pinyin);
			comment.setText(chengYu.comment);
			handleEmptyChengYuItem(chengYu.comment, commentTitle, comment);
			original.setText(chengYu.original);
			handleEmptyChengYuItem(chengYu.original, originalTitle, original);
			example.setText(chengYu.example);
			handleEmptyChengYuItem(chengYu.example, exampleTitle, example);

			container.addView(view);
			return view;
		}

		private void handleEmptyChengYuItem(String item, View... views) {
			if (item.length() == 0 || item.equals("无")) {
				for (View view : views) {
					view.setVisibility(View.GONE);
				}
			} else {
				for (View view : views) {
					view.setVisibility(View.VISIBLE);
				}
			}
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	class ChengYuPagerChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			mCurrentViewPageIndex = position;
			mCurrentChengYuId = mChengYuList.get(position).id;

			updateChengYuNameAndId(mChengYuList.get(position).name,
					mCurrentChengYuId);
			updatePrievousAndNextButton();

			loadNextPage();
			loadPrePage();
		}

	}

	private void updateChengYuNameAndId(String name, int id) {
		mChengYuNameTextView.setText(name);
		mChengYuIndexTextView.setText("第" + id + "个，共" + CHENGYU_TOTAL_NUMBER
				+ "个");
	}

	/**
	 * Query ChengYu database.
	 * 
	 */
	private class ChengyuQuery extends AsyncQueryHandler {

		public ChengyuQuery(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			super.onQueryComplete(token, cookie, cursor);
			Log.d(TAG, "onQueryComplete token = " + token);
			switch (token) {
			case TOKEN_SINGLE_QUERY:
				singleQuery(cursor);
				break;
			case TOKEN_HEAD_QUERY:
				headQuery(cursor);
				break;
			case TOKEN_TAIL_QUERY:
				tailQuery(cursor);
				break;
			case TOKEN_SEARCHBOX_QUERY:
				searchQuery(cursor);
				break;
			default:
				break;
			}

		}

		private void searchQuery(Cursor cursor) {
			mSearchCursor = cursor;
			Cursor old = mSearchListAdapter.swapCursor(mSearchCursor);
			if (old != null && !old.isClosed()) {
				old.close();
			}
			mSearchListAdapter.notifyDataSetChanged();

			if (cursor != null && cursor.getCount() > 0) {
				mSearchEditText.setCompletionHint("共搜索到" + cursor.getCount()
						+ "个成语");
				mSearchCursor.moveToFirst();
				ChengYu chengYu = getChengYu(mSearchCursor);
				Log.d(TAG, "chengyu " + chengYu.name + " id = " + chengYu.id);
			}
		}

		private void tailQuery(Cursor cursor) {
			if (cursor != null) {
				try {
					if (cursor.moveToLast()) {
						do {
							ChengYu chengYu = getChengYu(cursor);
							addChengYuIntoViewPager(chengYu, 0);
							mPageTailChengYuId--;
						} while (cursor.moveToPrevious());

						mPagerAdapter.notifyDataSetChanged();
						if (mCurrentChengYuId != mPageTailChengYuId) {
							mCurrentViewPageIndex = mCurrentChengYuId
									- mPageTailChengYuId;
							mViewPager.setCurrentItem(mCurrentViewPageIndex,
									false);
						}
					}
				} catch (Exception e) {
					Log.e(TAG, "tailQuery " + e);
				} finally {
					cursor.close();
				}
			}
		}

		private void headQuery(Cursor cursor) {
			if (cursor != null) {
				try {
					while (cursor.moveToNext()) {
						ChengYu chengYu = getChengYu(cursor);
						addChengYuIntoViewPager(chengYu);
						mPageHeadChengYuId++;
					}
					mPagerAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					Log.e(TAG, "headQuery " + e);
				} finally {
					cursor.close();
				}
			}
		}

		private void singleQuery(Cursor cursor) {
			if (cursor != null) {
				try {
					// Clear the list.
					mChengYuList.clear();
					// Add the query result into list.
					while (cursor.moveToNext()) {
						ChengYu chengYu = getChengYu(cursor);

						int id = cursor.getInt(cursor
								.getColumnIndex(ChengyuColums._ID));

						// Update head and tail id.
						if (id == mCurrentChengYuId) {
							updateChengYuNameAndId(chengYu.name, id);
						} else if (id < mCurrentChengYuId) {
							// update tail id if needed
							if (id < mPageTailChengYuId) {
								mPageTailChengYuId = id;
							}
						} else if (id > mCurrentChengYuId) {
							// update head id if needed
							if (id > mPageHeadChengYuId) {
								mPageHeadChengYuId = id;
							}
						}
						addChengYuIntoViewPager(chengYu);
					}
					mPagerAdapter.notifyDataSetChanged();
					// Update current ChengYu
					Log.d(TAG, "tail id = " + mPageTailChengYuId
							+ ", currentId = " + mCurrentChengYuId
							+ ", headId = " + mPageHeadChengYuId);
					Log.d(TAG, "chengyu list " + mChengYuList.toString());
					if (mCurrentChengYuId != mPageTailChengYuId) {
						mCurrentViewPageIndex = mCurrentChengYuId
								- mPageTailChengYuId;
						mViewPager.setCurrentItem(mCurrentViewPageIndex, false);
					}
				} catch (Exception e) {
					Log.e(TAG, "singleQuery " + e);
				} finally {
					cursor.close();
				}
			}
		}

		private ChengYu getChengYu(Cursor cursor) {
			ChengYu chengYu = new ChengYu();
			chengYu.id = cursor
					.getInt(cursor.getColumnIndex(ChengyuColums._ID));
			chengYu.name = cursor.getString(cursor
					.getColumnIndex(ChengyuColums.NAME));
			chengYu.pinyin = cursor.getString(cursor
					.getColumnIndex(ChengyuColums.PINYIN));
			chengYu.comment = cursor.getString(cursor
					.getColumnIndex(ChengyuColums.COMMENT));
			chengYu.original = cursor.getString(cursor
					.getColumnIndex(ChengyuColums.ORIGINAL));
			chengYu.example = cursor.getString(cursor
					.getColumnIndex(ChengyuColums.EXAMPLE));
			return chengYu;
		}

		private void addChengYuIntoViewPager(ChengYu chengYu) {
			// Add into last.
			addChengYuIntoViewPager(chengYu, -1);
		}

		private void addChengYuIntoViewPager(ChengYu chengYu, int position) {
			if (position == -1) {
				mChengYuList.add(chengYu);
			} else {
				mChengYuList.add(position, chengYu);
			}
		}
	}

	private class SearchAdapter extends SimpleCursorAdapter {

		public SearchAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_1, mSearchCursor,
					new String[] { ChengyuColums.NAME },
					new int[] { android.R.id.text1 },
					SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		}

		@Override
		public CharSequence convertToString(Cursor cursor) {
			if (cursor != null) {
				return cursor.getString(cursor
						.getColumnIndex(ChengyuColums.NAME));
			}
			return "";
		}
	}

	/**
	 * Search list item click
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		movetoChengYu((int) id);
		closeInputMethod(mSearchEditText);
	}

	private void closeInputMethod(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
