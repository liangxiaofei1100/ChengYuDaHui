package com.zhaoyan.juyou.game.chengyudahui.knowledge;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class GuessGameOfPictureActivity extends Activity implements OnItemClickListener{
	private static final String TAG = GuessGameOfPictureActivity.class.getSimpleName();
	
	private LinearLayout mCyIconLL;
	private ImageView mCyIconIV;
	private GridView mGridView;
	
	private MyAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_guess_main);
		
//		DisplayMetrics dm = new DisplayMetrics();getWindowManager().getDefaultDisplay().getMetrics(dm);
//		int width = dm.widthPixels;//宽度
//		int height = dm.heightPixels ;//高度
//		
//		Log.d(TAG, "width=" + width + ",height=" + height);
//		mCyIconLL = (LinearLayout) findViewById(R.id.ll_cy_icon);
//		int llheight = (int) (height * 0.45);
//		Log.d(TAG, "llheight=" + llheight);
//		LinearLayout.LayoutParams ll_params = new LayoutParams(width, llheight);
//		ll_params.setMargins(0, 20, 0, 20);
//		mCyIconLL.setLayoutParams(ll_params);
//		mCyIconIV = (ImageView) findViewById(R.id.iv_cy_icon);
//		
//		int ivHeight = (int)(llheight * 0.8);
//		Log.d(TAG, "ivHeight=" + ivHeight);
//		ViewGroup.LayoutParams ivParams = mCyIconIV.getLayoutParams();
//		ivParams.height = ivHeight;
//		ivParams.width = ivHeight;
//		mCyIconIV.setLayoutParams(ivParams);
//		Log.d(TAG, "after.height=" + ivParams.height + ",width=" + ivParams.width);
		
		mGridView = (GridView) findViewById(R.id.gv_cy_word);
		mAdapter = new MyAdapter(getApplicationContext());
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
	}
	
	class MyAdapter extends BaseAdapter{
		private LayoutInflater mInflater = null;
		
		public MyAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 24;
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
			View view = mInflater.inflate(R.layout.picture_guess_item, null);
			ImageView wordBtn = (ImageView) view.findViewById(R.id.iv_cy_word_icon);
			if (position == 6) {
				wordBtn.setBackgroundResource(0);
//				wordBtn.setText("");
			}
			return view;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onItemClick");
	}
}
