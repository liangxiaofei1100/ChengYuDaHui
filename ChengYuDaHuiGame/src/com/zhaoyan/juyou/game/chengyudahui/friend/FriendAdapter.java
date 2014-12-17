package com.zhaoyan.juyou.game.chengyudahui.friend;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhaoyan.communication.ipc.aidl.User;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.view.RoundedImageView;

public class FriendAdapter extends BaseAdapter{
	private static final String TAG = FriendAdapter.class.getSimpleName();
	
	private LayoutInflater mInflater = null;
	private List<User> mData = null;
	

	public FriendAdapter(Context context, List<User> mData) {
		mInflater = LayoutInflater.from(context);
		this.mData = mData;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public User getItem(int position) {
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
			view = mInflater.inflate(R.layout.fragment_friend_item, null);
			holder = new ViewHolder();
			holder.iconView = (RoundedImageView) view.findViewById(R.id.head_view);
			holder.usernameView = (TextView) view.findViewById(R.id.username_view);
			holder.infoView = (TextView) view.findViewById(R.id.info_view);
			
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
//		if (position % 2 == 0) {
//			view.setBackgroundResource(R.drawable.friend_list_bg2);
//		} else {
//			view.setBackgroundResource(R.drawable.friend_list_bg1);
//		}
//		
		User user = mData.get(position);
		
		holder.iconView.setImageResource(R.drawable.default_avatar);
		holder.usernameView.setText(user.getUserName());
		holder.infoView.setText("用户列表测试:" + position);
		
		return view;
	}
	
	
	private class ViewHolder{
		RoundedImageView iconView;
		TextView usernameView;
		TextView infoView;
	}

}
