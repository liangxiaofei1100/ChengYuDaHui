package com.zhaoyan.juyou.game.chengyudahui.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class DictateData {
	public static final String CHENGYUDBNAME = "dictate.db";
	public static final int DICTATEVERSION = 1;
	public static final String AUTHORITY = "com.zhaoyan.juyou.game.chengyudahui.db.dictateprovider";

	public static class DictateColums implements BaseColumns {
		public static final String TableName = "dictateword";

		public static final String NAME = "name";// 原始词
		public static final int INDEX_NAME = 2;

		public static final String PINYIN = "pinyin";// 拼音
		public static final int INDEX_PINYIN = 3;

		public static final String COMMENT = "comment";// 释义
		public static final int INDEX_COMMENT = 4;

		public static final String DICTATE = "dictate";// 听写词
		public static final int INDEX_DICTATE = 5;
		public static final String ORIGINAL = "original";// 出处
		public static final int INDEX_ORIGINAL = 6;

		public static final String EXAMPLE = "example";// 示例
		public static final int INDEX_EXAMPLE = 7;
		public static final String IMG_DES = "image_descrition";// 图片描述
		public static final int INDEX_IMG_DES = 8;
		public static final String LEVEL = "level";// 难度
		public static final int INDEX_LEVEL = 9;
		public static final String ALLUSION = "allusion";// 典故
		public static final int INDEX_ALLUSION = 10;
		public static final String RESULT = "result";// 听写结果
		public static final int INDEX_RESULT = 11;

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TableName);

	}
}
