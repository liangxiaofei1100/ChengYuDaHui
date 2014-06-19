//package com.zhaoyan.juyou.game.chengyudahui.db;
//
//import android.content.ContentProvider;
//import android.content.ContentUris;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.UriMatcher;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.database.sqlite.SQLiteQueryBuilder;
//import android.net.Uri;
//
//import com.zhaoyan.common.util.Log;
//
//public class ZyProvider extends ContentProvider {
//	private static final String TAG = "ZhaoyanProvider";
//
//	private SQLiteDatabase mSqLiteDatabase;
//	private DatabaseHelper mDatabaseHelper;
//
//	public static final int HISTORY_COLLECTION = 10;
//	public static final int HISTORY_SINGLE = 11;
//	public static final int HISTORY_FILTER = 12;
//
//	public static final int TRAFFIC_STATICS_RX_COLLECTION = 20;
//	public static final int TRAFFIC_STATICS_RX_SINGLE = 21;
//	public static final int TRAFFIC_STATICS_TX_COLLECTION = 22;
//	public static final int TRAFFIC_STATICS_TX_SINGLE = 23;
//
//	public static final int USER_COLLECTION = 30;
//	public static final int USER_SINGLE = 31;
//
//	public static final int ACCOUNT_COLLECTION = 40;
//	public static final int ACCOUNT_SINGLE = 41;
//
//	public static final UriMatcher uriMatcher;
//
//	static {
//		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//		uriMatcher.addURI(DownloadAppData.AUTHORITY, "history",
//				HISTORY_COLLECTION);
//		uriMatcher.addURI(DownloadAppData.AUTHORITY, "history/*",
//				HISTORY_SINGLE);
//		uriMatcher.addURI(DownloadAppData.AUTHORITY, "history_filter/*",
//				HISTORY_FILTER);
//
//		uriMatcher.addURI(DownloadAppData.AUTHORITY, "trafficstatics_rx",
//				TRAFFIC_STATICS_RX_COLLECTION);
//		uriMatcher.addURI(DownloadAppData.AUTHORITY, "trafficstatics_rx/#",
//				TRAFFIC_STATICS_RX_SINGLE);
//		uriMatcher.addURI(DownloadAppData.AUTHORITY, "trafficstatics_tx",
//				TRAFFIC_STATICS_TX_COLLECTION);
//		uriMatcher.addURI(DownloadAppData.AUTHORITY, "trafficstatics_tx/#",
//				TRAFFIC_STATICS_TX_SINGLE);
//
//		uriMatcher.addURI(DownloadAppData.AUTHORITY, "user", USER_COLLECTION);
//		uriMatcher.addURI(DownloadAppData.AUTHORITY, "user/#", USER_SINGLE);
//
//		uriMatcher.addURI(DownloadAppData.AUTHORITY, "account",
//				ACCOUNT_COLLECTION);
//		uriMatcher.addURI(DownloadAppData.AUTHORITY, "account/#",
//				ACCOUNT_SINGLE);
//	}
//
//	@Override
//	public boolean onCreate() {
//		mDatabaseHelper = new DatabaseHelper(getContext());
//		return (mDatabaseHelper == null) ? false : true;
//	}
//
//	@Override
//	public int delete(Uri uri, String selection, String[] selectionArgs) {
//		mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
//		String table;
//
//		switch (uriMatcher.match(uri)) {
//		case HISTORY_COLLECTION:
//			table = DownloadAppData.DownloadApp.TABLE_NAME;
//			break;
//		case HISTORY_SINGLE:
//			table = DownloadAppData.DownloadApp.TABLE_NAME;
//			String segment2 = uri.getPathSegments().get(1);
//			if (selection != null && segment2.length() > 0) {
//				selection = "_id=" + segment2 + " AND (" + selection + ")";
//			} else {
//				// 由于segment是个string，那么需要给他加个'',如果是int型的就不需要了
//				// selection = "pkg_name='" + segment + "'";
//			}
//			break;
//
//		case TRAFFIC_STATICS_RX_SINGLE:
//			table = DownloadAppData.TrafficStaticsRX.TABLE_NAME;
//			selection = "_id=" + uri.getPathSegments().get(1);
//			selectionArgs = null;
//			break;
//		case TRAFFIC_STATICS_RX_COLLECTION:
//			table = DownloadAppData.TrafficStaticsRX.TABLE_NAME;
//			break;
//		case TRAFFIC_STATICS_TX_SINGLE:
//			table = DownloadAppData.TrafficStaticsTX.TABLE_NAME;
//			selection = "_id=" + uri.getPathSegments().get(1);
//			selectionArgs = null;
//			break;
//		case TRAFFIC_STATICS_TX_COLLECTION:
//			table = DownloadAppData.TrafficStaticsTX.TABLE_NAME;
//			break;
//		case USER_SINGLE:
//			table = DownloadAppData.User.TABLE_NAME;
//			selection = "_id=" + uri.getPathSegments().get(1);
//			selectionArgs = null;
//			break;
//		case USER_COLLECTION:
//			table = DownloadAppData.User.TABLE_NAME;
//			break;
//		case ACCOUNT_SINGLE:
//			table = DownloadAppData.Account.TABLE_NAME;
//			selection = "_id=" + uri.getPathSegments().get(1);
//			selectionArgs = null;
//			break;
//		case ACCOUNT_COLLECTION:
//			table = DownloadAppData.Account.TABLE_NAME;
//			break;
//		default:
//			throw new IllegalArgumentException("UnKnow Uri:" + uri);
//		}
//
//		int count = mSqLiteDatabase.delete(table, selection, selectionArgs);
//		if (count > 0) {
//			getContext().getContentResolver().notifyChange(uri, null);
//		}
//
//		return count;
//	}
//
//	@Override
//	public String getType(Uri uri) {
//		switch (uriMatcher.match(uri)) {
//		case HISTORY_COLLECTION:
//			return DownloadAppData.DownloadApp.CONTENT_TYPE;
//		case HISTORY_SINGLE:
//			return DownloadAppData.DownloadApp.CONTENT_TYPE_ITEM;
//
//		case TRAFFIC_STATICS_TX_COLLECTION:
//			return DownloadAppData.TrafficStaticsRX.CONTENT_TYPE;
//		case TRAFFIC_STATICS_RX_SINGLE:
//			return DownloadAppData.TrafficStaticsRX.CONTENT_TYPE_ITEM;
//		case USER_COLLECTION:
//			return DownloadAppData.User.CONTENT_TYPE;
//		case USER_SINGLE:
//			return DownloadAppData.User.CONTENT_TYPE_ITEM;
//		case ACCOUNT_COLLECTION:
//			return DownloadAppData.Account.CONTENT_TYPE;
//		case ACCOUNT_SINGLE:
//			return DownloadAppData.Account.CONTENT_TYPE_ITEM;
//		default:
//			throw new IllegalArgumentException("Unkonw uri:" + uri);
//		}
//	}
//
//	@Override
//	public Uri insert(Uri uri, ContentValues values) {
//		Log.v(TAG, "insert db");
//		mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
//		String table;
//		Uri contentUri;
//
//		switch (uriMatcher.match(uri)) {
//		case HISTORY_COLLECTION:
//		case HISTORY_SINGLE:
//			table = DownloadAppData.DownloadApp.TABLE_NAME;
//			contentUri = DownloadAppData.DownloadApp.CONTENT_URI;
//			break;
//
//		case TRAFFIC_STATICS_RX_SINGLE:
//		case TRAFFIC_STATICS_RX_COLLECTION:
//			table = DownloadAppData.TrafficStaticsRX.TABLE_NAME;
//			contentUri = DownloadAppData.TrafficStaticsRX.CONTENT_URI;
//			break;
//
//		case TRAFFIC_STATICS_TX_SINGLE:
//		case TRAFFIC_STATICS_TX_COLLECTION:
//			table = DownloadAppData.TrafficStaticsTX.TABLE_NAME;
//			contentUri = DownloadAppData.TrafficStaticsTX.CONTENT_URI;
//			break;
//
//		case USER_SINGLE:
//		case USER_COLLECTION:
//			table = DownloadAppData.User.TABLE_NAME;
//			contentUri = DownloadAppData.User.CONTENT_URI;
//			break;
//
//		case ACCOUNT_SINGLE:
//		case ACCOUNT_COLLECTION:
//			table = DownloadAppData.Account.TABLE_NAME;
//			contentUri = DownloadAppData.Account.CONTENT_URI;
//			break;
//		default:
//			throw new IllegalArgumentException("Unknow uri:" + uri);
//		}
//
//		long rowId = mSqLiteDatabase.insertWithOnConflict(table, "", values,
//				SQLiteDatabase.CONFLICT_REPLACE);
//		if (rowId > 0) {
//			Uri rowUri = ContentUris.withAppendedId(contentUri, rowId);
//			getContext().getContentResolver().notifyChange(uri, null);
//			Log.v(TAG, "insertDb.rowId=" + rowId);
//			return rowUri;
//		}
//		throw new IllegalArgumentException("Cannot insert into uri:" + uri);
//	}
//
//	@Override
//	public Cursor query(Uri uri, String[] projection, String selection,
//			String[] selectionArgs, String sortOrder) {
//		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
//
//		switch (uriMatcher.match(uri)) {
//		case HISTORY_COLLECTION:
//			qb.setTables(DownloadAppData.DownloadApp.TABLE_NAME);
//			break;
//		case HISTORY_SINGLE:
//			qb.setTables(DownloadAppData.DownloadApp.TABLE_NAME);
//			qb.appendWhere("_id=");
//			qb.appendWhere(uri.getPathSegments().get(1));
//			break;
//		case HISTORY_FILTER:
//			break;
//
//		case TRAFFIC_STATICS_RX_SINGLE:
//			qb.setTables(DownloadAppData.TrafficStaticsRX.TABLE_NAME);
//			qb.appendWhere("_id=" + uri.getPathSegments().get(1));
//			break;
//		case TRAFFIC_STATICS_RX_COLLECTION:
//			qb.setTables(DownloadAppData.TrafficStaticsRX.TABLE_NAME);
//			break;
//
//		case TRAFFIC_STATICS_TX_SINGLE:
//			qb.setTables(DownloadAppData.TrafficStaticsTX.TABLE_NAME);
//			qb.appendWhere("_id=" + uri.getPathSegments().get(1));
//			break;
//		case TRAFFIC_STATICS_TX_COLLECTION:
//			qb.setTables(DownloadAppData.TrafficStaticsTX.TABLE_NAME);
//			break;
//		case USER_SINGLE:
//			qb.setTables(DownloadAppData.User.TABLE_NAME);
//			qb.appendWhere("_id=" + uri.getPathSegments().get(1));
//			break;
//		case USER_COLLECTION:
//			qb.setTables(DownloadAppData.User.TABLE_NAME);
//			break;
//		case ACCOUNT_SINGLE:
//			qb.setTables(DownloadAppData.Account.TABLE_NAME);
//			qb.appendWhere("_id=" + uri.getPathSegments().get(1));
//			break;
//		case ACCOUNT_COLLECTION:
//			qb.setTables(DownloadAppData.Account.TABLE_NAME);
//			break;
//
//		default:
//			throw new IllegalArgumentException("Unknow uri:" + uri);
//		}
//
//		mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
//		Cursor ret = qb.query(mSqLiteDatabase, projection, selection,
//				selectionArgs, null, null, sortOrder);
//
//		if (ret != null) {
//			ret.setNotificationUri(getContext().getContentResolver(), uri);
//		}
//
//		return ret;
//	}
//
//	@Override
//	public int update(Uri uri, ContentValues values, String selection,
//			String[] selectionArgs) {
//		int match = uriMatcher.match(uri);
//		mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
//		String table;
//
//		switch (match) {
//		case HISTORY_SINGLE:
//			table = DownloadAppData.DownloadApp.TABLE_NAME;
//			selection = "_id=" + uri.getPathSegments().get(1);
//			selectionArgs = null;
//			break;
//		case HISTORY_COLLECTION:
//			table = DownloadAppData.DownloadApp.TABLE_NAME;
//			break;
//
//		case TRAFFIC_STATICS_RX_SINGLE:
//			table = DownloadAppData.TrafficStaticsRX.TABLE_NAME;
//			selection = "_id=" + uri.getPathSegments().get(1);
//			selectionArgs = null;
//			break;
//		case TRAFFIC_STATICS_RX_COLLECTION:
//			table = DownloadAppData.TrafficStaticsRX.TABLE_NAME;
//			break;
//		case TRAFFIC_STATICS_TX_SINGLE:
//			table = DownloadAppData.TrafficStaticsTX.TABLE_NAME;
//			selection = "_id=" + uri.getPathSegments().get(1);
//			selectionArgs = null;
//			break;
//		case TRAFFIC_STATICS_TX_COLLECTION:
//			table = DownloadAppData.TrafficStaticsTX.TABLE_NAME;
//			break;
//		case USER_SINGLE:
//			table = DownloadAppData.User.TABLE_NAME;
//			selection = "_id=" + uri.getPathSegments().get(1);
//			selectionArgs = null;
//			break;
//		case USER_COLLECTION:
//			table = DownloadAppData.User.TABLE_NAME;
//			break;
//		case ACCOUNT_SINGLE:
//			table = DownloadAppData.Account.TABLE_NAME;
//			selection = "_id=" + uri.getPathSegments().get(1);
//			selectionArgs = null;
//			break;
//		case ACCOUNT_COLLECTION:
//			table = DownloadAppData.Account.TABLE_NAME;
//			break;
//		default:
//			throw new UnsupportedOperationException("Cannot update uri:" + uri);
//		}
//		int count = mSqLiteDatabase.update(table, values, selection, null);
//		getContext().getContentResolver().notifyChange(uri, null);
//		return count;
//	}
//
//	private static class DatabaseHelper extends SQLiteOpenHelper {
//
//		public DatabaseHelper(Context context) {
//			super(context, DownloadAppData.DATABASE_NAME, null,
//					DownloadAppData.DATABASE_VERSION);
//			Log.d(TAG, "DatabaseHelper");
//		}
//
//		@Override
//		public void onCreate(SQLiteDatabase db) {
//			Log.d(TAG, "DatabaseHelper.onCreate");
//
//			// create history table
//			db.execSQL("create table " + DownloadAppData.DownloadApp.TABLE_NAME
//					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
//					+ DownloadAppData.DownloadApp.FILE_PATH + " TEXT, "
//					+ DownloadAppData.DownloadApp.FILE_NAME + " TEXT, "
//					+ DownloadAppData.DownloadApp.FILE_SIZE + " LONG, "
//					+ DownloadAppData.DownloadApp.SEND_USERNAME + " TEXT, "
//					+ DownloadAppData.DownloadApp.RECEIVE_USERNAME + " TEXT, "
//					+ DownloadAppData.DownloadApp.PROGRESS + " LONG, "
//					+ DownloadAppData.DownloadApp.DATE + " LONG, "
//					+ DownloadAppData.DownloadApp.STATUS + " INTEGER, "
//					+ DownloadAppData.DownloadApp.MSG_TYPE + " INTEGER, "
//					+ DownloadAppData.DownloadApp.FILE_TYPE + " INTEGER, "
//					+ DownloadAppData.DownloadApp.FILE_ICON + " BLOB, "
//					+ DownloadAppData.DownloadApp.SEND_USER_HEADID
//					+ " INTEGER, " + DownloadAppData.DownloadApp.SEND_USER_ICON
//					+ " BLOB);");
//
//			// create traffic statics rx table.
//			db.execSQL("create table "
//					+ DownloadAppData.TrafficStaticsRX.TABLE_NAME
//					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
//					+ DownloadAppData.TrafficStaticsRX.DATE + " TEXT UNIQUE, "
//					+ DownloadAppData.TrafficStaticsRX.TOTAL_RX_BYTES
//					+ " LONG);");
//
//			// create traffic statics tx table.
//			db.execSQL("create table "
//					+ DownloadAppData.TrafficStaticsTX.TABLE_NAME
//					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
//					+ DownloadAppData.TrafficStaticsTX.DATE + " TEXT UNIQUE, "
//					+ DownloadAppData.TrafficStaticsTX.TOTAL_TX_BYTES
//					+ " LONG);");
//
//			// create user table
//			db.execSQL("create table " + DownloadAppData.User.TABLE_NAME
//					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
//					+ DownloadAppData.User.USER_NAME + " TEXT, "
//					+ DownloadAppData.User.USER_ID + " INTEGER, "
//					+ DownloadAppData.User.HEAD_ID + " INTEGER, "
//					+ DownloadAppData.User.THIRD_LOGIN + " INTEGER, "
//					+ DownloadAppData.User.HEAD_DATA + " BLOB, "
//					+ DownloadAppData.User.IP_ADDR + " TEXT, "
//					+ DownloadAppData.User.STATUS + " INTEGER, "
//					+ DownloadAppData.User.TYPE + " INTEGER, "
//					+ DownloadAppData.User.SSID + " TEXT, "
//					+ DownloadAppData.User.NETWORK + " INTEGER, "
//					+ DownloadAppData.User.SIGNATURE + " TEXT);");
//
//			// create account table
//			db.execSQL("create table " + DownloadAppData.Account.TABLE_NAME
//					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
//					+ DownloadAppData.Account.USER_NAME + " TEXT, "
//					+ DownloadAppData.Account.HEAD_ID + " INTEGER, "
//					+ DownloadAppData.Account.HEAD_DATA + " BLOB, "
//					+ DownloadAppData.Account.ACCOUNT_ZHAOYAN + " TEXT, "
//					+ DownloadAppData.Account.PHONE_NUMBER + " TEXT, "
//					+ DownloadAppData.Account.EMAIL + " TEXT, "
//					+ DownloadAppData.Account.ACCOUNT_QQ + " TEXT, "
//					+ DownloadAppData.Account.ACCOUNT_RENREN + " TEXT, "
//					+ DownloadAppData.Account.ACCOUNT_SINA_WEIBO + " TEXT, "
//					+ DownloadAppData.Account.ACCOUNT_TENCENT_WEIBO + " TEXT, "
//					+ DownloadAppData.Account.SIGNATURE + " TEXT, "
//					+ DownloadAppData.Account.LOGIN_STATUS + " INTEGER, "
//					+ DownloadAppData.Account.TOURIST_ACCOUNT + " INTEGER, "
//					+ DownloadAppData.Account.LAST_LOGIN_TIME + " LONG);");
//		}
//
//		@Override
//		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			db.execSQL("DROP TABLE IF EXISTS "
//					+ DownloadAppData.DownloadApp.TABLE_NAME);
//			db.execSQL("DROP TABLE IF EXISTS "
//					+ DownloadAppData.TrafficStaticsRX.TABLE_NAME);
//			db.execSQL("DROP TABLE IF EXISTS "
//					+ DownloadAppData.TrafficStaticsTX.TABLE_NAME);
//			db.execSQL("DROP TABLE IF EXISTS "
//					+ DownloadAppData.User.TABLE_NAME);
//			db.execSQL("DROP TABLE IF EXISTS "
//					+ DownloadAppData.Account.TABLE_NAME);
//			onCreate(db);
//		}
//
//	}
//
//}
