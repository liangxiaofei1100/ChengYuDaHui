package com.zhaoyan.juyou.game.chengyudahui.knowledge;

import java.util.List;

import com.zhaoyan.juyou.game.chengyudahui.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class JieLongAdapter extends BaseAdapter {
	private static final String TAG = JieLongAdapter.class.getSimpleName();

	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

	private List<JieLongMessageEntity> mMessageData;
	private Context mContext;
	private LayoutInflater mInflater;

	public JieLongAdapter(Context context, List<JieLongMessageEntity> data) {
		this.mContext = context;
		this.mMessageData = data;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return mMessageData.size();
	}

	public Object getItem(int position) {
		return mMessageData.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		JieLongMessageEntity entity = mMessageData.get(position);

		if (entity.isComeMessage) {
			return IMsgViewType.IMVT_COM_MSG;
		} else {
			return IMsgViewType.IMVT_TO_MSG;
		}

	}

	public int getViewTypeCount() {
		return 2;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		JieLongMessageEntity entity = mMessageData.get(position);
		boolean isComMsg = entity.isComeMessage;

		ViewHolder viewHolder = null;
		if (convertView == null) {
			if (isComMsg) {
				convertView = mInflater.inflate(R.layout.jielong_item_left,
						null);
			} else {
				convertView = mInflater.inflate(R.layout.jielong_item_right,
						null);
			}

			viewHolder = new ViewHolder();
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
			viewHolder.isComMsg = isComMsg;

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvContent.setText(entity.text);

		return convertView;
	}

	static class ViewHolder {
		public TextView tvContent;
		public boolean isComMsg = true;
	}

}
