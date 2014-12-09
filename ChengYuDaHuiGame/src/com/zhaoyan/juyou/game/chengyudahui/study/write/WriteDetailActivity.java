package com.zhaoyan.juyou.game.chengyudahui.study.write;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.communication.cache.CacheableBitmapDrawable;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.BaseZyActivity;
import com.zhaoyan.juyou.game.chengyudahui.db.DictateData;
import com.zhaoyan.juyou.game.chengyudahui.db.DictateData.DictateColums;
import com.zhaoyan.juyou.game.chengyudahui.download.Conf;
import com.zhaoyan.juyou.game.chengyudahui.download.NetworkCacheableImageView;
import com.zhaoyan.juyou.game.chengyudahui.download.NetworkCacheableImageView.OnImageLoadedListener;
import com.zhaoyan.juyou.game.chengyudahui.view.ActionBar;

public class WriteDetailActivity extends BaseZyActivity implements OnClickListener {
	private TextView[] mPinYinTVs;
	private TextView[] mWordTVs;
	private ImageView[] mWordPaintIVs;
	
	private NetworkCacheableImageView mDictateWordImage;
	private View mFirstLayout, mSecondLayout, mThirdLayout, mFourthLayout,
			mOriginalView, mExampleView, mAllusionView;
	private String mWord;
	private Map<Integer, Bitmap> mPaintMap;
	private ResultView mResultView;
	private ResultListener mrListener;
	private AlertDialog resultDialog;
	private List<Integer> wordIndex;
	private TextView mDictateComment, mDictateOriginal, mDictateExample,
			mDictateAllusion, mImgDescription;
	private final String PIC_PATH = Conf.URL_EX + Conf.CLOUD_LISTEN_DIR;
	private final int IMG_LOADED = 0;
	private String mImgDescriptionStr;
	private Cursor mWordCursor;
	private String mGameLevel;
	private int wordInCursor;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case IMG_LOADED:
				mImgDescription.setText(mImgDescriptionStr);
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dictate);
		
		ActionBar actionBar = getZyActionBar();
		actionBar.setActionHomeAsUpEnable(true);
		actionBar.setTitle("汉字听写");
		
		initView();
		
		mGameLevel = getIntent().getStringExtra("level");
		wordInCursor = getIntent().getIntExtra("index", 0);
		getWord(mGameLevel);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void initView() {
		mWordTVs = new TextView[4];
		mWordTVs[0] = (TextView) findViewById(R.id.dictate_first_word);
		mWordTVs[1] = (TextView) findViewById(R.id.dictate_second_word);
		mWordTVs[2] = (TextView) findViewById(R.id.dictate_third_word);
		mWordTVs[3] = (TextView) findViewById(R.id.dictate_fourth_word);
		
		mDictateWordImage = (NetworkCacheableImageView) findViewById(R.id.dictate_word_image);
		mWordPaintIVs = new ImageView[4];
		mWordPaintIVs[0] = (ImageView) findViewById(R.id.dictate_first_word_img);
		mWordPaintIVs[1] = (ImageView) findViewById(R.id.dictate_second_word_img);
		mWordPaintIVs[2] = (ImageView) findViewById(R.id.dictate_third_word_img);
		mWordPaintIVs[3] = (ImageView) findViewById(R.id.dictate_fourth_word_img);
		
		mPinYinTVs = new TextView[4];
		mPinYinTVs[0] = (TextView) findViewById(R.id.first_pinyin);
		mPinYinTVs[1] = (TextView) findViewById(R.id.second_pinyin);
		mPinYinTVs[2] = (TextView) findViewById(R.id.third_pinyin);
		mPinYinTVs[3] = (TextView) findViewById(R.id.fourth_pinyin);
		
		mFirstLayout = findViewById(R.id.first_layout);
		mSecondLayout = findViewById(R.id.second_layout);
		mThirdLayout = findViewById(R.id.third_layout);
		mFourthLayout = findViewById(R.id.fourth_layout);
		// mDictateWordImage.setImageResource(R.drawable.test);
		mDictateAllusion = (TextView) findViewById(R.id.tv_dictate_allusion);
		mDictateComment = (TextView) findViewById(R.id.tv_dictate_comment);
		mDictateExample = (TextView) findViewById(R.id.tv_dictate_example);
		mDictateOriginal = (TextView) findViewById(R.id.tv_dictate_original);
		mImgDescription = (TextView) findViewById(R.id.img_des_text);
		mOriginalView = findViewById(R.id.dictate_original);
		mExampleView = findViewById(R.id.dictate_example);
		mAllusionView = findViewById(R.id.dictate_allusion);
		findViewById(R.id.dictate_show_result).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showResult();
					}
				});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(this, PaintGameActivty.class);
		intent.putExtra("index", Integer.valueOf(v.getTag().toString()));
		this.startActivityForResult(intent, 20);

	}

	private void getWord(String level) {
		if (wordIndex == null)
			wordIndex = new ArrayList<Integer>();
		wordIndex.clear();
		if (mWordCursor != null && !mWordCursor.isClosed()) {
			setValue(mWordCursor);
			return;
		}
		if (level == null)
			level = "高级";
		
		mWordCursor = getContentResolver().query(
				DictateColums.CONTENT_URI,
				new String[] { "_id", DictateColums.NAME,
						DictateColums.PINYIN, DictateColums.COMMENT,
						DictateColums.DICTATE, DictateColums.ORIGINAL,
						DictateColums.EXAMPLE, DictateColums.IMG_DES,
						DictateColums.LEVEL, DictateColums.ALLUSION,
						DictateColums.RESULT },
				DictateColums.LEVEL + " = '" + level + "'", null, null);
		
		if (mWordCursor != null && mWordCursor.getCount() > 0) {
			if (wordInCursor > 0)
				mWordCursor.move(wordInCursor);
			try {
				setValue(mWordCursor);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return;
		} else {
			if (mGameLevel.equals("初级")) {
				mGameLevel = "中级";
			} else {
				mGameLevel = "高级";
			}
			getWord(mGameLevel);
		}
	}

	private void showPaint(int index) {
		wordIndex.add(index);
		switch (Math.abs(index) % mWord.length()) {
		case 0:
			mWordTVs[0].setVisibility(View.GONE);
			mWordPaintIVs[0].setVisibility(View.VISIBLE);
			mWordPaintIVs[0].setClickable(true);
			mWordPaintIVs[0].setOnClickListener(this);
			break;
		case 1:
			mWordTVs[1].setVisibility(View.GONE);
			mWordPaintIVs[1].setVisibility(View.VISIBLE);
			mWordPaintIVs[1].setClickable(true);
			mWordPaintIVs[1].setOnClickListener(this);
			break;
		case 2:
			mWordTVs[2].setVisibility(View.GONE);
			mWordPaintIVs[2].setVisibility(View.VISIBLE);
			mWordPaintIVs[2].setClickable(true);
			mWordPaintIVs[2].setOnClickListener(this);
			break;
		case 3:
			mWordTVs[3].setVisibility(View.GONE);
			mWordPaintIVs[3].setVisibility(View.VISIBLE);
			mWordPaintIVs[3].setClickable(true);
			mWordPaintIVs[3].setOnClickListener(this);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			if (mPaintMap == null)
				mPaintMap = new HashMap<Integer, Bitmap>();
			Bitmap temp = PaintGameActivty.mPaintBitmap.copy(
					Bitmap.Config.ARGB_8888, false);
			PaintGameActivty.mPaintBitmap.recycle();
			int i = data.getIntExtra("index", -1);
			switch (i) {
			case 1:
				mWordPaintIVs[0].setImageBitmap(temp);
				break;
			case 2:
				mWordPaintIVs[1].setImageBitmap(temp);
				break;
			case 3:
				mWordPaintIVs[2].setImageBitmap(temp);
				break;
			case 4:
				mWordPaintIVs[3].setImageBitmap(temp);
				break;
			default:
				return;
			}
			Bitmap t = mPaintMap.get(i);
			if (t != null) {
				t.recycle();
				t = null;
			}
			mPaintMap.put(i, temp);
		}
	}

	private void nextWord() {
		mWordPaintIVs[0].setVisibility(View.GONE);
		mWordPaintIVs[1].setVisibility(View.GONE);
		mWordPaintIVs[2].setVisibility(View.GONE);
		mWordPaintIVs[3].setVisibility(View.GONE);
		
		mWordPaintIVs[0].setImageResource(R.drawable.mizige1);
		mWordPaintIVs[1].setImageResource(R.drawable.mizige1);
		mWordPaintIVs[2].setImageResource(R.drawable.mizige1);
		mWordPaintIVs[3].setImageResource(R.drawable.mizige1);
		if (mPaintMap != null) {
			for (java.util.Map.Entry<Integer, Bitmap> entry : mPaintMap
					.entrySet()) {
				entry.getValue().recycle();
			}
			mPaintMap.clear();
		}
		getWord(mGameLevel);
	}

	private void setPinyin(String pinyin) {
		if (pinyin == null)
			return;
		int n = pinyin.indexOf(" ");
		if (n == -1)
			return;
		try {
			String[] s = pinyin.split(" ");
			mPinYinTVs[0].setText(s[0]);
			mPinYinTVs[1].setText(s[1]);
			mPinYinTVs[2].setText(s[2]);
			mPinYinTVs[3].setText(s[3]);
		} catch (Exception e) {

		}
	}

	private int updateResult(final String result) {
		if (result == null)
			return -1;
		else {
			ContentValues values = new ContentValues();
			values.put(DictateColums.RESULT, result);
			try {
				return getContentResolver().update(
						DictateColums.CONTENT_URI,
						values,
						" _id = '"
								+ mWordCursor.getInt(mWordCursor
										.getColumnIndex("_id")) + "'", null);
			} catch (Exception e) {
				Log.e(WriteDetailActivity.class.getSimpleName(),
						"update result exception : " + e.toString());
				e.printStackTrace();
				return -1;
			}
		}
	}

	private void showResult() {
		if (mResultView == null)
			mResultView = new ResultView(this);
		View v = mResultView.getView(mWord, wordIndex);
		mResultView.setImage(mPaintMap, wordIndex);
		if (mrListener == null)
			mrListener = new ResultListener();
		v.findViewById(R.id.dictate_right).setOnClickListener(mrListener);
		v.findViewById(R.id.dictate_wrong).setOnClickListener(mrListener);
		v.findViewById(R.id.dictate_shared).setOnClickListener(mrListener);
		if (resultDialog == null) {
			Builder mBuilder = new Builder(this);
			mBuilder.setView(v);
			resultDialog = mBuilder.create();
		}
		resultDialog.show();
	}

	private class ResultListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.dictate_right:
				//right
				updateResult("right");
				nextWord();
				break;
			case R.id.dictate_wrong:
				//wrong
				updateResult("wrong");
				nextWord();
				break;
			case R.id.dictate_shared:
				// TODO shared the picture
				break;
			default:
				break;
			}
			resultDialog.dismiss();
			mResultView.setImage(null, null);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mPaintMap != null) {
			for (java.util.Map.Entry<Integer, Bitmap> entry : mPaintMap
					.entrySet()) {
				entry.getValue().recycle();
			}
			mPaintMap.clear();
		}
		if (mWordCursor != null)
			mWordCursor.close();
	}

	private void setWord(String s) {
		int i = 0;
		try {
			mWordTVs[0].setText(s.charAt(i) + "");
			mFirstLayout.setVisibility(View.VISIBLE);
			mWordTVs[0].setVisibility(View.VISIBLE);
			i++;
			mWordTVs[1].setText(s.charAt(i) + "");
			mSecondLayout.setVisibility(View.VISIBLE);
			mWordTVs[1].setVisibility(View.VISIBLE);
			i++;
			mWordTVs[2].setText(s.charAt(i) + "");
			mThirdLayout.setVisibility(View.VISIBLE);
			mWordTVs[2].setVisibility(View.VISIBLE);
			i++;
			mWordTVs[3].setText(s.charAt(i) + "");
			mFourthLayout.setVisibility(View.VISIBLE);
			mWordTVs[3].setVisibility(View.VISIBLE);
		} catch (Exception e) {
			Log.e(WriteDetailActivity.class.getName(), "" + e.toString());
			switch (i) {
			case 2:
				mThirdLayout.setVisibility(View.GONE);
				mFourthLayout.setVisibility(View.GONE);
				break;
			case 3:
				mFourthLayout.setVisibility(View.GONE);
				break;

			default:
				break;
			}
		}
	}

	private void setValue(Cursor c) {
		if (!c.moveToNext()) {
			if (mGameLevel == null) {
				mGameLevel = "初级";
			} else if (mGameLevel.equals("初级")) {
				mGameLevel = "中级";
			} else {
				mGameLevel = "高级";
			}
			wordInCursor = -1;
			c.close();
			mWordCursor.close();
			mWordCursor = null;
			getWord(mGameLevel);
			return;
		}
		boolean fromcache = mDictateWordImage.loadImage(
				PIC_PATH + c.getInt(c.getColumnIndex("_id")) + ".jpg", false,
				listener);
		if (!fromcache) {
			mDictateWordImage.setImageResource(R.drawable.default_img);
		}
		// mDictateWordImage.setImageResource(R.drawable.test);
		mWord = c.getString(c.getColumnIndex(DictateData.DictateColums.NAME))
				.trim();
		setWord(mWord);
		setPinyin(c.getString(c
				.getColumnIndex(DictateData.DictateColums.PINYIN)));
		String s = c.getString(c.getColumnIndex(DictateColums.ALLUSION));
		if (s != null && !s.equals("null")&&!s.isEmpty()) {
			mDictateAllusion.setText(c.getString(c
					.getColumnIndex(DictateColums.ALLUSION)) + "");
			setViewVisiable(2, true);
		} else {
			setViewVisiable(2, false);
		}
		s = c.getString(c.getColumnIndex(DictateData.DictateColums.COMMENT));
		if (s != null && !s.equals("null")&&!s.isEmpty()) {
			mDictateComment.setText(c.getString(c
					.getColumnIndex(DictateData.DictateColums.COMMENT)));
		} else {
			mDictateComment.setText("");
		}
		s = c.getString(c.getColumnIndex(DictateData.DictateColums.EXAMPLE));
		if (s != null && !s.equals("null")&&!s.isEmpty()) {
			mDictateExample.setText(c.getString(c
					.getColumnIndex(DictateData.DictateColums.EXAMPLE)) + "");
			setViewVisiable(1, true);
		} else {
			setViewVisiable(1, false);
		}
		s = c.getString(c.getColumnIndex(DictateData.DictateColums.ORIGINAL));
		if (s != null && !s.equals("null")&&!s.isEmpty()) {
			mDictateOriginal.setText(c.getString(c
					.getColumnIndex(DictateData.DictateColums.ORIGINAL)) + "");
			setViewVisiable(0, true);
		} else {
			setViewVisiable(0, false);
		}
		if (!fromcache) {
			mImgDescription.setText("需要网络连接才能获取图片");
			mImgDescriptionStr = c.getString(c
					.getColumnIndex(DictateData.DictateColums.IMG_DES)) + "";
		} else
			mImgDescription.setText(c.getString(c
					.getColumnIndex(DictateData.DictateColums.IMG_DES)) + "");
		s = c.getString(c.getColumnIndex(DictateData.DictateColums.DICTATE))
				.trim();
		int len = s.length();
		for (int j = 0; j < len; j++) {
			showPaint(Integer.valueOf(s.charAt(j) + ""));
		}
		s = null;
	}

	private OnImageLoadedListener listener = new OnImageLoadedListener() {

		@Override
		public void onImageLoaded(CacheableBitmapDrawable result) {
			// TODO Auto-generated method stub
			if (result != null) {
				mHandler.obtainMessage(IMG_LOADED).sendToTarget();
			} else {
				mDictateWordImage.setImageResource(R.drawable.default_img);
			}
		}
	};

	private void setViewVisiable(int index, boolean flag) {
		int visiable;
		if (flag) {
			visiable = View.VISIBLE;
		} else {
			visiable = View.GONE;
		}
		switch (index) {
		case 0:
			mDictateOriginal.setVisibility(visiable);
			mOriginalView.setVisibility(visiable);
			break;
		case 1:
			mDictateExample.setVisibility(visiable);
			mExampleView.setVisibility(visiable);
			break;
		case 2:
			mDictateAllusion.setVisibility(visiable);
			mAllusionView.setVisibility(visiable);
			break;
		default:
			break;
		}
	}

}
