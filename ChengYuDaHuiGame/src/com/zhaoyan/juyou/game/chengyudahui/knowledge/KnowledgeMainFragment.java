package com.zhaoyan.juyou.game.chengyudahui.knowledge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.DBConfig;
import com.zhaoyan.juyou.game.chengyudahui.MainActivity;
import com.zhaoyan.juyou.game.chengyudahui.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class KnowledgeMainFragment extends Fragment implements OnItemClickListener {
	private static final String TAG = KnowledgeMainFragment.class.getSimpleName();
	
	private ListView mListView;
	private List<String> mList = new ArrayList<String>();
	
	private KnowledgeMainActivity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onAttach:" + activity);
		mActivity = (KnowledgeMainActivity) activity;
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
		mActivity.setTitle("知识闯关");
		// test
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
		// test

		MyAdapter myAdapter = new MyAdapter();
		mListView.setAdapter(myAdapter);
		mListView.setOnItemClickListener(this);
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//每一回的配置文件以固定格式命名：knowledge1.xml,1表示第一回
		String path =DBConfig.FILES_DIR + "/knowledge" + (position + 1) + ".xml";
		File file = new File(path);
		if (!file.exists()) {
			Log.e(TAG, file.getAbsolutePath() + " is not exist");
			showToast("该关卡尚未解锁");
			return;
		}
//		Intent intent = new Intent();
//		intent.setClass(getActivity(), KnowledgeItemActivity.class);
//		intent.putExtra("path", path);
//		startActivity(intent);
		mActivity.selectItem(1);
		Log.d(TAG, "filepath:" + file.getAbsolutePath());
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
			if (position == 0) {
				imageView.setVisibility(View.INVISIBLE);
			}
			
			TextView stageView = (TextView) view.findViewById(R.id.tv_knowledge_stage);
			stageView.setText("第" + (position + 1) + "回");
			return view;
		}
		
	}

}
