package com.zhaoyan.juyou.game.chengyudahui.study.story;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomIndicator extends TextView {
	
	private int mCount = 0;
	public int getCount() {
		return mCount;
	}

	private int mCurrentPosition = 0;
	
	public CustomIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomIndicator(Context context) {
		super(context);
	}
	
	public void setCurrentPosition(int pos) {
		mCurrentPosition = pos;
		if(mCurrentPosition < 0) {
			mCurrentPosition = 0;
		}
		if(mCurrentPosition > mCount-1) {
			mCurrentPosition = mCount-1;
		}
		
		setText((mCurrentPosition + 1) + "/" + mCount);
	}
	
	public void next() {
		setCurrentPosition(mCurrentPosition+1);
	}
	
	public void previous() {
		setCurrentPosition(mCurrentPosition-1);
	}
	
	public void setCount(int count) {
		this.mCount = count;
		this.mCurrentPosition = 0;
		initViews();
	}
	
	private void initViews() {
		setCurrentPosition(0);
	}
	
}
