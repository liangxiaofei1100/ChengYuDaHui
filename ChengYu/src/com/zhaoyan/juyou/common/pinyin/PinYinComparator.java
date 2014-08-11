package com.zhaoyan.juyou.common.pinyin;

import java.util.Comparator;

import com.zhaoyan.juyou.game.chengyu.ChengYu;

public class PinYinComparator implements Comparator<ChengYu> {

	@Override
	public int compare(ChengYu o1, ChengYu o2) {
		int result = 0;
		String pinYin1 = o1.pinyin;
		String pinYin2 = o2.pinyin;

		String[] pinYin1Subs = pinYin1.split(PinYinUtil.PINYIN_SEPERATOR);
		String[] pinYin2Subs = pinYin2.split(PinYinUtil.PINYIN_SEPERATOR);

		int n = 0;
		if (pinYin1Subs.length > pinYin2Subs.length) {
			n = pinYin2Subs.length;
		} else {
			n = pinYin1Subs.length;
		}

		for (int i = 0; i < n; i++) {
			System.out.println("n = " + n + ", i = " + i);
			int resultWithoutShengDiao = PinYinUtil.removePinYinShengDiao(
					pinYin1Subs[i]).compareTo(
					PinYinUtil.removePinYinShengDiao(pinYin2Subs[i]));
			if (resultWithoutShengDiao == 0) {
				// compare by ShengDiao
				int resultWithShengDiao = pinYin1Subs[i]
						.compareTo(pinYin2Subs[i]);
				if (resultWithShengDiao == 0) {
					continue;
				} else {
					// compare by ShengDiao is the result.
					result = resultWithShengDiao;
					break;
				}
			} else {
				// compare by PinYin without ShengDiao is the result.
				result = resultWithoutShengDiao;
				break;
			}
		}

		// handle length compare.
		if (result == 0) {
			if (pinYin1Subs.length > pinYin2Subs.length) {
				result = 1;
			} else if (pinYin1Subs.length == pinYin2Subs.length) {
				result = 0;
			} else {
				result = -1;
			}
		}

		return result;
	}
}
