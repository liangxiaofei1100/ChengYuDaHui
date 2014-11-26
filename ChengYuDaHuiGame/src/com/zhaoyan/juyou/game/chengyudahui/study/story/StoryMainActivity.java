package com.zhaoyan.juyou.game.chengyudahui.study.story;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.BaseActivity;
import com.zhaoyan.juyou.game.chengyudahui.activity.BaseZyActivity;
import com.zhaoyan.juyou.game.chengyudahui.db.StoryData.TypeColums;
import com.zhaoyan.juyou.game.chengyudahui.view.ActionBar;

public class StoryMainActivity extends BaseZyActivity implements OnItemClickListener {
	private static final String TAG = StoryMainActivity.class.getSimpleName();
	
	private ListView mListView;
	private List<StoryItem> mList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowledge_fragment_main);
		
		ActionBar actionBar = getZyActionBar();
		actionBar.setActionHomeAsUpEnable(true);
		
		Intent intent = getIntent();
		if(intent != null){
			actionBar.setTitle(intent.getStringExtra("title"));
		}
		
		mListView = (ListView) findViewById(R.id.lv_knowledge);
		mList = new ArrayList<StoryItem>();
		
		Cursor cursor  = getContentResolver().query(TypeColums.CONTENT_URI, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			int typeId;
			String typeName;
			String folder;
			
			StoryItem item = null;
			do {
				typeId = cursor.getInt(cursor.getColumnIndex(TypeColums.TYPE));
				typeName = cursor.getString(cursor.getColumnIndex(TypeColums.NAME));
				folder = cursor.getString(cursor.getColumnIndex(TypeColums.FOLDER));
				
				item = new StoryItem(typeId, typeName, folder);
				mList.add(item);
			} while (cursor.moveToNext());
			cursor.close();
		} 

		MyAdapter myAdapter = new MyAdapter();
		mListView.setAdapter(myAdapter);
		mListView.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		StoryItem item = mList.get(position);
		Intent intent = new Intent();
		intent.putExtra("storyItem", item);
		intent.setClass(this, StoryItemActivity.class);
		startActivity(intent);
	}
	
	private class MyAdapter extends BaseAdapter{
		
		LayoutInflater inflater = null;
		
		public MyAdapter(){
			inflater =  LayoutInflater.from(StoryMainActivity.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
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
			View view = inflater.inflate(R.layout.knowledge_list_item, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.iv_stage_lock);
			imageView.setVisibility(View.INVISIBLE);
			
			TextView stageView = (TextView) view.findViewById(R.id.tv_knowledge_stage);
			stageView.setVisibility(View.GONE);
			
			TextView titleView = (TextView) view.findViewById(R.id.tv_knowledge_title);
			titleView.setText(mList.get(position).getTypeName());
			return view;
		}
		
	}

}
