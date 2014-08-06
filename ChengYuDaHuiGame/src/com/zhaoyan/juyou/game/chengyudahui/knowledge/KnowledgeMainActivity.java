package com.zhaoyan.juyou.game.chengyudahui.knowledge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.MainActivity;
import com.zhaoyan.juyou.game.chengyudahui.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class KnowledgeMainActivity extends Activity implements OnClickListener, OnItemClickListener {
	private static final String TAG = KnowledgeMainActivity.class.getSimpleName();
	
	private ListView mListView;
	private ImageView mBackView;
	
	private List<String> mList = new ArrayList<String>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowledge_main);
		
		mListView = (ListView) findViewById(R.id.lv_knowledge);
		mBackView = (ImageView) findViewById(R.id.iv_back);
		mBackView.setOnClickListener(this);
		
		//test
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		mList.add("豆腐西施");
		//test
		
		MyAdapter myAdapter = new MyAdapter();
		mListView.setAdapter(myAdapter);
		mListView.setOnItemClickListener(this);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	private class MyAdapter extends BaseAdapter{
		
		LayoutInflater inflater = null;
		
		public MyAdapter(){
			inflater = getLayoutInflater().from(getApplicationContext());
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
			return view;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		//每一回的配置文件以固定格式命名：knowledge1.xml,1表示第一回
		String path = MainActivity.FILES_DIR + "/knowledge" + (position + 1) + ".xml";
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		Intent intent = new Intent();
		intent.setClass(KnowledgeMainActivity.this, GuessGameOfPictureActivity.class);
		intent.putExtra("path", path);
		startActivity(intent);
		Log.d(TAG, "filepath:" + file.getAbsolutePath());
	}
}
