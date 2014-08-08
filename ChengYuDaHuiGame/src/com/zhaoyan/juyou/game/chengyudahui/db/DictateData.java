package com.zhaoyan.juyou.game.chengyudahui.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class DictateData {
	public static final String CHENGYUDBNAME = "dictate.db";
	public static final int DICTATEVERSION = 1;
	public static final String AUTHORITY = "com.zhaoyan.juyou.game.chengyudahui.db.dictateprovider";

	public static class DictateColums implements BaseColumns {
		public static final String TableName = "dictateword";
		public static final String NAME = "name";

		public static final String PINYIN = "pinyin";

		public static final String COMMENT = "comment";

		public static final String ORIGINAL = "original";
		public static final String DICTATE = "dictate";

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TableName);

	}
}
