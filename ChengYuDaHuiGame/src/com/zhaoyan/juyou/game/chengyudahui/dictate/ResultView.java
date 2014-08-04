package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.Map;

import com.zhaoyan.juyou.game.chengyudahui.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultView {
	private LayoutInflater mLayoutInflater;
	private View mView;

	public ResultView(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
	}

	public View getView(String s) {
		if (mView == null)
			mView = mLayoutInflater.inflate(R.layout.dictate_result_layout,
					null);
		if (s != null && s.length() > 0) {
			int i = 0;
			try {
				TextView t = (TextView) mView
						.findViewById(R.id.result_first_word);
				t.setText(s.charAt(i) + "");
				i++;
				t = (TextView) mView.findViewById(R.id.result_second_word);
				t.setText(s.charAt(i) + "");
				i++;
				t = (TextView) mView.findViewById(R.id.result_third_word);
				t.setText(s.charAt(i) + "");
				i++;
				t = (TextView) mView.findViewById(R.id.result_fourth_word);
				t.setText(s.charAt(i) + "");
			} catch (Exception e) {
				switch (i) {
				case 0:
					break;
				case 1:
					mView.findViewById(R.id.dictate_result_second_layout)
							.setVisibility(View.GONE);
					mView.findViewById(R.id.dictate_result_third_layout)
							.setVisibility(View.GONE);
					mView.findViewById(R.id.dictate_result_fourth_layout)
							.setVisibility(View.GONE);
					break;
				case 2:
					mView.findViewById(R.id.dictate_result_third_layout)
							.setVisibility(View.GONE);
					mView.findViewById(R.id.dictate_result_fourth_layout)
							.setVisibility(View.GONE);
					break;
				case 3:
					mView.findViewById(R.id.dictate_result_fourth_layout)
							.setVisibility(View.GONE);
					break;
				default:
					break;
				}
			}
		}
		return mView;
	}

	public void setImage(Map<Integer, Bitmap> map) {
		ImageView im = null;
		if (map != null && map.size() > 0) {
			for (java.util.Map.Entry<Integer, Bitmap> entry : map.entrySet()) {
				int i = entry.getKey();
				switch (i) {
				case 1:
					im = (ImageView) mView.findViewById(R.id.result_first_img);
					im.setImageBitmap(entry.getValue());
					break;
				case 2:
					im = (ImageView) mView.findViewById(R.id.result_second_img);
					im.setImageBitmap(entry.getValue());
					break;
				case 3:
					im = (ImageView) mView.findViewById(R.id.result_third_img);
					im.setImageBitmap(entry.getValue());
					break;
				case 4:
					im = (ImageView) mView.findViewById(R.id.result_fourth_img);
					im.setImageBitmap(entry.getValue());
					break;
				default:
					break;
				}
				if (im != null)
					im.setVisibility(View.VISIBLE);
			}
		} else {
			im = (ImageView) mView.findViewById(R.id.result_first_img);
			im.setImageResource(R.drawable.mizige1);
			im.setVisibility(View.INVISIBLE);
			im = (ImageView) mView.findViewById(R.id.result_second_img);
			im.setImageResource(R.drawable.mizige1);
			im.setVisibility(View.INVISIBLE);
			im = (ImageView) mView.findViewById(R.id.result_fourth_img);
			im.setImageResource(R.drawable.mizige1);
			im.setVisibility(View.INVISIBLE);
			im = (ImageView) mView.findViewById(R.id.result_third_img);
			im.setImageResource(R.drawable.mizige1);
			im.setVisibility(View.INVISIBLE);
		}

	}

}
