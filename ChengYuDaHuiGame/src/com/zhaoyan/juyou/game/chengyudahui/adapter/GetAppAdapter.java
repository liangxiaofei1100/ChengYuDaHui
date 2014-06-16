package com.zhaoyan.juyou.game.chengyudahui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.angel.devil.view.AsyncImageView;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.frontia.AppInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GetAppAdapter extends BaseAdapter {
	private static final String TAG = "GetAppAdapter";
	private List<AppInfo> mDataList = new ArrayList<AppInfo>();
	private LayoutInflater mInflater;
	
	public GetAppAdapter(List<AppInfo> list, Context context){
		mDataList = list;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mDataList.size();
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
		View view = null;
		ViewHolder holder = null;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.get_app_list_item, null);
			holder = new ViewHolder();
			holder.imageView = (AsyncImageView) view.findViewById(R.id.file_img);
			holder.nameView = (TextView) view.findViewById(R.id.file_name);
			holder.infoView = (TextView) view.findViewById(R.id.file_info);
			
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		Log.d(TAG, "getView:mDataList.size():" + mDataList.size());
		AppInfo appInfo = mDataList.get(position);
		holder.nameView.setText(appInfo.getAppLabel());
		holder.infoView.setText("Version:" + appInfo.getAppVersion() + ",Size:" + appInfo.getSize());
		holder.imageView.setDefaultImageResource(R.drawable.ic_launcher);
		holder.imageView.setPath(appInfo.getIconUrl());
		
		return view;
	}
	
	private class ViewHolder{
		AsyncImageView imageView;
		TextView nameView;
		TextView infoView;
	}

}
