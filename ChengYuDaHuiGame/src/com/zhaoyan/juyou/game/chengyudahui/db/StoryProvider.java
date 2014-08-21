package com.zhaoyan.juyou.game.chengyudahui.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.zhaoyan.communication.util.Log;
import com.zhaoyan.juyou.game.chengyudahui.DBConfig;
import com.zhaoyan.juyou.game.chengyudahui.db.StoryData.ItemColums;
import com.zhaoyan.juyou.game.chengyudahui.db.StoryData.TypeColums;
import com.zhaoyan.juyou.game.chengyudahui.db.WordData.WordColums;


/**
 * need to more,just modify for query
 * 
 * */
public class StoryProvider extends ContentProvider {
	private final String TAG = StoryProvider.class.getName();
	private static UriMatcher mUriMatcher;
	private SQLiteDatabase mSqLiteDatabase;
	private ChengyuDbHelper mChengyuDbHelper;
	
	public static final int BEAR_COLLECTION = 1;
	public static final int BEAR_SINGLE = 2;
	public static final int CHENGYU_COLLECTION = 3;
	public static final int CHENGYU_SINGLE = 4;
	public static final int SLEEP_COLLECTION = 5;
	public static final int SLEEP_SINGLE = 6;
	public static final int CHILD_COLLECTION = 7;
	public static final int CHILD_SINGLE = 8;
	public static final int FAIRY_TALE_COLLECTION = 9;
	public static final int FAIRY_TALE_SINGLE = 10;
	public static final int HISTORY_COLLECTION = 11;
	public static final int HISTORY_SINGLE = 12;
	public static final int GOLD_CAT_COLLECTION = 13;
	public static final int GOLD_CAT_SINGLE = 14;
	public static final int XIYOUJI_COLLECTION = 15;
	public static final int XIYOUJI_SINGLE = 16;
	public static final int CHILD_SONG_COLLECTION = 17;
	public static final int CHILD_SONG_SINGLE = 18;
	public static final int TYPE_COLLECTION = 19;
	public static final int TYPE_SINGLE = 20;
	
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(StoryData.AUTHORITY, TypeColums.TABLE_NAME, TYPE_COLLECTION);
		mUriMatcher.addURI(StoryData.AUTHORITY, TypeColums.TABLE_NAME + "/#", TYPE_SINGLE);
		
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.BEAR_TABLE_NAME, BEAR_COLLECTION);
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.BEAR_TABLE_NAME + "/#", BEAR_SINGLE);
		
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.CHENGYU_TABLE_NAME, CHENGYU_COLLECTION);
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.CHENGYU_TABLE_NAME + "/#", CHENGYU_SINGLE);
		
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.SLEEP_TABLE_NAME, SLEEP_COLLECTION);
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.SLEEP_TABLE_NAME + "/#", SLEEP_SINGLE);
		
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.CHILD_TABLE_NAME, CHILD_COLLECTION);
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.CHILD_TABLE_NAME + "/#", CHILD_SINGLE);
		
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.FAIRY_TALE_TABLE_NAME, FAIRY_TALE_COLLECTION);
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.FAIRY_TALE_TABLE_NAME + "/#", FAIRY_TALE_SINGLE);
		
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.HISTORY_TABLE_NAME, HISTORY_COLLECTION);
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.HISTORY_TABLE_NAME + "/#", HISTORY_SINGLE);
		
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.GOLD_CAT_TABLE_NAME, GOLD_CAT_COLLECTION);
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.GOLD_CAT_TABLE_NAME + "/#", GOLD_CAT_SINGLE);
		
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.XIYOUJI_TABLE_NAME, XIYOUJI_COLLECTION);
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.XIYOUJI_TABLE_NAME + "/#", XIYOUJI_SINGLE);
		
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.CHILD_SONG_TABLE_NAME, CHILD_SONG_COLLECTION);
		mUriMatcher.addURI(StoryData.AUTHORITY, ItemColums.CHILD_SONG_TABLE_NAME + "/#", CHILD_SONG_SINGLE);
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
		case TYPE_COLLECTION:
			builder.setTables(TypeColums.TABLE_NAME);
			break;
		case BEAR_COLLECTION:
			builder.setTables(ItemColums.BEAR_TABLE_NAME);
			break;
		case CHENGYU_COLLECTION:
			builder.setTables(ItemColums.CHENGYU_TABLE_NAME);
			break;
		case SLEEP_COLLECTION:
			builder.setTables(ItemColums.SLEEP_TABLE_NAME);
			break;
		case CHILD_COLLECTION:
			builder.setTables(ItemColums.CHILD_TABLE_NAME);
			break;
		case FAIRY_TALE_COLLECTION:
			builder.setTables(ItemColums.FAIRY_TALE_TABLE_NAME);
			break;
		case HISTORY_COLLECTION:
			builder.setTables(ItemColums.HISTORY_TABLE_NAME);
			break;
		case GOLD_CAT_COLLECTION:
			builder.setTables(ItemColums.GOLD_CAT_TABLE_NAME);
			break;
		case XIYOUJI_COLLECTION:
			builder.setTables(ItemColums.XIYOUJI_TABLE_NAME);
			break;
		case CHILD_SONG_COLLECTION:
			builder.setTables(ItemColums.CHILD_SONG_TABLE_NAME);
			break;
		default:
			Log.e(TAG, "Unknwo Uri:" + arg0);
			throw new IllegalArgumentException("Unknow Uri : " + arg0);
		}
		if (mSqLiteDatabase == null) {
			mSqLiteDatabase = mChengyuDbHelper.getReadDb(DBConfig.STORY_DB_PATH);
		}
//		Log.d(TAG, "query.buid:" + builder);
//		Log.d(TAG, "query.44dabatease:" + mSqLiteDatabase);
		return builder.query(mSqLiteDatabase, arg1, arg2, arg3, null, null,
				arg4);
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
