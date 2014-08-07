package com.zhaoyan.juyou.game.chengyudahui.knowledge;

import android.net.Uri;
import android.provider.BaseColumns;

public class NoteMetaData{
	public static final String DATABASE_NAME = "word.db";
	public static final int DATABASE_VERSION = 2;

	public static final String AUTHORITY = "com.yuri.notebook.db.notebookproviderrrr";
	
	/**profiles table*/
	public static final class Note implements BaseColumns{
		public static final String TABLE_NAME = "word";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/word");
		public static final Uri CONTENT_FILTER_URI = Uri.parse("content://" + AUTHORITY + "/word_filter");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/word";
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/word";
		
		//items
		/**note title. Type:String*/
		public static final String WORD = "word";
		/**note content. Type:String*/
		
		/**order by _id DESC*/
		public static final String SORT_ORDER_DEFAULT = _ID + " DESC"; 
	}
}
