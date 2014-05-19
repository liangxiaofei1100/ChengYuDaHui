package com.zhaoyan.communication.provider;

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

import com.zhaoyan.communication.util.Log;


public class ZhaoYanCommunicationProvider extends ContentProvider {
	private static final String TAG = "ZhaoyanProvider";

	private SQLiteDatabase mSqLiteDatabase;
	private DatabaseHelper mDatabaseHelper;

	public static final int HISTORY_COLLECTION = 10;
	public static final int HISTORY_SINGLE = 11;
	public static final int HISTORY_FILTER = 12;

	public static final int TRAFFIC_STATICS_RX_COLLECTION = 20;
	public static final int TRAFFIC_STATICS_RX_SINGLE = 21;
	public static final int TRAFFIC_STATICS_TX_COLLECTION = 22;
	public static final int TRAFFIC_STATICS_TX_SINGLE = 23;

	public static final int USER_COLLECTION = 30;
	public static final int USER_SINGLE = 31;

	public static final int ACCOUNT_COLLECTION = 40;
	public static final int ACCOUNT_SINGLE = 41;

	public static final UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(ZhaoYanCommunicationData.AUTHORITY, "history", HISTORY_COLLECTION);
		uriMatcher.addURI(ZhaoYanCommunicationData.AUTHORITY, "history/*", HISTORY_SINGLE);
		uriMatcher.addURI(ZhaoYanCommunicationData.AUTHORITY, "history_filter/*",
				HISTORY_FILTER);

		uriMatcher.addURI(ZhaoYanCommunicationData.AUTHORITY, "trafficstatics_rx",
				TRAFFIC_STATICS_RX_COLLECTION);
		uriMatcher.addURI(ZhaoYanCommunicationData.AUTHORITY, "trafficstatics_rx/#",
				TRAFFIC_STATICS_RX_SINGLE);
		uriMatcher.addURI(ZhaoYanCommunicationData.AUTHORITY, "trafficstatics_tx",
				TRAFFIC_STATICS_TX_COLLECTION);
		uriMatcher.addURI(ZhaoYanCommunicationData.AUTHORITY, "trafficstatics_tx/#",
				TRAFFIC_STATICS_TX_SINGLE);

		uriMatcher.addURI(ZhaoYanCommunicationData.AUTHORITY, "user", USER_COLLECTION);
		uriMatcher.addURI(ZhaoYanCommunicationData.AUTHORITY, "user/#", USER_SINGLE);

