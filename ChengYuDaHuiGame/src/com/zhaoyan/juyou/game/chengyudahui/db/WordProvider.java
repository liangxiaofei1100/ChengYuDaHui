package com.zhaoyan.juyou.game.chengyudahui.db;

import com.zhaoyan.juyou.game.chengyudahui.db.WordData.WordColums;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


/**
 * need to more,just modify for query
 * 
 * */
public class WordProvider extends ContentProvider {
	private final String TAG = WordProvider.class.getName();
	private static UriMatcher mUriMatcher;
	private SQLiteDatabase mSqLiteDatabase;
	private ChengyuDbHelper mChengyuDbHelper;
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(WordData.AUTHORITY, WordColums.TableName, 0);
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
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mChengyuDbHelper = new ChengyuDbHelper();
		return mChengyuDbHelper == null ? false : true;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		switch (mUriMatcher.match(arg0)) {
		case 0:
			builder.setTables(WordColums.TableName);
			if (mSqLiteDatabase == null) {
				mSqLiteDatabase = mChengyuDbHelper
						.getReadDb(com.zhaoyan.juyou.game.chengyudahui.MainActivity.WORD_PATH);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknow Uri : " + arg0);
		}

		return builder.query(mSqLiteDatabase, arg1, arg2, arg3, null, null,
				arg4);
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}