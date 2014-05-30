package com.zhaoyan.juyou.game.chengyu;

public class ChengYu {
	String name = "";
	String pinyin = "";
	String comment = "";
	String original = "";
	String example = "";
	String english = "";
	String opposite = "";
	String similar = "";
	String story = "";

	public ChengYu(String name, String pinyin, String comment, String original,
			String example, String english, String opposite, String similar,
			String story) {
		this.name = name;
		this.pinyin = pinyin;
		this.comment = comment;
		this.original = original;
		this.example = example;
		this.english = english;
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
				+ ", english=" + english + ", opposite=" + opposite
				+ ", similar=" + similar + ", story=" + story + "]";
	}

}