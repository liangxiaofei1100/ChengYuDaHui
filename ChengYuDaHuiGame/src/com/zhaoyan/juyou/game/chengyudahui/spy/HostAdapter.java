package com.zhaoyan.juyou.game.chengyudahui.spy;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaoyan.communication.ipc.aidl.HostInfo;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;

public class HostAdapter extends BaseAdapter {
	private static final String TAG = HostAdapter.class.getSimpleName();
	private LayoutInflater mInflater;
	private List<HostInfo> mDataList = new ArrayList<HostInfo>();
	
	private Context mContext;
	
	public HostAdapter(Context context, List<HostInfo> list){
		mDataList = list;
		
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}
	
	public void setData(List<HostInfo> data){
		mDataList =data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mDataList.size();
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
		View view = mInflater.inflate(R.layout.server_list_item, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.iv_sli_user_icon);
		TextView nameView = (TextView) view.findViewById(R.id.tv_sli_user_name);
		TextView tipView = (TextView) view.findViewById(R.id.tv_sli_user_tip);
		
		HostInfo info = mDataList.get(position);
		Log.d(TAG, "getView.info.name=" + info.ownerName + ",ownerId=" + info.ownerID);
		imageView.setImageBitmap(getUserIcon());
		nameView.setText(info.ownerName);
		return view;
	}
	
	private Bitmap getUserIcon(){
		Log.d(TAG, "getUserIcon");
		Uri uri = Uri.parse("content://com.zhaoyan.juyou.provider.JuyouProvider/user");
		String selection = "user_id" + "=-1";
		Cursor cursor = mContext.getContentResolver().query(uri, new String[]{"name","head"}, selection, null, null);
		if (cursor == null || cursor.getCount() <= 0) {
			return null;
		} else {
			if (cursor.moveToFirst()) {
				byte[] headByte = cursor.getBlob(cursor.getColumnIndex("head"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				Log.d(TAG, "name=" + name);
				
				cursor.close();
				if (headByte.length == 0) {
					Drawable drawable = mContext.getResources().getDrawable(R.drawable.head_unkown);
					Bitmap bitmap = Bitmap
							.createBitmap(
									drawable.getIntrinsicWidth(),
									drawable.getIntrinsicHeight(),
									drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
											: Bitmap.Config.RGB_565);
					Canvas canvas = new Canvas(bitmap);
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight());
					drawable.draw(canvas);
					return bitmap;
				}
				return BitmapFactory.decodeByteArray(headByte, 0, headByte.length);
			}
		}
		return null;
	}

}
