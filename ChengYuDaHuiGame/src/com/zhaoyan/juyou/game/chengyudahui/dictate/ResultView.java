package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.juyou.game.chengyudahui.R;

public class ResultView {
	private LayoutInflater mLayoutInflater;
	private View mView;

	public ResultView(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
	}

	public View getView(String s, List<Integer> index) {
		if (mView == null)
			mView = mLayoutInflater.inflate(R.layout.dictate_result_layout,
					null);
		if (s != null && s.length() > 0) {
			int i = 0;
			try {
				TextView t = (TextView) mView
						.findViewById(R.id.result_first_word);
				t.setText(s.charAt(i) + "");
				t.setVisibility(View.VISIBLE);
				i++;
				t = (TextView) mView.findViewById(R.id.result_second_word);
				t.setText(s.charAt(i) + "");
				t.setVisibility(View.VISIBLE);
				mView.findViewById(R.id.dictate_result_second_layout)
						.setVisibility(View.VISIBLE);
				i++;
				t = (TextView) mView.findViewById(R.id.result_third_word);
				t.setText(s.charAt(i) + "");
				t.setVisibility(View.VISIBLE);
				mView.findViewById(R.id.dictate_result_third_layout)
						.setVisibility(View.VISIBLE);
				i++;
				t = (TextView) mView.findViewById(R.id.result_fourth_word);
				t.setText(s.charAt(i) + "");
				t.setVisibility(View.VISIBLE);
				mView.findViewById(R.id.dictate_result_fourth_layout)
						.setVisibility(View.VISIBLE);
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
		if (index != null && index.size() > 0) {
			ImageView im = null;
			for (int j : index) {
				switch (j) {
				case 0:
					im = (ImageView) mView.findViewById(R.id.result_first_img);
					break;
				case 1:
					im = (ImageView) mView.findViewById(R.id.result_second_img);
					break;
				case 2:
					im = (ImageView) mView.findViewById(R.id.result_third_img);
					break;
				case 3:
					im = (ImageView) mView.findViewById(R.id.result_fourth_img);
					break;
				default:
					break;
				}
				if (im != null) {
					im.setImageResource(R.drawable.mizige1);
					im.setVisibility(View.VISIBLE);
				}
			}
		}
		return mView;
	}

	public void setImage(Map<Integer, Bitmap> map, List<Integer> index) {
		ImageView im = null;
		if (index != null) {
			for (int i : index) {
				i += 1;
				switch (i) {
				case 1:
					im = (ImageView) mView.findViewById(R.id.result_first_img);
					break;
				case 2:
					im = (ImageView) mView.findViewById(R.id.result_second_img);
					break;
				case 3:
					im = (ImageView) mView.findViewById(R.id.result_third_img);
					break;
				case 4:
					im = (ImageView) mView.findViewById(R.id.result_fourth_img);
					break;
				default:
					break;
				}
				if (im != null) {
					if (map != null) {
						Bitmap bt = map.get(i);
						if (bt != null) {
							im.setImageBitmap(bt);
						} else {
							im.setImageResource(R.drawable.mizige1);
						}
					} else {
						im.setImageResource(R.drawable.mizige1);
					}
					im.setVisibility(View.VISIBLE);
				}
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
