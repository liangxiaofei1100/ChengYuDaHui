package com.zhaoyan.juyou.game.chengyudahui.db;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.db.ZyData.SignInColumns;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


public class ZyProvider extends ContentProvider {
	private static final String TAG = ZyProvider.class.getSimpleName();

	private SQLiteDatabase mSqLiteDatabase;
	private DatabaseHelper mDatabaseHelper;
	
	public static final int SIGNIN_COLLECTION = 10;
	public static final int SIGNIN_SINGLE = 11;

	public static final UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		uriMatcher.addURI(ZyData.AUTHORITY, "signin", SIGNIN_COLLECTION);
		uriMatcher.addURI(ZyData.AUTHORITY, "signin/#", SIGNIN_SINGLE);
	}

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new DatabaseHelper(getContext());
		return (mDatabaseHelper == null) ? false : true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case SIGNIN_COLLECTION:
			return ZyData.SignInColumns.CONTENT_TYPE;
		case SIGNIN_SINGLE:
			return ZyData.SignInColumns.CONTENT_TYPE_ITEM;
		default:
			throw new IllegalArgumentException("Unkonw uri:" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.v(TAG, "insert db");
		mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
		String table;
		Uri contentUri;

		switch (uriMatcher.match(uri)) {
		case SIGNIN_COLLECTION:
		case SIGNIN_SINGLE:
			table = ZyData.SignInColumns.TABLE_NAME;
			contentUri = ZyData.SignInColumns.CONTENT_URI;
			break;
		default:
			throw new IllegalArgumentException("Unknow uri:" + uri);
		}

		long rowId = mSqLiteDatabase.insertWithOnConflict(table, "", values,
				SQLiteDatabase.CONFLICT_REPLACE);
		if (rowId > 0) {
			Uri rowUri = ContentUris.withAppendedId(contentUri, rowId);
			getContext().getContentResolver().notifyChange(uri, null);
			Log.v(TAG, "insertDb.rowId=" + rowId);
			return rowUri;
		}
		throw new IllegalArgumentException("Cannot insert into uri:" + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (uriMatcher.match(uri)) {
		case SIGNIN_COLLECTION:
			qb.setTables(ZyData.SignInColumns.TABLE_NAME);
			break;
		case SIGNIN_SINGLE:
			qb.setTables(ZyData.SignInColumns.TABLE_NAME);
			qb.appendWhere(SignInColumns.DATE + "=");
			qb.appendWhere(uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknow uri:" + uri);
		}

		mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
		Cursor ret = qb.query(mSqLiteDatabase, projection, selection,
				selectionArgs, null, null, sortOrder);

		if (ret != null) {
			ret.setNotificationUri(getContext().getContentResolver(), uri);
		}

		return ret;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int match = uriMatcher.match(uri);
		mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
		String table;

		switch (match) {
		case SIGNIN_SINGLE:
			table = SignInColumns.TABLE_NAME;
			selection = "_id=" + uri.getPathSegments().get(1);
			selectionArgs = null;
			break;
		case SIGNIN_COLLECTION:
			table = SignInColumns.TABLE_NAME;
			break;

		default:
			throw new UnsupportedOperationException("Cannot update uri:" + uri);
		}
		int count = mSqLiteDatabase.update(table, values, selection, null);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, ZyData.DATABASE_NAME, null,
					ZyData.DATABASE_VERSION);
			Log.d(TAG, "DatabaseHelper");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "DatabaseHelper.onCreate");

			
			//create signin table
			db.execSQL("create table " + SignInColumns.TABLE_NAME
			+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ SignInColumns.DATE + " LONG);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "
					+ SignInColumns.TABLE_NAME);
			onCreate(db);
		}

	}

}