		uriMatcher.addURI(ZhaoYanCommunicationData.AUTHORITY, "account", ACCOUNT_COLLECTION);
		uriMatcher.addURI(ZhaoYanCommunicationData.AUTHORITY, "account/#", ACCOUNT_SINGLE);
	}

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new DatabaseHelper(getContext());
		return (mDatabaseHelper == null) ? false : true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
		String table;

		switch (uriMatcher.match(uri)) {
		case HISTORY_COLLECTION:
			table = ZhaoYanCommunicationData.History.TABLE_NAME;
			break;
		case HISTORY_SINGLE:
			table = ZhaoYanCommunicationData.History.TABLE_NAME;
			String segment2 = uri.getPathSegments().get(1);
			if (selection != null && segment2.length() > 0) {
				selection = "_id=" + segment2 + " AND (" + selection + ")";
			} else {
				// 由于segment是个string，那么需要给他加个'',如果是int型的就不需要了
				// selection = "pkg_name='" + segment + "'";
			}
			break;

		case TRAFFIC_STATICS_RX_SINGLE:
			table = ZhaoYanCommunicationData.TrafficStaticsRX.TABLE_NAME;
			selection = "_id=" + uri.getPathSegments().get(1);
			selectionArgs = null;
			break;
		case TRAFFIC_STATICS_RX_COLLECTION:
			table = ZhaoYanCommunicationData.TrafficStaticsRX.TABLE_NAME;
			break;
		case TRAFFIC_STATICS_TX_SINGLE:
			table = ZhaoYanCommunicationData.TrafficStaticsTX.TABLE_NAME;
			selection = "_id=" + uri.getPathSegments().get(1);
			selectionArgs = null;
			break;
		case TRAFFIC_STATICS_TX_COLLECTION:
			table = ZhaoYanCommunicationData.TrafficStaticsTX.TABLE_NAME;
			break;
		case USER_SINGLE:
			table = ZhaoYanCommunicationData.User.TABLE_NAME;
			selection = "_id=" + uri.getPathSegments().get(1);
			selectionArgs = null;
			break;
		case USER_COLLECTION:
			table = ZhaoYanCommunicationData.User.TABLE_NAME;
			break;
		case ACCOUNT_SINGLE:
			table = ZhaoYanCommunicationData.Account.TABLE_NAME;
			selection = "_id=" + uri.getPathSegments().get(1);
			selectionArgs = null;
			break;
		case ACCOUNT_COLLECTION:
			table = ZhaoYanCommunicationData.Account.TABLE_NAME;
			break;
		default:
			throw new IllegalArgumentException("UnKnow Uri:" + uri);
		}

		int count = mSqLiteDatabase.delete(table, selection, selectionArgs);
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case HISTORY_COLLECTION:
			return ZhaoYanCommunicationData.History.CONTENT_TYPE;
		case HISTORY_SINGLE:
			return ZhaoYanCommunicationData.History.CONTENT_TYPE_ITEM;

		case TRAFFIC_STATICS_TX_COLLECTION:
			return ZhaoYanCommunicationData.TrafficStaticsRX.CONTENT_TYPE;
		case TRAFFIC_STATICS_RX_SINGLE:
			return ZhaoYanCommunicationData.TrafficStaticsRX.CONTENT_TYPE_ITEM;
		case USER_COLLECTION:
			return ZhaoYanCommunicationData.User.CONTENT_TYPE;
		case USER_SINGLE:
			return ZhaoYanCommunicationData.User.CONTENT_TYPE_ITEM;
		case ACCOUNT_COLLECTION:
			return ZhaoYanCommunicationData.Account.CONTENT_TYPE;
		case ACCOUNT_SINGLE:
			return ZhaoYanCommunicationData.Account.CONTENT_TYPE_ITEM;
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
		case HISTORY_COLLECTION:
		case HISTORY_SINGLE:
			table = ZhaoYanCommunicationData.History.TABLE_NAME;
			contentUri = ZhaoYanCommunicationData.History.CONTENT_URI;
			break;

		case TRAFFIC_STATICS_RX_SINGLE:
		case TRAFFIC_STATICS_RX_COLLECTION:
			table = ZhaoYanCommunicationData.TrafficStaticsRX.TABLE_NAME;
			contentUri = ZhaoYanCommunicationData.TrafficStaticsRX.CONTENT_URI;
			break;

		case TRAFFIC_STATICS_TX_SINGLE:
		case TRAFFIC_STATICS_TX_COLLECTION:
			table = ZhaoYanCommunicationData.TrafficStaticsTX.TABLE_NAME;
			contentUri = ZhaoYanCommunicationData.TrafficStaticsTX.CONTENT_URI;
			break;

		case USER_SINGLE:
		case USER_COLLECTION:
			table = ZhaoYanCommunicationData.User.TABLE_NAME;
			contentUri = ZhaoYanCommunicationData.User.CONTENT_URI;
			break;

		case ACCOUNT_SINGLE:
		case ACCOUNT_COLLECTION:
			table = ZhaoYanCommunicationData.Account.TABLE_NAME;
			contentUri = ZhaoYanCommunicationData.Account.CONTENT_URI;
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
		case HISTORY_COLLECTION:
			qb.setTables(ZhaoYanCommunicationData.History.TABLE_NAME);
			break;
		case HISTORY_SINGLE:
			qb.setTables(ZhaoYanCommunicationData.History.TABLE_NAME);
			qb.appendWhere("_id=");
			qb.appendWhere(uri.getPathSegments().get(1));
			break;
		case HISTORY_FILTER:
			break;

		case TRAFFIC_STATICS_RX_SINGLE:
			qb.setTables(ZhaoYanCommunicationData.TrafficStaticsRX.TABLE_NAME);
			qb.appendWhere("_id=" + uri.getPathSegments().get(1));
			break;
		case TRAFFIC_STATICS_RX_COLLECTION:
			qb.setTables(ZhaoYanCommunicationData.TrafficStaticsRX.TABLE_NAME);
			break;

		case TRAFFIC_STATICS_TX_SINGLE:
			qb.setTables(ZhaoYanCommunicationData.TrafficStaticsTX.TABLE_NAME);
			qb.appendWhere("_id=" + uri.getPathSegments().get(1));
			break;
		case TRAFFIC_STATICS_TX_COLLECTION:
			qb.setTables(ZhaoYanCommunicationData.TrafficStaticsTX.TABLE_NAME);
			break;
		case USER_SINGLE:
			qb.setTables(ZhaoYanCommunicationData.User.TABLE_NAME);
			qb.appendWhere("_id=" + uri.getPathSegments().get(1));
			break;
		case USER_COLLECTION:
			qb.setTables(ZhaoYanCommunicationData.User.TABLE_NAME);
			break;
		case ACCOUNT_SINGLE:
			qb.setTables(ZhaoYanCommunicationData.Account.TABLE_NAME);
			qb.appendWhere("_id=" + uri.getPathSegments().get(1));
			break;
		case ACCOUNT_COLLECTION:
			qb.setTables(ZhaoYanCommunicationData.Account.TABLE_NAME);
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
		case HISTORY_SINGLE:
			table = ZhaoYanCommunicationData.History.TABLE_NAME;
			selection = "_id=" + uri.getPathSegments().get(1);
			selectionArgs = null;
			break;
		case HISTORY_COLLECTION:
			table = ZhaoYanCommunicationData.History.TABLE_NAME;
			break;

		case TRAFFIC_STATICS_RX_SINGLE:
			table = ZhaoYanCommunicationData.TrafficStaticsRX.TABLE_NAME;
			selection = "_id=" + uri.getPathSegments().get(1);
			selectionArgs = null;
			break;
		case TRAFFIC_STATICS_RX_COLLECTION:
			table = ZhaoYanCommunicationData.TrafficStaticsRX.TABLE_NAME;
			break;
		case TRAFFIC_STATICS_TX_SINGLE:
			table = ZhaoYanCommunicationData.TrafficStaticsTX.TABLE_NAME;
			selection = "_id=" + uri.getPathSegments().get(1);
			selectionArgs = null;
			break;
		case TRAFFIC_STATICS_TX_COLLECTION:
			table = ZhaoYanCommunicationData.TrafficStaticsTX.TABLE_NAME;
			break;
		case USER_SINGLE:
			table = ZhaoYanCommunicationData.User.TABLE_NAME;
			selection = "_id=" + uri.getPathSegments().get(1);
			selectionArgs = null;
			break;
		case USER_COLLECTION:
			table = ZhaoYanCommunicationData.User.TABLE_NAME;
			break;
		case ACCOUNT_SINGLE:
			table = ZhaoYanCommunicationData.Account.TABLE_NAME;
			selection = "_id=" + uri.getPathSegments().get(1);
			selectionArgs = null;
			break;
		case ACCOUNT_COLLECTION:
			table = ZhaoYanCommunicationData.Account.TABLE_NAME;
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
			super(context, ZhaoYanCommunicationData.DATABASE_NAME, null,
					ZhaoYanCommunicationData.DATABASE_VERSION);
			Log.d(TAG, "DatabaseHelper");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "DatabaseHelper.onCreate");

			// create history table
			db.execSQL("create table " + ZhaoYanCommunicationData.History.TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ ZhaoYanCommunicationData.History.FILE_PATH + " TEXT, "
					+ ZhaoYanCommunicationData.History.FILE_NAME + " TEXT, "
					+ ZhaoYanCommunicationData.History.FILE_SIZE + " LONG, "
					+ ZhaoYanCommunicationData.History.SEND_USERNAME + " TEXT, "
					+ ZhaoYanCommunicationData.History.RECEIVE_USERNAME + " TEXT, "
					+ ZhaoYanCommunicationData.History.PROGRESS + " LONG, "
					+ ZhaoYanCommunicationData.History.DATE + " LONG, "
					+ ZhaoYanCommunicationData.History.STATUS + " INTEGER, "
					+ ZhaoYanCommunicationData.History.MSG_TYPE + " INTEGER, "
					+ ZhaoYanCommunicationData.History.FILE_TYPE + " INTEGER, "
					+ ZhaoYanCommunicationData.History.FILE_ICON + " BLOB, "
					+ ZhaoYanCommunicationData.History.SEND_USER_HEADID + " INTEGER, "
					+ ZhaoYanCommunicationData.History.SEND_USER_ICON + " BLOB);");

			// create traffic statics rx table.
			db.execSQL("create table " + ZhaoYanCommunicationData.TrafficStaticsRX.TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ ZhaoYanCommunicationData.TrafficStaticsRX.DATE + " TEXT UNIQUE, "
					+ ZhaoYanCommunicationData.TrafficStaticsRX.TOTAL_RX_BYTES + " LONG);");

			// create traffic statics tx table.
			db.execSQL("create table " + ZhaoYanCommunicationData.TrafficStaticsTX.TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ ZhaoYanCommunicationData.TrafficStaticsTX.DATE + " TEXT UNIQUE, "
					+ ZhaoYanCommunicationData.TrafficStaticsTX.TOTAL_TX_BYTES + " LONG);");

			// create user table
			db.execSQL("create table " + ZhaoYanCommunicationData.User.TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ ZhaoYanCommunicationData.User.USER_NAME + " TEXT, "
					+ ZhaoYanCommunicationData.User.USER_ID + " INTEGER, "
					+ ZhaoYanCommunicationData.User.HEAD_ID + " INTEGER, "
					+ ZhaoYanCommunicationData.User.THIRD_LOGIN + " INTEGER, "
					+ ZhaoYanCommunicationData.User.HEAD_DATA + " BLOB, "
					+ ZhaoYanCommunicationData.User.IP_ADDR + " TEXT, "
					+ ZhaoYanCommunicationData.User.STATUS + " INTEGER, "
					+ ZhaoYanCommunicationData.User.TYPE + " INTEGER, " + ZhaoYanCommunicationData.User.SSID
					+ " TEXT, " + ZhaoYanCommunicationData.User.NETWORK + " INTEGER, "
					+ ZhaoYanCommunicationData.User.SIGNATURE + " TEXT);");

			// create account table
			db.execSQL("create table " + ZhaoYanCommunicationData.Account.TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ ZhaoYanCommunicationData.Account.USER_NAME + " TEXT, "
					+ ZhaoYanCommunicationData.Account.HEAD_ID + " INTEGER, "
					+ ZhaoYanCommunicationData.Account.HEAD_DATA + " BLOB, "
					+ ZhaoYanCommunicationData.Account.ACCOUNT_ZHAOYAN + " TEXT, "
					+ ZhaoYanCommunicationData.Account.PHONE_NUMBER + " TEXT, "
					+ ZhaoYanCommunicationData.Account.EMAIL + " TEXT, "
					+ ZhaoYanCommunicationData.Account.ACCOUNT_QQ + " TEXT, "
					+ ZhaoYanCommunicationData.Account.ACCOUNT_RENREN + " TEXT, "
					+ ZhaoYanCommunicationData.Account.ACCOUNT_SINA_WEIBO + " TEXT, "
					+ ZhaoYanCommunicationData.Account.ACCOUNT_TENCENT_WEIBO + " TEXT, "
					+ ZhaoYanCommunicationData.Account.SIGNATURE + " TEXT, "
					+ ZhaoYanCommunicationData.Account.LOGIN_STATUS + " INTEGER, "
					+ ZhaoYanCommunicationData.Account.TOURIST_ACCOUNT + " INTEGER, "
					+ ZhaoYanCommunicationData.Account.LAST_LOGIN_TIME + " LONG);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + ZhaoYanCommunicationData.History.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "
					+ ZhaoYanCommunicationData.TrafficStaticsRX.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "
					+ ZhaoYanCommunicationData.TrafficStaticsTX.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + ZhaoYanCommunicationData.User.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + ZhaoYanCommunicationData.Account.TABLE_NAME);
			onCreate(db);
		}

	}

}
