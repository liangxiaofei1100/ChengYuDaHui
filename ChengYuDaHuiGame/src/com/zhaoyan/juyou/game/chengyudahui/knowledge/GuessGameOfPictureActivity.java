package com.zhaoyan.juyou.game.chengyudahui.knowledge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.kirin.d.d;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.WordData.WordColums;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;
import com.zhaoyan.juyou.game.chengyudahui.view.Effectstype;
import com.zhaoyan.juyou.game.chengyudahui.view.NiftyDialogBuilder;

public class GuessGameOfPictureActivity extends Activity implements OnItemClickListener, OnClickListener{
	private static final String TAG = GuessGameOfPictureActivity.class.getSimpleName();
	
	private GridView mGridView;
	private MyAdapter mAdapter;
	private ImageView mTipView, mFreeView;
	private AnswerButton[] mAnswerBtns = new AnswerButton[4];
	
	private List<Word> mWordsList = new ArrayList<Word>();
	
	private SparseBooleanArray mAnswerArray = new SparseBooleanArray(4);
	String testStr  = "沉鱼落雁";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_guess_main);
		
		String path = getIntent().getStringExtra("path");
		Log.d(TAG, "path:" + path);
		
		mTipView = (ImageView) findViewById(R.id.iv_cy_tip);
		mFreeView = (ImageView) findViewById(R.id.iv_cy_free);
		
		mTipView.setOnClickListener(this);
		mFreeView.setOnClickListener(this);
		
		mAnswerBtns[0] = (AnswerButton) findViewById(R.id.btn_answer_01);
		mAnswerBtns[1] = (AnswerButton) findViewById(R.id.btn_answer_02);
		mAnswerBtns[2] = (AnswerButton) findViewById(R.id.btn_answer_03);
		mAnswerBtns[3] = (AnswerButton) findViewById(R.id.btn_answer_04);
		mAnswerBtns[0].setOnClickListener(this);
		mAnswerBtns[1].setOnClickListener(this);
		mAnswerBtns[2].setOnClickListener(this);
		mAnswerBtns[3].setOnClickListener(this);
		
		
		mWordsList = getData(testStr);

		mGridView = (GridView) findViewById(R.id.gv_cy_word);
		mAdapter = new MyAdapter(getApplicationContext());
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
		
		//init 
		for (int i = 0; i < 4; i++) {
			mAnswerArray.put(i, false);
		}
	}
	
	/**
	 * 生成包括答案在内的24个汉字
	 * @param chengyu
	 * @return
	 */
	private List<Word> getData(String chengyu){
		List<String> list = new ArrayList<String>();
		//从数据库中取得20个随机汉字
		Cursor cursor = getContentResolver().query(
				WordColums.CONTENT_URI, new String[] {"word"}, null,
				null, null);
		Log.d(TAG, "cursor.count=" + cursor.getCount());
		
		//生成20个不重复的随机数
		Set<Integer> set = Utils.getRandomNums(20, 4766);
		String letter = "";
		Iterator<Integer> iterator = set.iterator();
		while (iterator.hasNext()) {
			cursor.moveToPosition(iterator.next());
			letter = cursor.getString(cursor.getColumnIndex("word"));
			list.add(letter);
		}
		cursor.close();
		
		//将当前正确答案成语添加到list中
		//一定是四字成语哦
		for (int i = 0; i < 4; i++) {
			list.add(chengyu.substring(i, i+1));
		}
		//随机排序
		Collections.sort(list);
		
		List<Word> wordList = new ArrayList<Word>();
		Word word = null;
		for(String string : list){
			//得到每个字所在的位置
			int position = list.indexOf(string);
			word = new Word();
			word.setPosition(position);
			word.setWord(string);
			wordList.add(word);
		}
		return wordList;
	}
	
	class MyAdapter extends BaseAdapter{
		private LayoutInflater mInflater = null;
		
		private SparseBooleanArray mVisibleArray;
		
		public MyAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mInflater = LayoutInflater.from(context);
			mVisibleArray = new SparseBooleanArray(mWordsList.size());
			for (int i = 0; i < mWordsList.size(); i++) {
				mVisibleArray.put(i, true);
			}
		}
		
		public void setVisibile(int position, boolean visible){
			mVisibleArray.put(position, visible);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mWordsList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.picture_guess_item, null);
			
			View itemView = view.findViewById(R.id.rl_guess_item);
			itemView.setVisibility(mVisibleArray.get(position) ? View.VISIBLE : View.INVISIBLE);
			
			TextView wordBtn = (TextView) view.findViewById(R.id.tv_cy_word);
			wordBtn.setText(mWordsList.get(position).getWord());
			return view;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d(TAG, "onItemClick");
		Word word2 = mWordsList.get(position);
		
		if (mAnswerArray.indexOfValue(false) == -1 ) {
			//四个答案格斗填满了，而且都是错的，那么再来的话 就重新开始吧
			Word word = null;
			for (int i = 0; i < 4; i++) {
//				word = mWordsList.get(mAnswerBtns[i].getWord().getPosition());
//				word.setVisibile(true);
				mAdapter.setVisibile(mAnswerBtns[i].getWord().getPosition(), true);
				mAnswerArray.put(i, false);
				mAnswerBtns[i].setWord(null);
			}
			setButtonsColor(Color.BLACK);
		}
		
		for (int i = 0; i < 4; i++) {
			if (!mAnswerArray.get(i)) {
				mAnswerArray.put(i, true);
				mAnswerBtns[i].setWord(word2);
				mAdapter.setVisibile(position, false);
				mAdapter.notifyDataSetChanged();
				
				if (mAnswerArray.indexOfValue(false) == -1 ) {
					String answer = "";
					for(Button button : mAnswerBtns){
						answer += button.getText();
					}
					if (answer.equals(testStr)) {
						//答对了，弹出对话框，显示该成语意思
						showRightDialog();
						setButtonsColor(Color.GREEN);
					} else {
						setButtonsColor(Color.RED);
					}
				}
				return;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_cy_tip:
			break;
		case R.id.iv_cy_free:
			break;
		case R.id.btn_answer_01:
			setButtonsColor(Color.BLACK);
			
			Word word = mAnswerBtns[0].getWord();
			mAdapter.setVisibile(word.getPosition(), true);
			mAdapter.notifyDataSetChanged();
			
			mAnswerBtns[0].setWord(null);
			mAnswerArray.put(0, false);
			break;
		case R.id.btn_answer_02:
			setButtonsColor(Color.BLACK);
			
			Word word1 = mAnswerBtns[1].getWord();
			mAdapter.setVisibile(word1.getPosition(), true);
			mAdapter.notifyDataSetChanged();
			
			mAnswerBtns[1].setWord(null);
			mAnswerArray.put(1, false);
			break;
		case R.id.btn_answer_03:
			setButtonsColor(Color.BLACK);
			
			Word word2 = mAnswerBtns[2].getWord();
			mAdapter.setVisibile(word2.getPosition(), true);
			mAdapter.notifyDataSetChanged();
			
			mAnswerBtns[2].setWord(null);
			mAnswerArray.put(2, false);
			break;
		case R.id.btn_answer_04:
			setButtonsColor(Color.BLACK);
			
			Word word3 = mAnswerBtns[3].getWord();
			mAdapter.setVisibile(word3.getPosition(), true);
			mAdapter.notifyDataSetChanged();
			
			mAnswerBtns[3].setWord(null);
			mAnswerArray.put(3, false);
			break;

		default:
			break;
		}
	}
	
	private void setButtonsColor(int color){
		for(Button button : mAnswerBtns){
			button.setTextColor(color);
		}
	}
	
	/**
	 * @param n 
	 */
	private void clickAnswerButton(int n){
		setButtonsColor(Color.BLACK);
		
		Word word = mAnswerBtns[n].getWord();
		mAdapter.setVisibile(word.getPosition(), true);
		mAdapter.notifyDataSetChanged();
		
		mAnswerBtns[n].setWord(null);
		mAnswerArray.put(n, false);
	}
	
	private void showRightDialog(){
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
		dialogBuilder.withTitle(testStr)
		// .withTitle(null) no title
		.withTitleColor("#000000")
		// def
		.withDividerColor("#11000000")
		// def
		.withMessage("成语解释在这里.")
		// .withMessage(null) no Msg
		.withMessageColor("#000000")
		// def
		.withIcon(getResources().getDrawable(R.drawable.ic_launcher))
		.isCancelableOnTouchOutside(true) // def | isCancelable(true)
		.withDuration(700) // def
		.withEffect(Effectstype.SlideBottom) // def Effectstype.Slidetop
		.withButton1Text("下一题") // def gone
		.setCustomView(R.layout.custom_view, getApplicationContext()) // .setCustomView(View
																// ResId,context)
		.setButton1Click(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogBuilder.dismiss();
				Toast.makeText(v.getContext(), "NExt",
						Toast.LENGTH_SHORT).show();
			}
		});
		dialogBuilder.setCancelable(false);
		dialogBuilder.show();
	}
	
}
