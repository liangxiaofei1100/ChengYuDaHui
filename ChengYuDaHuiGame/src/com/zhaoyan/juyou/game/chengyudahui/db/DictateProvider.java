package com.zhaoyan.juyou.game.chengyudahui.db;

import com.zhaoyan.juyou.game.chengyudahui.DBConfig;
import com.zhaoyan.juyou.game.chengyudahui.db.DictateData.DictateColums;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DictateProvider extends ContentProvider {
	private static UriMatcher mUriMatcher;
	private SQLiteDatabase mSqLiteDatabase;
	private ChengyuDbHelper mChengyuDbHelper;
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(DictateData.AUTHORITY, DictateColums.TableName, 0);
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mChengyuDbHelper = new ChengyuDbHelper();
		return mChengyuDbHelper == null ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		switch (mUriMatcher.match(uri)) {
		case 0:
			builder.setTables(DictateColums.TableName);
			break;
		default:
			throw new IllegalArgumentException("Unknow Uri : " + uri);
		}
		if (mSqLiteDatabase == null) {
			mSqLiteDatabase = mChengyuDbHelper.getReadDb(DBConfig.DICTATE_DB_PATH);
		}

		return builder.query(mSqLiteDatabase, projection, selection,
				selectionArgs, null, null, sortOrder);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
