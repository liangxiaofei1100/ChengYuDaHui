package com.zhaoyan.juyou.game.chengyudahui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.bean.ListItemInfo;
import com.zhaoyan.juyou.game.chengyudahui.view.RoundedImageView;

public class ItemFragmentAdapter extends BaseAdapter {
	
	private List<ListItemInfo> mData;
	private LayoutInflater mInflater = null;
	
	public ItemFragmentAdapter(Context context, List<ListItemInfo> list){
		mInflater = LayoutInflater.from(context);
		
		mData = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public ListItemInfo getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder holder = null;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.fragment_list_item2, null);
			holder = new ViewHolder();
			holder.imageView = (RoundedImageView) view.findViewById(R.id.head_view);
			holder.titleView = (TextView) view.findViewById(R.id.username_view);
			holder.summaryView = (TextView) view.findViewById(R.id.info_view);
			
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		ListItemInfo itemInfo = mData.get(position);
		holder.imageView.setImageResource(itemInfo.getIconId());
		holder.titleView.setText(itemInfo.getTitle());
		holder.summaryView.setText(itemInfo.getSummary());
		
		return view;
	}
	
	private class ViewHolder{
		RoundedImageView imageView;
		TextView titleView;
		TextView summaryView;
	}

}
