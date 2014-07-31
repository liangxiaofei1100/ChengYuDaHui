package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.paint.PaintGameActivty;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class DictateActivity extends Activity implements OnClickListener {
	private TextView mDictateWordPinyin, mFirstPinyin, mSecondPinyin,
			mThirdPinyin, mFourthPinyin, mDictateWordFirst, mDictateWordFourth,
			mDictateWordSecond, mDictateWordThird, mDictateWordComment;
	private ImageView mDictateWordImage, mFirstPaintImg, mSecondPaintImg,
			mThirdPaintImg, mFourthPaintImg;
	private View mFirstLayout, mSecondLayout, mThirdLayout, mFourthLayout;
	private Random mIndexRandom;
	private String mWord;
	private Map<Integer, Bitmap> mPaintMap;
	private ResultView mResultView;
	private ResultListener mrListener;
	private AlertDialog resultDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView(R.layout.activity_dictate);
		getWord();
	}

	private void initView(int id) {
		setContentView(id);
		mDictateWordComment = (TextView) findViewById(R.id.dictate_word_comment);
		mDictateWordFirst = (TextView) findViewById(R.id.dictate_first_word);
		mDictateWordFourth = (TextView) findViewById(R.id.dictate_fourth_word);
		mDictateWordImage = (ImageView) findViewById(R.id.dictate_word_image);
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
		mDictateWordImage.setImageResource(R.drawable.test);
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
		if (mIndexRandom == null) {
			mIndexRandom = new Random();
		}
		mWord = "瓮中捉鳖";
		mDictateWordComment
				.setText("【释义】从大坛子里捉王八。比喻想要捕捉的对象已在掌握之中。形容手到擒来，轻易而有把握。\n【出处】元·康进之《李逵负荆》第四折：“这是揉着我山儿的痒处，管叫他瓮中捉鳖，手到拿来。");
		// String temp = mWord;
		int i = 0;
		try {
			mDictateWordFirst.setText(mWord.charAt(i) + "");
			i++;
			mDictateWordSecond.setText(mWord.charAt(i) + "");
			i++;
			mDictateWordThird.setText(mWord.charAt(i) + "");
			i++;
			mDictateWordFourth.setText(mWord.charAt(i) + "");
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
		setPinyin("wèng zhōng zhuō biē");

		switch (Math.abs(mIndexRandom.nextInt()) % mWord.length()) {
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
			Bitmap b = data.getParcelableExtra("data");
			int i = data.getIntExtra("index", -1);
			switch (i) {
			case 1:
				mFirstPaintImg.setImageBitmap(b);
				break;
			case 2:
				mSecondPaintImg.setImageBitmap(b);
				break;
			case 3:
				mThirdPaintImg.setImageBitmap(b);
				break;
			case 4:
				mFourthPaintImg.setImageBitmap(b);
				break;
			default:
				return;
			}
			mPaintMap.put(i, b);
		}
	}

	private void nextWord() {
		mFirstPaintImg.setVisibility(View.GONE);
		mSecondPaintImg.setVisibility(View.GONE);
		mSecondPaintImg.setVisibility(View.GONE);
		mFourthPaintImg.setVisibility(View.GONE);
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

	private void showResult() {
		if (mResultView == null)
			mResultView = new ResultView(this);
		View v = mResultView.getView(mWord);
		mResultView.setImage(mPaintMap);
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
				//TODO right
				break;
			case R.id.dictate_wrong:
				//TODO wrong
				break;
			default:
				break;
			}
			mResultView.setImage(null);
			resultDialog.dismiss();
		}
	}

}
