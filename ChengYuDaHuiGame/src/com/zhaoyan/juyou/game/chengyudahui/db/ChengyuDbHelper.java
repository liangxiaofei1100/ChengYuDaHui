package com.zhaoyan.juyou.game.chengyudahui.db;

import java.io.File;

import com.zhaoyan.communication.util.Log;

import android.database.sqlite.SQLiteDatabase;

public class ChengyuDbHelper {
	private static final String TAG = ChengyuDbHelper.class.getSimpleName();

	public SQLiteDatabase getReadDb(String path) {
		Log.d(TAG, "getReadDb.path:" + path);
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			try {
				return SQLiteDatabase.openDatabase(path, null,
						SQLiteDatabase.OPEN_READWRITE);
			} catch (Exception e) {
				Log.e(TAG, "getReadDb.error:" + e.toString());
				e.printStackTrace();
			}
		}
		Log.e(TAG, path + " is not exist or is not file.");
		return null;
	}

}
