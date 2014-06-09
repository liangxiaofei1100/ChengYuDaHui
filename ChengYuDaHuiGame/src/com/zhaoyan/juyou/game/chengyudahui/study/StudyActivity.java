package com.zhaoyan.juyou.game.chengyudahui.study;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zhaoyan.juyou.game.chengyudahui.MainActivity;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.ChengyuData.ChengyuColums;
import com.zhaoyan.juyou.game.chengyudahui.db.HistoryData.HistoryColums;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class StudyActivity extends Activity {
	private final String TAG = StudyActivity.class.getName();
	private EditText mSearhEditor;
	private ListView mChengyuListView;
	private ChengyuQuery mChengyuQuery;
	private final int QUERY_TOKEN = 100;
	private List<Map<String, String>> mSourceList;
	private SimpleAdapter mListAdapter;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_layout);
		mSearhEditor = (EditText) findViewById(R.id.search_edit);
		mChengyuListView = (ListView) findViewById(R.id.chengyu_list);
		mSearhEditor.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null) {
					String temp = arg0.toString();
					if (temp.trim().length() > 0) {
						queryChengyu(temp);
					}
				}
			}
		});
		if (mSourceList == null)
			mSourceList = new ArrayList<Map<String, String>>();
		mListAdapter = new SimpleAdapter(this, mSourceList,
				R.layout.study_list_item_layout, new String[] {
						ChengyuColums.PINYIN, ChengyuColums.NAME }, new int[] {
						R.id.text1, R.id.text2 });
		mChengyuListView.setAdapter(mListAdapter);
		mChengyuListView.setOnItemClickListener(new ChengyuItemClick());
		randomQuery();
	}

	private void randomQuery() {
		Random random = new Random();
		String s = "";
		for (int i = 0; i < 15; i++) {
			s = s + "'" + Math.abs(random.nextInt()) % MainActivity.DB_NUMBER
					+ "',";
		}
		s = s.substring(0, s.length() - 1);
		Log.e(StudyActivity.class.getName(), s);
		if (mChengyuQuery == null)
			mChengyuQuery = new ChengyuQuery(getContentResolver());
		mChengyuQuery.cancelOperation(QUERY_TOKEN);
		mChengyuQuery.startQuery(QUERY_TOKEN, null, ChengyuColums.CONTENT_URI,
				new String[] { ChengyuColums.NAME, ChengyuColums.PINYIN,
						ChengyuColums.COMMENT, ChengyuColums.ORIGINAL,
						ChengyuColums.EXAMPLE, ChengyuColums.ENGLISH,
						ChengyuColums.SIMILAR, ChengyuColums.OPPOSITE },
				"_id in (" + s + ")", null, null);
		ContentValues values=new ContentValues();
		values.put(HistoryColums.KIND, 0);
		values.put(HistoryColums.NAME, s);
		values.put(HistoryColums.TIME, MainActivity.getDate());
		
	}

	private void queryChengyu(final String string) {
		if (string != null) {
			if (mChengyuQuery == null)
				mChengyuQuery = new ChengyuQuery(getContentResolver());
			mChengyuQuery.cancelOperation(QUERY_TOKEN);
			mChengyuQuery.startQuery(QUERY_TOKEN, null,
					ChengyuColums.CONTENT_URI, new String[] {
							ChengyuColums.NAME, ChengyuColums.PINYIN,
							ChengyuColums.COMMENT, ChengyuColums.ORIGINAL,
							ChengyuColums.EXAMPLE, ChengyuColums.ENGLISH,
							ChengyuColums.SIMILAR, ChengyuColums.OPPOSITE },
					"name like '%" + string + "%'", null, null);
		}
	}

	private class ChengyuQuery extends AsyncQueryHandler {

		public ChengyuQuery(ContentResolver cr) {
			super(cr);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			// TODO Auto-generated method stub
			super.onQueryComplete(token, cookie, cursor);
			List<Map<String, String>> tempList = new ArrayList<Map<String, String>>();
			if (cursor != null) {
				while (cursor.moveToNext()) {
					Map<String, String> map = new HashMap<String, String>();
					map.put(ChengyuColums.NAME, cursor.getString(0));
					map.put(ChengyuColums.PINYIN, cursor.getString(1));
					map.put(ChengyuColums.COMMENT, cursor.getString(2));
					map.put(ChengyuColums.ORIGINAL, cursor.getString(3));
					map.put(ChengyuColums.EXAMPLE, cursor.getString(4));
					map.put(ChengyuColums.ENGLISH, cursor.getString(5));
					map.put(ChengyuColums.SIMILAR, cursor.getString(6));
					map.put(ChengyuColums.OPPOSITE, cursor.getString(7));
					tempList.add(map);
				}
				cursor.close();
			}
			if (mSourceList == null)
				mSourceList = new ArrayList<Map<String, String>>();
			mSourceList.clear();
			mSourceList.addAll(tempList);
			mListAdapter.notifyDataSetChanged();
		}

	}

	private class ChengyuItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Map<String, String> map = mSourceList.get(arg2);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					StudyActivity.this);
			builder.setTitle(map.get(ChengyuColums.NAME));
			builder.setMessage("※拼音 ： " + map.get(ChengyuColums.PINYIN) + "\n"
					+ "※释义： " + map.get(ChengyuColums.COMMENT));
			builder.setPositiveButton("确定", null);
			builder.create().show();
		}

	}
}
