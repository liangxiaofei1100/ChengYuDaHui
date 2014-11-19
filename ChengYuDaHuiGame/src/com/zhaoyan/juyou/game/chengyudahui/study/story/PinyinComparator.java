package com.zhaoyan.juyou.game.chengyudahui.study.story;

import java.util.Comparator;


public class PinyinComparator implements Comparator<StoryInfo> {

	public int compare(StoryInfo o1, StoryInfo o2) {
		if (o1.getSortLetter().equals("@")
				|| o2.getSortLetter().equals("#")) {
			return -1;
		} else if (o1.getSortLetter().equals("#")
				|| o2.getSortLetter().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetter().compareTo(o2.getSortLetter());
		}
	}

}
