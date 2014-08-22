package com.zhaoyan.juyou.game.chengyudahui.knowledge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.ChengyuData.ChengyuColums;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class ChengYuJieLongFragment extends Fragment implements OnClickListener {
	private static final String TAG = ChengYuJieLongFragment.class
			.getSimpleName();

	private Toast mToast;

	private Button mBtnSend;
	private EditText mEditTextContent;
	private JieLongAdapter mAdapter;
	private ListView mListView;
	private List<JieLongMessageEntity> mJieLongData = new ArrayList<JieLongMessageEntity>();
	private KnowledgeMainActivity mActivity;
	private ChengYu mChengYuAnswer;
	private ChengYu mChengYuQuestion;

	private static final String[] PROJECTION = { ChengyuColums._ID,
			ChengyuColums.NAME, ChengyuColums.PINYIN };

	private int mTargetNumber = 3;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (KnowledgeMainActivity) activity;
		mActivity.setTitle("成语接龙");
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.chengyu_jielong, container,
				false);
		initView(rootView);
		initData();
		return rootView;
	}

	private void initView(View rootView) {
		mBtnSend = (Button) rootView.findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);

		Button tipsButton = (Button) rootView.findViewById(R.id.btn_tips);
		tipsButton.setOnClickListener(this);

		mEditTextContent = (EditText) rootView
				.findViewById(R.id.et_sendmessage);
		mEditTextContent
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {

						if (actionId == EditorInfo.IME_ACTION_SEND
								|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							send();
							return true;
						}
						return false;
					}
				});

		mListView = (ListView) rootView.findViewById(R.id.listview);
		mAdapter = new JieLongAdapter(mActivity, mJieLongData);
		mListView.setAdapter(mAdapter);
	}

	private void initData() {
		mChengYuQuestion = new ChengYu();
		mChengYuQuestion.name = "一鸣惊人";
		mChengYuQuestion.pinyin = "yī míng jīng rén";

		sendQuestion(mChengYuQuestion);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_tips:
			tips();
			break;
		case R.id.btn_send:
			send();
			break;
		}
	}

	private void tips() {
		JieLongMessageEntity entity = mJieLongData.get(mJieLongData.size() - 1);
		if (!entity.isComeMessage) {
			return;
		}
		List<ChengYu> answers = getNextChengYu(mChengYuQuestion);
		if (!answers.isEmpty()) {
			ChengYu answer = getFromMatchedChengYus(answers);
			mChengYuAnswer = answer;
			sendAnswer(mChengYuAnswer);

			if (mJieLongData.size() >= mTargetNumber * 2) {
				nextStage();
				return;
			}

			List<ChengYu> questions = getNextChengYu(answer);
			if (!questions.isEmpty()) {
				mChengYuQuestion = getFromMatchedChengYus(questions);
				sendQuestion(mChengYuQuestion);
			} else {
				toast("已经没有可以接龙的成语");
				nextStage();
			}
		} else {
			toast("已经没有可以接龙的成语");
			nextStage();
		}
	}

	private void send() {
		JieLongMessageEntity entity = mJieLongData.get(mJieLongData.size() - 1);
		if (!entity.isComeMessage) {
			return;
		}

		String contString = mEditTextContent.getText().toString().trim();
		if (contString.length() > 0) {
			ChengYu chengYu = new ChengYu();
			chengYu.name = contString;
			if (checkChengYu(chengYu)) {
				mChengYuAnswer = chengYu;
				sendAnswer(mChengYuAnswer);

				if (mJieLongData.size() >= mTargetNumber * 2) {
					nextStage();
				} else {
					preSendQuestion();
				}
			}
		}
	}

	private void preSendQuestion() {
		List<ChengYu> questions = getNextChengYu(mChengYuAnswer);
		if (!questions.isEmpty()) {
			mChengYuQuestion = getFromMatchedChengYus(questions);
			sendQuestion(mChengYuQuestion);
		} else {
			toast("已经没有可以接龙的成语");
			nextStage();
		}
	}

	private ChengYu getFromMatchedChengYus(List<ChengYu> chengYus) {
		if (chengYus.size() == 0) {
			return null;
		}

		for (ChengYu chengYu : chengYus) {
			List<ChengYu> nextChengYus = getNextChengYu(chengYu);
			chengYu.nextNumber = nextChengYus.size();
		}
		Collections.sort(chengYus);

		int index = 0;
		if (chengYus.size() > 3) {
			Random random = new Random();
			index = random.nextInt(3);
		}

		return chengYus.get(index);
	}

	/**
	 * 
	 * @param chengyu
	 * @return
	 */
	private List<ChengYu> getNextChengYu(ChengYu chengyu) {
		List<ChengYu> nextChengYus = new ArrayList<ChengYu>();

		String[] pinyins = chengyu.pinyin.split(PinYinUtil.PINYIN_SEPERATOR);
		String lastWordPinOfChengYu = pinyins[pinyins.length - 1];

		ContentResolver resolver = mActivity.getContentResolver();
		String selection = ChengyuColums.PINYIN + " like '"
				+ lastWordPinOfChengYu + " %'";
		Log.d(TAG, "getNextChengYu selecion = " + selection);
		Cursor cursor = resolver.query(ChengyuColums.CONTENT_URI, PROJECTION,
				selection, null, null);
		if (cursor != null) {
			int i = 0;
			while (cursor.moveToNext()) {
				ChengYu tmp = new ChengYu();
				tmp.name = cursor.getString(cursor
						.getColumnIndex(ChengyuColums.NAME));
				tmp.pinyin = cursor.getString(cursor
						.getColumnIndex(ChengyuColums.PINYIN));
				nextChengYus.add(tmp);
				i++;
				if (i == 5) {
					break;
				}
			}
			cursor.close();
		}

		return nextChengYus;
	}

	/**
	 * 1. chengyu exist. 2. chengyu pinyin match
	 * 
	 * @param chengyu
	 * @return
	 */
	private boolean checkChengYu(ChengYu chengyu) {
		Log.d(TAG, "checkChengYu chengyu " + chengyu + ", question = "
				+ mChengYuQuestion);

		String[] pinyins = mChengYuQuestion.pinyin
				.split(PinYinUtil.PINYIN_SEPERATOR);
		String lastWordPinOfQuestionChengYu = pinyins[pinyins.length - 1];

		ContentResolver resolver = mActivity.getContentResolver();
		String selection = ChengyuColums.NAME + "='" + chengyu.name + "' and "
				+ ChengyuColums.PINYIN + " like '"
				+ lastWordPinOfQuestionChengYu + " %'";
		Log.d(TAG, "checkChengYu selecion = " + selection);
		Cursor cursor = resolver.query(ChengyuColums.CONTENT_URI, PROJECTION,
				selection, null, null);
		if (cursor != null) {
			try {
				if (cursor.getCount() > 0 && cursor.moveToFirst()) {
					chengyu.pinyin = cursor.getString(cursor
							.getColumnIndex(ChengyuColums.PINYIN));
					return true;
				}
			} catch (Exception e) {
				Log.e(TAG, "checkChengYu " + e);
			} finally {
				cursor.close();
			}
		}
		toast("成语不匹配");
		return false;
	}

	private void toast(String message) {
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toast.makeText(mActivity, message, Toast.LENGTH_SHORT);
		mToast.show();
	}

	private void sendAnswer(ChengYu chengyu) {
		JieLongMessageEntity entity = new JieLongMessageEntity();
		entity.isComeMessage = false;
		entity.text = chengyu.name;
		mJieLongData.add(entity);
		mAdapter.notifyDataSetChanged();

		mEditTextContent.setText("");
	}

	private void sendQuestion(ChengYu chengyu) {
		JieLongMessageEntity entity = new JieLongMessageEntity();
		entity.isComeMessage = true;
		entity.text = chengyu.name;
		mJieLongData.add(entity);
		mAdapter.notifyDataSetChanged();
	}

	private void nextStage() {
		// TODO
		toast("恭喜过关");
	}

	public static class ChengYu implements Comparable<ChengYu> {
		String name;
		String pinyin;
		int nextNumber;

		@Override
		public int compareTo(ChengYu arg0) {
			if (nextNumber > arg0.nextNumber) {
				return 1;
			} else if (nextNumber < arg0.nextNumber) {
				return -1;
			}
			return 0;
		}
	}
}
