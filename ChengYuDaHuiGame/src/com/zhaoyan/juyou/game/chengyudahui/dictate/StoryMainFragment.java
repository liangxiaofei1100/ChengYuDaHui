package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.StoryData.TypeColums;

public class StoryMainFragment extends Fragment implements OnItemClickListener {
	private static final String TAG = StoryMainFragment.class.getSimpleName();
	
	private ListView mListView;
	private List<StoryItem> mList = null;
	
	private DictateMainFragmentActivity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onAttach:" + activity);
		mActivity = (DictateMainFragmentActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView()");
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.knowledge_fragment_main, null);
		
		mListView = (ListView) rootView.findViewById(R.id.lv_knowledge);
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "onActivityCreated");
		mActivity.setTitle("天天听故事");
		
		//get items data
		if (mList == null) {
			mList = new ArrayList<StoryItem>();
		}
		
		Cursor cursor  = getActivity().getContentResolver().query(TypeColums.CONTENT_URI, null, null, null, null);
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
		intent.setClass(getActivity(), StoryItemActivity.class);
		startActivity(intent);
	}
	
	private Toast mToast = null;
	private void showToast(String message){
		if (mToast != null) {
			mToast.cancel();
		}
		
		mToast = Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT);
		mToast.show();
	}
	
	private class MyAdapter extends BaseAdapter{
		
		LayoutInflater inflater = null;
		
		public MyAdapter(){
			inflater =  LayoutInflater.from(getActivity());
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
