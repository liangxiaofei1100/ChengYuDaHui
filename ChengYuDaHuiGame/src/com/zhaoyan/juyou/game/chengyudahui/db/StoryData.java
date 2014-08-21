package com.zhaoyan.juyou.game.chengyudahui.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class StoryData {
	public static final String DATABASE_NAME = "story.db";
	public static final int DATABASE_VERSION = 1;
	public static final String AUTHORITY = "com.zhaoyan.juyou.game.chengyudahui.db.storyprovider";

	public static class TypeColums implements BaseColumns {
		public static final String TABLE_NAME = "type";
		
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/type";
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/type";
		
		public static final String TYPE = "type";
		public static final String NAME = "name";
		public static final String FOLDER = "folder";
	}
	
	public static class ItemColums implements BaseColumns {
		public static final String BEAR_TABLE_NAME = "bear";
		public static final String CHENGYU_TABLE_NAME = "chengyu";
		public static final String SLEEP_TABLE_NAME = "sleep";
		public static final String CHILD_TABLE_NAME = "child";
		public static final String FAIRY_TALE_TABLE_NAME = "fairytale";
		public static final String HISTORY_TABLE_NAME = "history";
		public static final String GOLD_CAT_TABLE_NAME = "goldcat";
		public static final String XIYOUJI_TABLE_NAME = "xiyouji";
		public static final String CHILD_SONG_TABLE_NAME = "childsong";
		
		
		public static final Uri BEAR_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + BEAR_TABLE_NAME);
		public static final Uri CHENGYU_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + CHENGYU_TABLE_NAME);
		public static final Uri SLEEP_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + SLEEP_TABLE_NAME);
		public static final Uri CHILD_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + CHILD_TABLE_NAME);
		public static final Uri FAIRY_TALE_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + FAIRY_TALE_TABLE_NAME);
		public static final Uri HISTORY_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + HISTORY_TABLE_NAME);
		public static final Uri GOLD_CAT_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + GOLD_CAT_TABLE_NAME);
		public static final Uri XIYOUJI_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + XIYOUJI_TABLE_NAME);
		public static final Uri CHILD_SONG_CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + CHILD_SONG_TABLE_NAME);
		
		public static final String BEAR_CONTENT_TYPE = "vnd.android.cursor.dir/BEAR";
		public static final String BEAR_CONTENT_TYPE_ITEM = "vnd.android.cursor.item/BEAR";
		
		public static final String CHENGYU_CONTENT_TYPE = "vnd.android.cursor.dir/CHENGYU";
		public static final String CHENGYU_CONTENT_TYPE_ITEM = "vnd.android.cursor.item/CHENGYU";
		
		public static final String SLEEP_CONTENT_TYPE = "vnd.android.cursor.dir/SLEEP";
		public static final String SLEEP_CONTENT_TYPE_ITEM = "vnd.android.cursor.item/SLEEP";
		
		public static final String CHILD_CONTENT_TYPE = "vnd.android.cursor.dir/CHILD";
		public static final String CHILD_CONTENT_TYPE_ITEM = "vnd.android.cursor.item/CHILD";
		
		public static final String FAIRY_TALE_CONTENT_TYPE = "vnd.android.cursor.dir/FAIRY_TALE";
		public static final String FAIRY_TALE_CONTENT_TYPE_ITEM = "vnd.android.cursor.item/FAIRY_TALE";
		
		public static final String HISTORY_CONTENT_TYPE = "vnd.android.cursor.dir/HISTORY";
		public static final String HISTORY_CONTENT_TYPE_ITEM = "vnd.android.cursor.item/HISTORY";
		
		public static final String GOLD_CAT_CONTENT_TYPE = "vnd.android.cursor.dir/GOLD_CAT";
		public static final String GOLD_CAT_CONTENT_TYPE_ITEM = "vnd.android.cursor.item/GOLD_CAT";
		
		public static final String XIYOUJI_CONTENT_TYPE = "vnd.android.cursor.dir/XIYOUJI";
		public static final String XIYOUJI_CONTENT_TYPE_ITEM = "vnd.android.cursor.item/XIYOUJI";
		
		public static final String CHILD_SONG_CONTENT_TYPE = "vnd.android.cursor.dir/CHILD_SONG";
		public static final String CHILD_SONG_CONTENT_TYPE_ITEM = "vnd.android.cursor.item/CHILD_SONG";
		
		public static final String TYPE = "type";
		public static final String TITLE = "title";
		public static final String FILENAME = "filename";
		public static final String SIZE = "size";
	}

}
