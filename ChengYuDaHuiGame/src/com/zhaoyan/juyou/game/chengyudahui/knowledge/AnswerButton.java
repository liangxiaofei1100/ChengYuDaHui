package com.zhaoyan.juyou.game.chengyudahui.knowledge;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class AnswerButton extends Button {
	
	private Word mWord;

	public AnswerButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AnswerButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public AnswerButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public void setWord(Word word){
		mWord = word;
		if (word == null) {
			this.setText("");
			return;
		}
		this.setText(word.getWord());
	}
	
	public Word getWord(){
		return mWord;
	}

}
