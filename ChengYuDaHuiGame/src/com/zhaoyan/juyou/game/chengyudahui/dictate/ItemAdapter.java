package com.zhaoyan.juyou.game.chengyudahui.dictate;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.db.DictateData.DictateColums;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public class ItemAdapter extends CursorAdapter {

	public ItemAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		// TODO Auto-generated method stub
		String s = arg2.getString(arg2.getColumnIndex(DictateColums.RESULT));
		if (s != null) {
			if (s.equals("right")) {
				arg0.setBackgroundColor(Color.GREEN);
				return;
			} else if (s.equals("wrong")) {
				arg0.setBackgroundColor(Color.RED);
				return;
			}

		}
		arg0.setBackgroundColor(Color.GRAY);
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = LayoutInflater.from(arg0);
		return layoutInflater.inflate(R.layout.dictate_ietm, null);
	}

}
