package com.zhaoyan.juyou.game.chengyudahui.db;

import com.zhaoyan.juyou.game.chengyudahui.db.ChengyuData.ChengyuColums;
import com.zhaoyan.juyou.game.chengyudahui.db.HistoryData.HistoryColums;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class HistoryProvider extends ContentProvider {
	private SQLiteDatabase mHistoryDatabase;
	private static UriMatcher mUriMatcher;
	private HistoryDB mHistoryDB;
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(HistoryData.AUTHORITY, HistoryColums.TableName, 0);
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub

		switch (mUriMatcher.match(arg0)) {
		case 0:
			if (mHistoryDatabase == null || mHistoryDatabase.isReadOnly()) {
				mHistoryDB.close();
				mHistoryDatabase = mHistoryDB.getWritableDatabase();
			}
			long id = mHistoryDatabase.insert(HistoryColums.TableName, null,
					arg1);
			if (id > 0) {
				return ContentUris
						.withAppendedId(HistoryColums.CONTENT_URI, id);
			}
			break;

		default:
			break;
		}
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mHistoryDB = new HistoryDB(getContext(), HistoryColums.TableName, null,
				HistoryData.HISTORY_VERSION);
		return mHistoryDB == null ? false : true;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		switch (mUriMatcher.match(arg0)) {
		case 0:
			builder.setTables(HistoryColums.TableName);
			break;
		default:
			throw new IllegalArgumentException("Unknow Uri : " + arg0);
		}
		if (mHistoryDatabase == null) {
			mHistoryDatabase = mHistoryDB.getReadableDatabase();
		}
		return builder.query(mHistoryDatabase, arg1, arg2, arg3, null, null,
				arg4);
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		switch (mUriMatcher.match(arg0)) {
		case 0:
			if (mHistoryDatabase == null || mHistoryDatabase.isReadOnly()) {
				mHistoryDB.close();
				mHistoryDatabase = mHistoryDB.getWritableDatabase();
			}
			int id = mHistoryDatabase.update(HistoryColums.TableName, arg1,
					arg2, arg3);
			if(id>0)
				return id;
			break;

		default:
			break;
		}
		return 0;
	}

}
