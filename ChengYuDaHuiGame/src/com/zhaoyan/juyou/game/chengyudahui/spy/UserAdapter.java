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

import com.zhaoyan.communication.ipc.aidl.User;
import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.R;

public class UserAdapter extends BaseAdapter {
	private static final String TAG = UserAdapter.class.getSimpleName();
	private LayoutInflater mInflater;
	private List<User> userList = new ArrayList<User>();
	
	private Context mContext;
	
	public UserAdapter(Context context, List<User> userList){
		mInflater = LayoutInflater.from(context);
		this.userList = userList;
		mContext = context;
	}
	
	public void setData(List<User> list){
		userList = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userList.size();
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
		View view = mInflater.inflate(R.layout.spy_server_list_item, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.iv_sli_user_icon);
		TextView nameView = (TextView) view.findViewById(R.id.tv_sli_user_name);
		TextView tipView = (TextView) view.findViewById(R.id.tv_sli_user_tip);
		
		User user = userList.get(position);
		Log.d(TAG, "getView.name="  + user.getUserName());
		imageView.setImageBitmap(getUserIcon(user.getUserID()));
		nameView.setText(user.getUserName());
		
		return view;
	}
	
	private Bitmap getUserIcon(int userId){
		Log.d(TAG, "getUserIcon.userId=" + userId);
		Uri uri = Uri.parse("content://com.zhaoyan.juyou.provider.JuyouProvider/user");
		String selection = "user_id" + "=?";
		String[] selectionArgs = {userId + ""};
		Cursor cursor = mContext.getContentResolver().query(uri, new String[]{"name","head"}, selection, selectionArgs, null);
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
