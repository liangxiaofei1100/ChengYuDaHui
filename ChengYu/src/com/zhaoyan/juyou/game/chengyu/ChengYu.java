package com.zhaoyan.juyou.game.chengyu;

public class ChengYu {
	String name = "";
	String pinyin = "";
	String comment = "";
	String original = "";
	String example = "";
	int frequently = 0;
	String opposite = "";
	String similar = "";
	String story = "";

	public ChengYu(String name, String pinyin, String comment, String original,
			String example, int frequently, String opposite, String similar,
			String story) {
		this.name = name;
		this.pinyin = pinyin;
		this.comment = comment;
		this.original = original;
		this.example = example;
		this.frequently = frequently;
		this.opposite = opposite;
		this.similar = similar;
		this.story = story;
	}

	public ChengYu() {

	}

	@Override
	public String toString() {
		return "ChengYu [name=" + name + ", pinyin=" + pinyin + ", comment="
				+ comment + ", original=" + original + ", example=" + example
				+ ", frequently=" + frequently + ", opposite=" + opposite
				+ ", similar=" + similar + ", story=" + story + "]";
	}

}