package com.zhaoyan.juyou.game.chengyudahui.db;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ChengyuDbHelper {

	public SQLiteDatabase getReadDb(String path) {
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			return SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY);
		}
		return null;
	}

}
