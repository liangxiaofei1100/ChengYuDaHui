package com.zhaoyan.juyou.game.chengyudahui.study.write;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.DictateData.DictateColums;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemAdapter extends CursorAdapter {
	private static final String TAG = ItemAdapter.class.getSimpleName();

	public ItemAdapter(Context context, Cursor c) {
		super(context, c, false);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		// TODO Auto-generated method stub
		String result = cursor.getString(cursor
				.getColumnIndex(DictateColums.RESULT));
		ViewHolder holder = (ViewHolder) view.getTag();
		int position = cursor.getPosition();

		holder.tv.setText((position + 1) + "");

		Log.d(TAG, position + ":" + result);
		if (result != null) {
			if (result.equals("right")) {
				holder.imageView
						.setImageResource(R.drawable.write_result_right);
				return;
			} else if (result.equals("wrong")) {
				holder.imageView
						.setImageResource(R.drawable.write_result_wrong);
				return;
			}
		}
		holder.imageView.setImageResource(R.drawable.write_result_unknow);
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		View v;
		LayoutInflater layoutInflater = LayoutInflater.from(arg0);
		ViewHolder holder = new ViewHolder();
		v = layoutInflater.inflate(R.layout.dictate_ietm, null);
		holder.tv = (TextView) v.findViewById(R.id.tv_dictate_item);
		holder.imageView = (ImageView) v.findViewById(R.id.iv_dictate_icon);
		v.setTag(holder);
		return v;
	}

	public class ViewHolder {
		TextView tv;
		ImageView imageView;
	}
}
