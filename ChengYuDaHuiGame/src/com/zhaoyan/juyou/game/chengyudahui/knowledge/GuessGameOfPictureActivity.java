package com.zhaoyan.juyou.game.chengyudahui.knowledge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
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

import com.baidu.frontia.Frontia;
import com.baidu.frontia.api.FrontiaAuthorization.MediaType;
import com.baidu.frontia.api.FrontiaSocialShare;
import com.baidu.frontia.api.FrontiaSocialShare.FrontiaTheme;
import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.baidu.frontia.api.FrontiaSocialShareListener;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.BaikeActivity;
import com.zhaoyan.juyou.game.chengyudahui.db.WordData.WordColums;
import com.zhaoyan.juyou.game.chengyudahui.download.Conf;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;
import com.zhaoyan.juyou.game.chengyudahui.view.Effectstype;
import com.zhaoyan.juyou.game.chengyudahui.view.NiftyDialogBuilder;

public class GuessGameOfPictureActivity extends Activity implements OnItemClickListener, OnClickListener{
	private static final String TAG = GuessGameOfPictureActivity.class.getSimpleName();
	
	private GridView mGridView;
	private PicGuessAdapter mAdapter;
	private ImageView mTipView, mFreeView;
	private ImageView mBackView;
	
	private AnswerButton[] mAnswerBtns = new AnswerButton[4];
	
	private List<Word> mWordsList = new ArrayList<Word>();
	
	private SparseBooleanArray mAnswerArray = new SparseBooleanArray(4);
	String testStr  = "沉鱼落雁";
	
