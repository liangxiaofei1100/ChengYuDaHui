package com.zhaoyan.juyou.game.chengyudahui.study;

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
			view = mInflater.inflate(R.layout.fragment_list_item, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) view.findViewById(R.id.list_item_icon);
			holder.titleView = (TextView) view.findViewById(R.id.list_item_title);
			holder.summaryView = (TextView) view.findViewById(R.id.list_item_summary);
			
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
		ImageView imageView;
		TextView titleView;
		TextView summaryView;
	}

}
