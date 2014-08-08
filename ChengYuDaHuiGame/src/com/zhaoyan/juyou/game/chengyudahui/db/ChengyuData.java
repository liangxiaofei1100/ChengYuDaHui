package com.zhaoyan.juyou.game.chengyudahui.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class ChengyuData {
	public static final String CHENGYUDBNAME = "chengyu.db";
	public static final int CHENGYUVERSION = 1;
	public static final String AUTHORITY = "com.zhaoyan.juyou.game.chengyudahui.db.chengyuprovider";

	public static class ChengyuColums implements BaseColumns {
		public static final String TableName = "chengyu";
		public static final String NAME = "name";

		public static final String PINYIN = "pinyin";

		public static final String COMMENT = "comment";

		public static final String ORIGINAL = "original";

		public static final String EXAMPLE = "example";

		public static final String ENGLISH = "english";

		public static final String SIMILAR = "similar";

		public static final String OPPOSITE = "opposite";
		public static final String FREQUENTLY = "frequently";
		public static final String CAICI = "caici";

		public static final String STORY = "story";

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TableName);
		
	}

}
