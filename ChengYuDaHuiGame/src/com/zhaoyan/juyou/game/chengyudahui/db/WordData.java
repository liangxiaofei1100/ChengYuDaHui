package com.zhaoyan.juyou.game.chengyudahui.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class WordData {
	public static final String WORDDBNAME = "word.db";
	public static final int WORDVERSION = 1;
	public static final String AUTHORITY = "com.zhaoyan.juyou.game.chengyudahui.db.wordprovider";

	public static class WordColums implements BaseColumns {
		public static final String TableName = "word";
		public static final String WORD = "word";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TableName);
	}

}
