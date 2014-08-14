package com.zhaoyan.juyou.game.chengyudahui.knowledge;

public class Word {

	private int position;
	private String word;
	private boolean visibile;
	private boolean tip;
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
	public String getWord() {
		return word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public boolean isVisibile() {
		return visibile;
	}
	
	public void setVisibile(boolean visibile) {
		this.visibile = visibile;
	}
	
	public boolean isTip() {
		return tip;
	}
	
	public void setTip(boolean tip) {
		this.tip = tip;
	}

	
}
