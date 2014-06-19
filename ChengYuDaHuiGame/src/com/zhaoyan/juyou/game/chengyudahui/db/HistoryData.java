package com.zhaoyan.juyou.game.chengyudahui.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class HistoryData {
	public static final String HISTORY_DB_NAME = "history.db";
	public static final int HISTORY_VERSION = 1;
	public static final String AUTHORITY = "com.zhaoyan.juyou.game.chengyudahui.db.historyrovider";

	public static class HistoryColums implements BaseColumns {
		public static final String TableName = "history";
		/**
		 * in speak & paint is score,in study is chengyu id
		 * */
		public static final String NAME = "name";
		/** 0 study,1 speak,2 paint */
		public static final String KIND = "kind";
		/**
		 * time only for day ,format as "yyyy-MM-dd"
		 * */
		public static final String TIME = "time";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TableName);
	}
}
