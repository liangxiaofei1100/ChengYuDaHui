package com.zhaoyan.juyou.game.chengyudahui.knowledge;

import java.util.HashMap;
import java.util.Map;

public class PinYinUtil {
	public static final String PINYIN_SEPERATOR = "[ ，]";
	private static Map<Character, Character> PIN_YIN_MAP = new HashMap<Character, Character>();

	static {
		// a
		PIN_YIN_MAP.put('ā', 'a');
		PIN_YIN_MAP.put('á', 'a');
		PIN_YIN_MAP.put('ǎ', 'a');
		PIN_YIN_MAP.put('à', 'a');
		// e
		PIN_YIN_MAP.put('ē', 'e');
		PIN_YIN_MAP.put('é', 'e');
		PIN_YIN_MAP.put('ě', 'e');
		PIN_YIN_MAP.put('è', 'e');
		// i
		PIN_YIN_MAP.put('ī', 'i');
		PIN_YIN_MAP.put('í', 'i');
		PIN_YIN_MAP.put('ǐ', 'i');
		PIN_YIN_MAP.put('ì', 'i');
		// o
		PIN_YIN_MAP.put('ō', 'o');
		PIN_YIN_MAP.put('ó', 'o');
		PIN_YIN_MAP.put('ǒ', 'o');
		PIN_YIN_MAP.put('ò', 'o');
		// u
		PIN_YIN_MAP.put('ū', 'u');
		PIN_YIN_MAP.put('ú', 'u');
		PIN_YIN_MAP.put('ǔ', 'u');
		PIN_YIN_MAP.put('ù', 'u');
		// v
		PIN_YIN_MAP.put('ǖ', 'v');
		PIN_YIN_MAP.put('ǘ', 'v');
		PIN_YIN_MAP.put('ǚ', 'v');
		PIN_YIN_MAP.put('ǜ', 'v');
		PIN_YIN_MAP.put('ü', 'v');
	}

	public static String removePinYinShengDiao(String pinyinWithShengDiao) {
		StringBuilder stringBuilder = new StringBuilder(
				pinyinWithShengDiao.length());

		for (int i = 0; i < pinyinWithShengDiao.length(); i++) {
			char c = pinyinWithShengDiao.charAt(i);

			if ((c >= 'a' && c <= 'z') || c == ' ' || c == '，') {
				// char in [a-z]
				stringBuilder.append(c);
			} else {
				// char is a PinYin with ShengDiao
				Character cRemovedShengDiao = PIN_YIN_MAP.get(c);
				if (cRemovedShengDiao != null) {
					stringBuilder.append(cRemovedShengDiao);
				} else {
					System.err.println("Unkown characer found: " + c + ", "
							+ pinyinWithShengDiao);
					return null;
				}
			}
		}
		return stringBuilder.toString();
	}
}
