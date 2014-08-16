package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;

public class StoryListAdapter extends BaseAdapter {
	private static final String TAG = StoryListAdapter.class.getSimpleName();
	
	private LayoutInflater mInflater;
	public List<StoryInfo> mDataList = null;


	public StoryListAdapter(Context con, List<StoryInfo> dataList) {
		mInflater = LayoutInflater.from(con);
		this.mDataList = dataList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataList.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.story_list_item,
					parent, false);
			holder = new ViewHolder();
			holder.textView = (TextView) convertView.findViewById(R.id.story_item_tv_title);
			holder.imageview = (ImageView) convertView.findViewById(R.id.story_item_iv_icon);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String title = mDataList.get(pos).getTitle();
		Log.d(TAG, "getView.title:" + title);
		holder.textView.setText(title);

		return convertView;
	}

	 class ViewHolder {
		ImageView imageview;
		TextView textView;
	}

	
}
