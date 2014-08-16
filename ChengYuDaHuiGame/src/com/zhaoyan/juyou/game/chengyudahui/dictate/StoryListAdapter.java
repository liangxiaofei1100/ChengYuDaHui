package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.juyou.game.chengyudahui.R;

public class StoryListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public List<String> mDataList = null;


	public StoryListAdapter(Context con, List<String> dataList) {
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

		holder.textView.setText(mDataList.get(pos));

		return convertView;
	}

	 class ViewHolder {
		ImageView imageview;
		TextView textView;
	}

	
}
