package com.zhaoyan.juyou.game.chengyudahui.knowledge;

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
import android.util.Log;

public class NoteProvider extends ContentProvider {
	private static final String TAG = "NoteProvider";
	
	private SQLiteDatabase mSqLiteDatabase;
	private DatabaseHelper mDatabaseHelper;
	
	public static final int NOTEBOOK_COLLECTION = 1;
	public static final int NOTEBOOK_SINGLE = 2;
	
	public static final int NOTEBOOK_FILTER = 5;
	
	public static final UriMatcher uriMatcher;
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(NoteMetaData.AUTHORITY, "word", NOTEBOOK_COLLECTION);
		uriMatcher.addURI(NoteMetaData.AUTHORITY, "word/#", NOTEBOOK_SINGLE);
		uriMatcher.addURI(NoteMetaData.AUTHORITY, "word_filter/*", NOTEBOOK_FILTER);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			super(context, NoteMetaData.DATABASE_NAME, null, NoteMetaData.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//notes table
			db.execSQL("create table " + NoteMetaData.Note.TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ NoteMetaData.Note.WORD + " TEXT);"
					);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//如果数据库版本发生变化，则删掉重建
			db.execSQL("DROP TABLE IF EXISTS " + NoteMetaData.Note.TABLE_NAME);
			onCreate(db);
		}
		
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
		
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case NOTEBOOK_COLLECTION:
			
			count = mSqLiteDatabase.delete(NoteMetaData.Note.TABLE_NAME, selection, selectionArgs);
			break;
		case NOTEBOOK_SINGLE:
			String segment = uri.getPathSegments().get(1);
			if (selection != null && segment.length() > 0) {
				selection = "_id=" + segment + " AND (" + selection + ")";
			}else {
				selection = "_id=" +  segment;
			}
			count = mSqLiteDatabase.delete(NoteMetaData.Note.TABLE_NAME, selection, selectionArgs);
			break;
			
		default:
			throw new IllegalArgumentException("UnKnow Uri:" + uri);
		}
		
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case NOTEBOOK_COLLECTION:
			return NoteMetaData.Note.CONTENT_TYPE;
		case NOTEBOOK_SINGLE:
			return NoteMetaData.Note.CONTENT_TYPE_ITEM;
			
		default:
			throw new IllegalArgumentException("Unkonw uri:" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, "insert db");
		switch (uriMatcher.match(uri)) {
		case NOTEBOOK_COLLECTION:
		case NOTEBOOK_SINGLE:
			mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
			long rowId = mSqLiteDatabase.insertWithOnConflict(NoteMetaData.Note.TABLE_NAME, "", 
					values, SQLiteDatabase.CONFLICT_REPLACE);
			if (rowId > 0) {
				Uri rowUri = ContentUris.withAppendedId(NoteMetaData.Note.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(uri, null);
				return rowUri;
			}
			throw new IllegalArgumentException("Cannot insert into uri:" + uri);
		default:
			throw new IllegalArgumentException("Unknow uri:" + uri);
		}
	}

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new DatabaseHelper(getContext());
		return (mDatabaseHelper == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch (uriMatcher.match(uri)) {
		case NOTEBOOK_COLLECTION:
			qb.setTables(NoteMetaData.Note.TABLE_NAME);
			break;
		case NOTEBOOK_SINGLE:
			qb.setTables(NoteMetaData.Note.TABLE_NAME);
			qb.appendWhere("_id=");
			qb.appendWhere(uri.getPathSegments().get(1));
			break;
		case NOTEBOOK_FILTER:
			qb.setTables(NoteMetaData.Note.TABLE_NAME);
			break;
		default:
			throw new IllegalArgumentException("Unknow uri:" + uri);
		}
		
		mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
		Cursor ret = qb.query(mSqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
		
		if (ret != null) {
			ret.setNotificationUri(getContext().getContentResolver(), uri);
		}
		
		return ret;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count;
		long rowId = 0;
		int match = uriMatcher.match(uri);
		mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
		
		switch (match) {
		case NOTEBOOK_SINGLE:
			String segment = uri.getPathSegments().get(1);
			rowId = Long.parseLong(segment);
			
			count = mSqLiteDatabase.update(NoteMetaData.Note.TABLE_NAME, values, "_id=" + rowId, null);
			break;
		case NOTEBOOK_COLLECTION:
			count = mSqLiteDatabase.update(NoteMetaData.Note.TABLE_NAME, values, selection, null);
			break;
		default:
			throw new UnsupportedOperationException("Cannot update uri:" + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

}
