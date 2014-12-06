package com.zhaoyan.juyou.game.chengyudahui.study.write;

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

	public ItemAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View view, Context arg1, Cursor arg2) {
		// TODO Auto-generated method stub
		String s = arg2.getString(arg2.getColumnIndex(DictateColums.RESULT));
		ViewHolder holder=(ViewHolder) view.getTag();
		if(holder!=null)
			holder.tv.setText((arg2.getPosition()+1)+"");
		if (s != null) {
			if (s.equals("right")) {
				holder.imageView.setImageResource(R.drawable.write_result_right);
				return;
			} else if (s.equals("wrong")) {
				holder.imageView.setImageResource(R.drawable.write_result_wrong);
				return;
			}
		}
		holder.imageView.setImageResource(R.drawable.write_result_unknow);
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View v;
		LayoutInflater layoutInflater = LayoutInflater.from(arg0);
		ViewHolder holder=new ViewHolder();
		 v= layoutInflater.inflate(R.layout.dictate_ietm, null);
		 holder.tv=(TextView) v.findViewById(R.id.tv_dictate_item);
		 holder.imageView = (ImageView) v.findViewById(R.id.iv_dictate_icon);
		 v.setTag(holder);
		 return v;
	}

	public  class ViewHolder{
		TextView tv;
		ImageView imageView;
	}
}
