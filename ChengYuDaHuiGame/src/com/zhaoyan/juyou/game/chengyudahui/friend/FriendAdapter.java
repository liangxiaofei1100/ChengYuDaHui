package com.zhaoyan.juyou.game.chengyudahui.friend;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.communication.ipc.aidl.User;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.view.RoundedImageView;

public class FriendAdapter extends BaseAdapter{
	private static final String TAG = FriendAdapter.class.getSimpleName();
	
	private LayoutInflater mInflater = null;
	private List<User> mData = null;
	
	private CardOnClickListener mCardOnClickListener = new CardOnClickListener();
	private ExpandViewOnClickListener mExpandViewOnClickListener = new ExpandViewOnClickListener();
	
	private int mClickPosition = -1;
	

	public FriendAdapter(Context context, List<User> mData) {
		mInflater = LayoutInflater.from(context);
		this.mData = mData;
	}
	
	private void setClickPosition(int position){
		mClickPosition = position;
		notifyDataSetChanged();
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
			holder.cardView = view.findViewById(R.id.friend_rl_card);
			holder.expandView = view.findViewById(R.id.friend_expand_view);
			holder.iconView = (RoundedImageView) view.findViewById(R.id.head_view);
			holder.usernameView = (TextView) view.findViewById(R.id.username_view);
			holder.infoView = (TextView) view.findViewById(R.id.info_view);
			
			holder.imageView1 = (ImageView) view.findViewById(R.id.friend_btn_1);
			holder.imageView2 = (ImageView) view.findViewById(R.id.friend_btn_2);
			holder.imageView3 = (ImageView) view.findViewById(R.id.friend_btn_3);
			
			holder.imageView1.setTag(new ExpandViewData(0));
			holder.imageView2.setTag(new ExpandViewData(1));
			holder.imageView3.setTag(new ExpandViewData(2));
			
			holder.cardView.setOnClickListener(mCardOnClickListener);
			holder.imageView1.setOnClickListener(mExpandViewOnClickListener);
			holder.imageView2.setOnClickListener(mExpandViewOnClickListener);
			holder.imageView3.setOnClickListener(mExpandViewOnClickListener);
			
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		CardData data = new CardData(position);
		holder.cardView.setTag(data);
		
		if (position % 2 == 0) {
			holder.cardView.setBackgroundResource(R.drawable.friend_list_bg2);
		} else {
			holder.cardView.setBackgroundResource(R.drawable.friend_list_bg1);
		}
		
		if (position == mClickPosition) {
			int visibility = holder.expandView.getVisibility();
			if (View.VISIBLE == visibility) {
				holder.expandView.setVisibility(View.GONE);
			} else {
				holder.expandView.setVisibility(View.VISIBLE);
			}
		} else {
			holder.expandView.setVisibility(View.GONE);
		}
		User user = mData.get(position);
		
		holder.iconView.setImageResource(R.drawable.avantar_test);
		holder.usernameView.setText(user.getUserName());
		holder.infoView.setText("用户列表测试:" + position);
		
		return view;
	}
	
	private class CardOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			CardData cardData = (CardData) v.getTag();
			int position = cardData.position;
			setClickPosition(position);
		}
	}
	
	private class ExpandViewOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ExpandViewData data = (ExpandViewData) v.getTag();
			System.out.println("ExpandOnClick:" + data.position);
		}
	}
	
	class CardData {
		int position;

		public CardData(int position) {
			this.position = position;
		}
	}
	
	class ExpandViewData {
		int position;

		public ExpandViewData(int position) {
			this.position = position;
		}
	}
	
	private class ViewHolder{
		View cardView;
		View expandView;
		RoundedImageView iconView;
		TextView usernameView;
		TextView infoView;
		ImageView imageView1;
		ImageView imageView2;
		ImageView imageView3;
	}

}
