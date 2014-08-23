package com.zhaoyan.juyou.game.story;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.zhaoyan.juyou.common.BaseColumns;
import com.zhaoyan.juyou.common.Log;
import com.zhaoyan.juyou.common.SqliteDatabase;

public class StoryDatabase {
	private static final String TAG = StoryDatabase.class.getSimpleName();

	private String mDatabaseFilePath;
	private Connection mConnection;

	public StoryDatabase(String databaseFilePath) {
		mDatabaseFilePath = databaseFilePath;
		File file = new File(databaseFilePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
	}

	public void open() {
		mConnection = SqliteDatabase.connect(mDatabaseFilePath);
	}

	public void close() {
		try {
			mConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addTypes(List<Type> types) {
		for (Type type : types) {
			addTypes(type);
		}
	}

	public void addTypes(Type type) {
		String sql = "insert into " + TypeTable.TABLE_NAME + " values("
				+ type.id + ","
				+ type.typeNumber + ",'" + type.typeName
				+ "','" + type.folder + "','"
				+ type.tableName + "');";
		try {
			Statement statement = mConnection.createStatement();
			statement.execute(sql);
		} catch (Exception e) {
			Log.e(TAG, "error sql = " + sql + e);
		}
	}

	public void addStoryItem(List<StoryData> datas, String tableName) {
		System.out.println("addStoryItem.begin:" + tableName);
		for (StoryData data : datas) {
			addStoryItem(data, tableName);
		}
		System.out.println("addStoryItem.end:" + tableName);
	}

	public void addStoryItem(StoryData data, String tableName) {
		String sql = "insert into " + tableName + " values("
				+ data.id + ","
				+ data.type + ",'" + data.title + "','" + data.fileName + "'," + data.size + ","
				+ data.duration + ");";
		try {
			Statement statement = mConnection.createStatement();
			statement.execute(sql);
		} catch (Exception e) {
			Log.e(TAG, "error sql = " + sql + e);
		}
	}

	public void createTables() throws SQLException {
		Statement statement = mConnection.createStatement();
//		statement.execute("drop table if exists " + TypeTable.TABLE_NAME);
//		statement.execute(TypeTable.createTable());
//
//		statement.execute("drop table if exists " + StoryItemTable.BEAR_TABLE_NAME);
//		statement.execute(StoryItemTable.createTable(StoryItemTable.BEAR_TABLE_NAME));
//
//		statement.execute("drop table if exists " + StoryItemTable.CHENGYU_TABLE_NAME);
//		statement.execute(StoryItemTable.createTable(StoryItemTable.CHENGYU_TABLE_NAME));
//
//		statement.execute("drop table if exists " + StoryItemTable.SLEEP_TABLE_NAME);
//		statement.execute(StoryItemTable.createTable(StoryItemTable.SLEEP_TABLE_NAME));
//
//		statement.execute("drop table if exists " + StoryItemTable.CHILD_TABLE_NAME);
//		statement.execute(StoryItemTable.createTable(StoryItemTable.CHILD_TABLE_NAME));
//
//		statement.execute("drop table if exists " + StoryItemTable.FAIRY_TALE_TABLE_NAME);
//		statement.execute(StoryItemTable.createTable(StoryItemTable.FAIRY_TALE_TABLE_NAME));
//		
//		statement.execute("drop table if exists " + StoryItemTable.HISTORY_WORLD_TABLE_NAME);
//		statement.execute(StoryItemTable.createTable(StoryItemTable.HISTORY_WORLD_TABLE_NAME));
//		
//		statement.execute("drop table if exists " + StoryItemTable.GOLD_CAT_TABLE_NAME);
//		statement.execute(StoryItemTable.createTable(StoryItemTable.GOLD_CAT_TABLE_NAME));
		
		statement.execute("drop table if exists " + StoryItemTable.XIYOUJI_TABLE_NAME);
		statement.execute(StoryItemTable.createTable(StoryItemTable.XIYOUJI_TABLE_NAME));
		
//		statement.execute("drop table if exists " + StoryItemTable.CHILD_SONG_TABLE_NAME);
//		statement.execute(StoryItemTable.createTable(StoryItemTable.CHILD_SONG_TABLE_NAME));
//		
//		statement.execute("drop table if exists " + StoryItemTable.HISTORY_CN_TABLE_NAME);
//		statement.execute(StoryItemTable.createTable(StoryItemTable.HISTORY_CN_TABLE_NAME));
		statement.close();
	}

	public static class TypeTable implements BaseColumns {
		public static final String TABLE_NAME = "type";

		public static final String TYPE = "type";
		public static final int INDEX_TYPE = 2;

		public static final String NAME = "name";
		public static final int INDEX_NAME = 3;

		public static final String FOLDER = "folder";
		public static final int INDEX_FOLDER = 4;
		
		public static final String TABLENAME = "tablename";
		public static final int INDEX_TABLENAME = 5;

		/**
		 * create table SQL.
		 * @return
		 */
		public static final String createTable() {
			return "create table " + TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + TYPE
					+ " INTEGER, " + NAME + " TEXT, " + FOLDER + " TEXT, "
					+ TABLENAME + ");";
		}
	}

	public static class StoryItemTable implements BaseColumns {
		public static final String BEAR_TABLE_NAME = "bear";
		public static final String CHENGYU_TABLE_NAME = "chengyu";
		public static final String SLEEP_TABLE_NAME = "sleep";
		public static final String CHILD_TABLE_NAME = "child";
		public static final String FAIRY_TALE_TABLE_NAME = "fairytale";
		public static final String HISTORY_WORLD_TABLE_NAME = "historyworld";
		public static final String GOLD_CAT_TABLE_NAME = "goldcat";
		public static final String XIYOUJI_TABLE_NAME = "xiyouji";
		public static final String CHILD_SONG_TABLE_NAME = "childsong";
		public static final String HISTORY_CN_TABLE_NAME = "historycn";

		public static final String TYPE = "type";
		public static final int INDEX_TYPE = 2;

		public static final String TITLE = "title";
		public static final int INDEX_title = 3;

		public static final String FILENAME = "filename";
		public static final int INDEX_FILENAME = 4;
		
		public static final String SIZE = "size";
		public static final int INDEX_SIZE = 5;
		
		public static final String DURATION = "duration";
		public static final int INDEX_DURATION = 6;
		
		/**
		 * create table SQL.
		 * @return
		 */
		public static final String createTable(String tablename) {
			return "create table " + tablename
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + TYPE
					+ " INTEGER, " + TITLE + " TEXT, " + FILENAME + " TEXT, " + SIZE + " LONG, " + DURATION + " INTEGER);";
		}
	}

}