	private FrontiaSocialShare mSocialShare;
	private FrontiaSocialShareContent mShareContent = new FrontiaSocialShareContent();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_guess_main);
		
		String path = getIntent().getStringExtra("path");
		Log.d(TAG, "path:" + path);
		
		initView();
		
		//init 
		initAnswerButtonArray();
		
		Frontia.init(this.getApplicationContext(), Conf.APIKEY);
		mSocialShare = Frontia.getSocialShare();
		mSocialShare.setContext(this);
		mSocialShare.setClientId(MediaType.WEIXIN.toString(), "wx329c742cb69b41b8");
		mShareContent.setTitle("成语大会");
		mShareContent.setContent("欢迎使用成语大会，相关问题请邮件dev_support@zhaoyan.com");
		mShareContent.setLinkUrl("http://developer.baidu.com/");
		mShareContent.setImageUri(Uri.parse("http://apps.bdimg.com/developer/static/04171450/developer/images/icon/terminal_adapter.png"));
	}
	
	private void initView(){
		mBackView = (ImageView) findViewById(R.id.iv_back);
		mTipView = (ImageView) findViewById(R.id.iv_cy_tip);
		mFreeView = (ImageView) findViewById(R.id.iv_cy_share);
		
		mBackView.setOnClickListener(this);
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
		mAdapter = new PicGuessAdapter(getApplicationContext());
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
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
		int count = cursor.getCount();
		Log.d(TAG, "cursor.count=" + count);
		
		//生成20个不重复的随机数
		Set<Integer> set = Utils.getRandomNums(20, count);
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
			//得到重新排序后每个字所在的位置
			int position = list.indexOf(string);
			word = new Word();
			word.setPosition(position);
			word.setWord(string);
			wordList.add(word);
		}
		return wordList;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d(TAG, "onItemClick");
		if (mAnswerArray.indexOfValue(false) == -1 ) {
			//四个答案格斗填满了，而且都是错的，那么再来的话 就重新开始吧
			//清楚掉当前答案框的内容
			clearAnswerButton();
		}
		
		Word word2 = mWordsList.get(position);
		
		//当用户点击grdiview中的字的时候，依次从第一个开始判断哪个框是空着的，就将用户选中的字放上去
		for (int i = 0; i < 4; i++) {
			if (!mAnswerArray.get(i)) {//
				mAnswerArray.put(i, true);//置为true，表示我这个框有字了
				mAnswerBtns[i].setWord(word2);
				mAdapter.setVisibile(position, false);//点击过了，就在gridview中隐藏掉
				mAdapter.notifyDataSetChanged();
				
				if (mAnswerArray.indexOfValue(false) == -1 ) {
					//用户已经找到四个字了
					String answer = "";
					for(Button button : mAnswerBtns){
						//将四个字组成一个词，看一下是不是我们的答案
						answer += button.getText();
					}
					if (answer.equals(testStr)) {
						//答对了，弹出对话框，显示该成语意思
						showRightDialog();
						setButtonsColor(Color.GREEN);
					} else {
						//打错了，将字体颜色改为红色，以提醒用户
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
		case R.id.iv_back:
			GuessGameOfPictureActivity.this.finish();
			break;
		case R.id.iv_cy_tip:
			break;
		case R.id.iv_cy_share:
			mSocialShare.show(GuessGameOfPictureActivity.this.getWindow().getDecorView(), 
					mShareContent, FrontiaTheme.LIGHT,  new ShareListener());
			break;
		case R.id.btn_answer_01:
			clickAnswerButton(1);
			break;
		case R.id.btn_answer_02:
			clickAnswerButton(2);
			break;
		case R.id.btn_answer_03:
			clickAnswerButton(3);
			break;
		case R.id.btn_answer_04:
			clickAnswerButton(4);
			break;

		default:
			break;
		}
	}
	
	private void initAnswerButtonArray(){
		for (int i = 0; i < 4; i++) {
			mAnswerArray.put(i, false);
		}
	}
	
	/**
	 * 设置答案框内文字的颜色，正常时黑色，答对是绿色，答错是红色
	 * @param color
	 */
	private void setButtonsColor(int color){
		for(Button button : mAnswerBtns){
			button.setTextColor(color);
		}
	}
	
	/**
	 * @param n 成语答案中的第几个答案框 1,2,3,4
	 */
	private void clickAnswerButton(int n){
		Log.d(TAG, "clickAnswerButton:" + n);
		//切换到数组下标
		n = n - 1;
		
		if (!mAnswerArray.get(n)) {
			//答案框内已经是空的话，就点击无效了
			return;
		}

		setButtonsColor(Color.BLACK);
		
		Word word = mAnswerBtns[n].getWord();
		mAdapter.setVisibile(word.getPosition(), true);
		mAdapter.notifyDataSetChanged();
		
		mAnswerBtns[n].setWord(null);
		mAnswerArray.put(n, false);
	}
	
	private void clearAnswerButton(){
		Log.d(TAG, "clearAnswerButton");
		setButtonsColor(Color.BLACK);
		
		//4 代表四个答案框
		Word word = null;
		for (int i = 0; i < 4; i++) {
			word = mAnswerBtns[i].getWord();
			mAdapter.setVisibile(word.getPosition(), true);
			mAnswerBtns[i].setWord(null);
			mAnswerArray.put(i, false);
		}
		mAdapter.notifyDataSetChanged();
	}
	
	private void showRightDialog(){
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
		dialogBuilder.withTitle(testStr)
		.withTitleColor("#000000")
		.withDividerColor("#11000000")
		.withMessage("成语解释在这里.")
		.withMessageColor("#000000")
		.withIcon(getResources().getDrawable(R.drawable.ic_launcher))
		.isCancelableOnTouchOutside(false) 
		.withDuration(450)
		.withEffect(Effectstype.SlideBottom) 
		.withButton1Text("继续闯关") 
		.withButton2Text("了解更多")
		.setCustomView(R.layout.custom_view, getApplicationContext()) 
		.setButton1Click(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogBuilder.dismiss();
				Toast.makeText(v.getContext(), "NExt",
						Toast.LENGTH_SHORT).show();
			}
		})
		.setButton2Click(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogBuilder.dismiss();
				Intent intent = new Intent();
				intent.setClass(GuessGameOfPictureActivity.this, BaikeActivity.class);
				intent.putExtra(BaikeActivity.KEYWORD, testStr);
				startActivity(intent);
			}
		})
		.show();
	}
	
	private class ShareListener implements FrontiaSocialShareListener {

		@Override
		public void onSuccess() {
			Log.d(TAG,"share success");
			Toast.makeText(getApplicationContext(), "分享成功", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onFailure(int errCode, String errMsg) {
			Log.d(TAG,"share errCode "+errCode + ";errMsg:" + errMsg);
		}

		@Override
		public void onCancel() {
			Log.d(TAG,"cancel ");
		}
		
	}
	
	class PicGuessAdapter extends BaseAdapter{
		private LayoutInflater mInflater = null;
		
		//用于标示Gridview中的item隐藏还是显示
		private SparseBooleanArray mVisibleArray;
		
		public PicGuessAdapter(Context context) {
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
	
}
