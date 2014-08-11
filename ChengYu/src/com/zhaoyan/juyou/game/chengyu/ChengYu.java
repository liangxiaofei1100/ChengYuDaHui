package com.zhaoyan.juyou.game.chengyu;

public class ChengYu {
	public String name = "";
	public String pinyin = "";
	public String comment = "";
	public String original = "";
	public String example = "";
	public int frequently = 0;
	public String opposite = "";
	public String similar = "";
	public String story = "";
	public int caici = 0;

	public ChengYu(String name, String pinyin, String comment, String original,
			String example, int frequently, String opposite, String similar,
			String story, int isCaici) {
		this.name = name;
		this.pinyin = pinyin;
		this.comment = comment;
		this.original = original;
		this.example = example;
		this.frequently = frequently;
		this.opposite = opposite;
		this.similar = similar;
		this.story = story;
		this.caici = isCaici;
	}

	public ChengYu() {

	}

}