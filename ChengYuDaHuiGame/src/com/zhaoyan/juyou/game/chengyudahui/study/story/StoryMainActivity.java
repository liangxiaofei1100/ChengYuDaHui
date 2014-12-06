package com.zhaoyan.juyou.game.chengyudahui.study.story;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.activity.BaseZyActivity;
import com.zhaoyan.juyou.game.chengyudahui.db.StoryData.TypeColums;
import com.zhaoyan.juyou.game.chengyudahui.view.ActionBar;

public class StoryMainActivity extends BaseZyActivity implements OnItemClickListener{
	
	private List<StoryItem> mList = new ArrayList<StoryItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_main);
		
		final ActionBar actionBar = getZyActionBar();
		actionBar.setActionHomeAsUpEnable(true);
		
		Intent intent = getIntent();
		if (intent != null) {
			actionBar.setTitle(intent.getStringExtra("title"));
		}
		
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
		
		ListView listView = (ListView) findViewById(R.id.lv_write);
		SampleListAdapter adapter = new SampleListAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		StoryItem item = mList.get(position);
		Intent intent = new Intent();
		intent.putExtra("storyItem", item);
		intent.setClass(this, StoryItemActivity.class);
		startActivity(intent);
	}
	
	private class SampleListAdapter extends ArrayAdapter<StoryItem> {

        public SampleListAdapter(Context context) {
        	super(context, 0, mList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	String name = getItem(position).getTypeName();
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_write_item, parent, false);
            }

            TextView letterImageView = (TextView) convertView.findViewById(R.id.tv_write_num);
//            letterImageView.setOval(true);
            TextView textView = (TextView) convertView.findViewById(R.id.tv_item_title);
            letterImageView.setText(((position + 1) + "").charAt(0) + "");
//            letterImageView.setLetter(((position + 1) + "").charAt(0));
            textView.setText(name);

            return convertView;
        }
        
    }
}
