package com.zhaoyan.juyou.game.chengyudahui.dictate;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zhaoyan.juyou.game.chengyudahui.R;
import com.zhaoyan.juyou.game.chengyudahui.utils.Utils;

public class StoryItemAdapter extends BaseAdapter implements SectionIndexer{
	private List<StoryInfo> list = null;
	private Context mContext;
	
	private int mSelectPosition = -1;
	
	public StoryItemAdapter(Context mContext, List<StoryInfo> list) {
		this.mContext = mContext;
		this.list = list;
	}
	
	public void setSelect(int position){
		mSelectPosition = position;
	}
	
	public void updateListView(List<StoryInfo> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final StoryInfo mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.story_list_item, null);
			viewHolder.headView = view.findViewById(R.id.story_rl_header);
			viewHolder.letterView = (TextView) view.findViewById(R.id.story_tv_catalog);
			viewHolder.titleView = (TextView) view.findViewById(R.id.story_item_tv_title);
			viewHolder.sizeView = (TextView) view.findViewById(R.id.story_item_size);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			viewHolder.headView.setVisibility(View.VISIBLE);
			viewHolder.letterView.setText(mContent.getSortLetter());
		}else{
			viewHolder.headView.setVisibility(View.GONE);
		}
	
		StoryInfo info = list.get(position);
		viewHolder.titleView.setText(info.getTitle());
		viewHolder.sizeView.setText(Utils.getFormatSize(info.getSize()));
		
		if (info.getLocalPath() != null && mSelectPosition == position) {
			viewHolder.titleView.setTextColor(Color.BLUE);
		} else {
			viewHolder.titleView.setTextColor(Color.BLACK);
		}
		
		return view;

	}
	
	class ViewHolder {
		View headView;
		TextView letterView;
		ImageView imageview;
		// Button button;
		TextView titleView;
		TextView sizeView;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetter().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetter();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}