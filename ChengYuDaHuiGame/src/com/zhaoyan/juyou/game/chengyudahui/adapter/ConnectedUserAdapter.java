package com.zhaoyan.juyou.game.chengyudahui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.communication.UserHelper;
import com.zhaoyan.communication.UserInfo;
import com.zhaoyan.communication.provider.ZhaoYanCommunicationData;
import com.zhaoyan.communication.util.BitmapUtilities;
import com.zhaoyan.juyou.game.chengyudahui.R;

/**
 * Adapter for network fragment user list view.
 * 
 */
public class ConnectedUserAdapter extends CursorAdapter {
	private LayoutInflater mLayoutInflater;

	public ConnectedUserAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder != null) {
			// name
			String name = cursor.getString(cursor
					.getColumnIndex(ZhaoYanCommunicationData.User.USER_NAME));
			holder.userName.setText(name);
			// head
			int headId = cursor.getInt(cursor
					.getColumnIndex(ZhaoYanCommunicationData.User.HEAD_ID));
			if (headId == UserInfo.HEAD_ID_NOT_PRE_INSTALL) {
				byte[] headData = cursor.getBlob(cursor
						.getColumnIndex(ZhaoYanCommunicationData.User.HEAD_DATA));
				if (headData.length == 0) {
					holder.userIcon.setImageResource(R.drawable.head_unkown);
				} else {
					Bitmap headBitmap = BitmapUtilities
							.byteArrayToBitmap(headData);
					holder.userIcon.setImageBitmap(headBitmap);
				}
			} else {
				holder.userIcon.setImageResource(UserHelper
						.getHeadImageResource(headId));
			}

			// signature
			String signature = cursor.getString(cursor.getColumnIndex(ZhaoYanCommunicationData.User.SIGNATURE));
			holder.userSignature.setText(signature);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		View view = mLayoutInflater.inflate(R.layout.connected_info_item, null);
		ViewHolder holder = new ViewHolder();

		holder.userIcon = (ImageView) view.findViewById(R.id.iv_cii_user_icon);
		holder.userName = (TextView) view.findViewById(R.id.tv_cii_user_name);
		holder.userSignature = (TextView) view.findViewById(R.id.tv_cii_user_signature);
		view.setTag(holder);
		return view;
	}

	private class ViewHolder {
		ImageView userIcon;
		TextView userName;
		TextView userSignature;
	}
}