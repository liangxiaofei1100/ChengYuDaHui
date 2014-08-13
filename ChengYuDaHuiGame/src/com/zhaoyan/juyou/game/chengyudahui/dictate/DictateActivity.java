package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zhaoyan.communication.cache.CacheableBitmapDrawable;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.DictateData;
import com.zhaoyan.juyou.game.chengyudahui.db.DictateData.DictateColums;
import com.zhaoyan.juyou.game.chengyudahui.download.Conf;
import com.zhaoyan.juyou.game.chengyudahui.download.NetworkCacheableImageView;
import com.zhaoyan.juyou.game.chengyudahui.download.NetworkCacheableImageView.OnImageLoadedListener;
import com.zhaoyan.juyou.game.chengyudahui.paint.PaintGameActivty;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class DictateActivity extends ActionBarActivity implements
		OnClickListener {
	private TextView mDictateWordPinyin, mFirstPinyin, mSecondPinyin,
			mThirdPinyin, mFourthPinyin, mDictateWordFirst, mDictateWordFourth,
			mDictateWordSecond, mDictateWordThird, mDictateWordComment;
	private ImageView mFirstPaintImg, mSecondPaintImg, mThirdPaintImg,
			mFourthPaintImg;
	private NetworkCacheableImageView mDictateWordImage;
	private View mFirstLayout, mSecondLayout, mThirdLayout, mFourthLayout;
	private Random mIndexRandom;
	private String mWord;
	private Map<Integer, Bitmap> mPaintMap;
	private ResultView mResultView;
	private ResultListener mrListener;
	private AlertDialog resultDialog;
	private boolean testFlag = false;
	private List<Integer> wordIndex;
	private TextView mDictateComment, mDictateOriginal, mDictateExample,
			mDictateAllusion, mImgDescription;
	private final String PIC_PATH = Conf.URL_EX + Conf.LISTEN_DIR;
	private final int IMG_LOADED = 0;
	private String mImgDescriptionStr;
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView(R.layout.activity_dictate);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("汉字听写");
		getWord();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void initView(int id) {
		setContentView(id);
		mDictateWordComment = (TextView) findViewById(R.id.dictate_word_comment);
		mDictateWordFirst = (TextView) findViewById(R.id.dictate_first_word);
		mDictateWordFourth = (TextView) findViewById(R.id.dictate_fourth_word);
		mDictateWordImage = (NetworkCacheableImageView) findViewById(R.id.dictate_word_image);
		mDictateWordPinyin = (TextView) findViewById(R.id.dictate_pinyin);
		mDictateWordSecond = (TextView) findViewById(R.id.dictate_second_word);
		mDictateWordThird = (TextView) findViewById(R.id.dictate_third_word);
		mFirstPaintImg = (ImageView) findViewById(R.id.dictate_first_word_img);
		mSecondPaintImg = (ImageView) findViewById(R.id.dictate_second_word_img);
		mThirdPaintImg = (ImageView) findViewById(R.id.dictate_third_word_img);
		mFourthPaintImg = (ImageView) findViewById(R.id.dictate_fourth_word_img);
		mFirstPinyin = (TextView) findViewById(R.id.first_pinyin);
		mSecondPinyin = (TextView) findViewById(R.id.second_pinyin);
		mThirdPinyin = (TextView) findViewById(R.id.third_pinyin);
		mFourthPinyin = (TextView) findViewById(R.id.fourth_pinyin);
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

	private void getWord() {
		if (wordIndex == null)
			wordIndex = new ArrayList<Integer>();
		wordIndex.clear();
		int id;
		if (mIndexRandom == null) {
			mIndexRandom = new Random();
		}
		Cursor c = getContentResolver().query(DictateColums.CONTENT_URI,
				new String[] { DictateColums.NAME }, null, null, null);
		if (c != null && c.getCount() > 0 && !testFlag) {
			id = Math.abs(mIndexRandom.nextInt()) % c.getCount();
			c.close();
			c = getContentResolver().query(
					DictateColums.CONTENT_URI,
					new String[] { "_id", DictateColums.NAME,
							DictateColums.PINYIN, DictateColums.COMMENT,
							DictateColums.DICTATE, DictateColums.ORIGINAL,
							DictateColums.EXAMPLE, DictateColums.IMG_DES,
							DictateColums.LEVEL, DictateColums.ALLUSION },
					"_id = " + id, null, null);
			if (c != null && c.getCount() > 0) {
				c.moveToNext();
				try {
					setValue(c);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} finally {
					c.close();
				}
				return;
			}
		}
		mWord = "沉鱼落雁";
		mDictateWordComment.setText("鱼见之chén入水底，yàn见之降落沙洲，形容女子容貌的美丽");
		// String temp = mWord;
		setWord(mWord);
		setPinyin("chén yú luò yàn");
		showPaint(0);
		showPaint(3);
	}

	private void showPaint(int index) {
		wordIndex.add(index);
		switch (Math.abs(index) % mWord.length()) {
		case 0:
			mDictateWordFirst.setVisibility(View.GONE);
			mFirstPaintImg.setVisibility(View.VISIBLE);
			mFirstPaintImg.setClickable(true);
			mFirstPaintImg.setOnClickListener(this);
			break;
		case 1:
			mDictateWordSecond.setVisibility(View.GONE);
			mSecondPaintImg.setVisibility(View.VISIBLE);
			mSecondPaintImg.setClickable(true);
			mSecondPaintImg.setOnClickListener(this);
			break;
		case 2:
			mDictateWordThird.setVisibility(View.GONE);
			mThirdPaintImg.setVisibility(View.VISIBLE);
			mThirdPaintImg.setClickable(true);
			mThirdPaintImg.setOnClickListener(this);
			break;
		case 3:
			mDictateWordFourth.setVisibility(View.GONE);
			mFourthPaintImg.setVisibility(View.VISIBLE);
			mFourthPaintImg.setClickable(true);
			mFourthPaintImg.setOnClickListener(this);
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
				mFirstPaintImg.setImageBitmap(temp);
				break;
			case 2:
				mSecondPaintImg.setImageBitmap(temp);
				break;
			case 3:
				mThirdPaintImg.setImageBitmap(temp);
				break;
			case 4:
				mFourthPaintImg.setImageBitmap(temp);
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
		mFirstPaintImg.setVisibility(View.GONE);
		mSecondPaintImg.setVisibility(View.GONE);
		mThirdPaintImg.setVisibility(View.GONE);
		mFourthPaintImg.setVisibility(View.GONE);
		mFirstPaintImg.setImageResource(R.drawable.mizige1);
		mSecondPaintImg.setImageResource(R.drawable.mizige1);
		mThirdPaintImg.setImageResource(R.drawable.mizige1);
		mFourthPaintImg.setImageResource(R.drawable.mizige1);
		if (mPaintMap != null) {
			for (java.util.Map.Entry<Integer, Bitmap> entry : mPaintMap
					.entrySet()) {
				entry.getValue().recycle();
			}
			mPaintMap.clear();
		}
		getWord();
	}

	private void setPinyin(String pinyin) {
		if (pinyin == null)
			return;
		int n = pinyin.indexOf(" ");
		if (n == -1)
			return;
		try {
			String[] s = pinyin.split(" ");
			mFirstPinyin.setText(s[0]);
			mSecondPinyin.setText(s[1]);
			mThirdPinyin.setText(s[2]);
			mFourthPinyin.setText(s[3]);
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
				return getContentResolver()
						.update(DictateColums.CONTENT_URI, values,
								DictateColums.NAME + " = '" + mWord + "'", null);
			} catch (Exception e) {
				Log.e(DictateActivity.class.getSimpleName(),
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
				// TODO right
				updateResult("right");
				nextWord();
				break;
			case R.id.dictate_wrong:
				// TODO wrong
				// if (!testFlag) {
				// ContentValues values = new ContentValues();
				// getContentResolver()
				// .insert(HistoryColums.CONTENT_URI, null);
				// }
				updateResult("wrong");
				nextWord();
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
	}

	private void setWord(String s) {
		int i = 0;
		try {
			mDictateWordFirst.setText(s.charAt(i) + "");
			mFirstLayout.setVisibility(View.VISIBLE);
			mDictateWordFirst.setVisibility(View.VISIBLE);
			i++;
			mDictateWordSecond.setText(s.charAt(i) + "");
			mSecondLayout.setVisibility(View.VISIBLE);
			mDictateWordSecond.setVisibility(View.VISIBLE);
			i++;
			mDictateWordThird.setText(s.charAt(i) + "");
			mThirdLayout.setVisibility(View.VISIBLE);
			mDictateWordThird.setVisibility(View.VISIBLE);
			i++;
			mDictateWordFourth.setText(s.charAt(i) + "");
			mFourthLayout.setVisibility(View.VISIBLE);
			mDictateWordFourth.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			Log.e(DictateActivity.class.getName(), "" + e.toString());
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
		boolean fromcache = mDictateWordImage.loadImage(
				PIC_PATH + c.getInt(c.getColumnIndex("_id")) + ".jpg", false,
				listener);
		if (!fromcache) {
			mDictateWordImage.setImageResource(R.drawable.ic_launcher);
		}
		mWord = c.getString(c.getColumnIndex(DictateData.DictateColums.NAME))
				.trim();
		setWord(mWord);
		setPinyin(c.getString(c
				.getColumnIndex(DictateData.DictateColums.PINYIN)));
		String s = c.getString(c.getColumnIndex(DictateColums.ALLUSION));
		if (s != null && !s.equals("null")) {
			mDictateAllusion.setText(c.getString(c
					.getColumnIndex(DictateColums.ALLUSION)) + "");
		} else {
			mDictateAllusion.setText("无");
		}
		s = c.getString(c.getColumnIndex(DictateData.DictateColums.COMMENT));
		if (s != null && !s.equals("null")) {
			mDictateComment.setText(c.getString(c
					.getColumnIndex(DictateData.DictateColums.COMMENT)));
		} else {
			mDictateComment.setText("无");
		}
		s = c.getString(c.getColumnIndex(DictateData.DictateColums.EXAMPLE));
		if (s != null && !s.equals("null")) {
			mDictateExample.setText(c.getString(c
					.getColumnIndex(DictateData.DictateColums.EXAMPLE)) + "");
		} else {
			mDictateExample.setText("无");
		}
		s = c.getString(c.getColumnIndex(DictateData.DictateColums.ORIGINAL));
		if (s != null && !s.equals("null")) {
			mDictateOriginal.setText(c.getString(c
					.getColumnIndex(DictateData.DictateColums.ORIGINAL)) + "");
		} else {
			mDictateOriginal.setText("无");
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
			mHandler.obtainMessage(IMG_LOADED).sendToTarget();
		}
	};
}
