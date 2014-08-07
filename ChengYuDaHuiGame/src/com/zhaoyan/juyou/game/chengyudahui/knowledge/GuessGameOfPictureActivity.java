package com.zhaoyan.juyou.game.chengyudahui.knowledge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.ChengyuData.WordColums;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;

public class GuessGameOfPictureActivity extends Activity implements OnItemClickListener, OnClickListener{
	private static final String TAG = GuessGameOfPictureActivity.class.getSimpleName();
	
	private GridView mGridView;
	
	private MyAdapter mAdapter;
	
	private ImageView mTipView, mFreeView;
	
	private List<String> mWordsList = new ArrayList<String>();
	
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
		
		String testStr  = "沉鱼落雁";
		mWordsList = getData(testStr);

		mGridView = (GridView) findViewById(R.id.gv_cy_word);
		mAdapter = new MyAdapter(getApplicationContext());
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
	}
	
	/**
	 * 生成包括答案在内的24个汉字
	 * @param chengyu
	 * @return
	 */
	private List<String> getData(String chengyu){
		List<String> list = new ArrayList<String>();
		//从数据库中取得20个随机汉字
		Cursor cursor = getContentResolver().query(
				WordColums.CONTENT_URI, new String[] {"word"}, null,
				null, null);
		Log.d(TAG, "cursor.count=" + cursor.getCount());
		
		//生成20个不重复的随机数
		Set<Integer> set = Utils.getRandomNums(20, 4766);
		String word = "";
		Iterator<Integer> iterator = set.iterator();
		while (iterator.hasNext()) {
			cursor.moveToPosition(iterator.next());
			word = cursor.getString(cursor.getColumnIndex("word"));
			list.add(word);
		}
		cursor.close();
		
		//将当前正确答案成语添加到list中
		//一定是四字成语哦
		for (int i = 0; i < 4; i++) {
			list.add(chengyu.substring(i, i+1));
		}
		//随机排序
		Collections.sort(list);
		return list;
	}
	
	class MyAdapter extends BaseAdapter{
		private LayoutInflater mInflater = null;
		
		public MyAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mInflater = LayoutInflater.from(context);
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
			// TODO Auto-generated method stub
			View view = mInflater.inflate(R.layout.picture_guess_item, null);
			TextView wordBtn = (TextView) view.findViewById(R.id.tv_cy_word);
			wordBtn.setText(mWordsList.get(position));
			return view;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onItemClick");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_cy_tip:
			
			break;
		case R.id.iv_cy_free:
			break;

		default:
			break;
		}
	}
	
	private void insertTo(ContentResolver contentResolver, String word){
		ContentValues values = new ContentValues();
		values.put(NoteMetaData.Note.WORD, word);
		contentResolver.insert(NoteMetaData.Note.CONTENT_URI, values);
	}
	
}
