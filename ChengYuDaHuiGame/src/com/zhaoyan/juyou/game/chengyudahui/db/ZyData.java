package com.zhaoyan.juyou.game.chengyudahui.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class ZyData {
	public static final String DATABASE_NAME = "zhaoyan.db";
	public static final int DATABASE_VERSION = 1;

	public static final String AUTHORITY = "com.zhaoyan.juyou.game.chengyudahui.db.ZyProvider";

	public static final class SignInColumns implements BaseColumns{
		public static final String TABLE_NAME = "signin";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/signin");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/signin";
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/signin";
		
		/**
		 * sign in date,type:long
		 */
		public static final String DATE = "date";
		
	}

}
