package com.zhaoyan.juyou.game.knowledge;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.zhaoyan.juyou.common.BaseColumns;
import com.zhaoyan.juyou.common.SqliteDatabase;

public class KnowledgeDatabase {

	private Connection mConnection;

	public KnowledgeDatabase(String databaseFilePath) {
		mConnection = SqliteDatabase.connect(databaseFilePath);
	}
	
	

	public void createTables() throws SQLException {
		Statement statement = mConnection.createStatement();
		statement.execute("drop table if exists " + StageTable.TABLE_NAME);
		statement.execute(StageTable.createTable());

		statement.execute("drop table if exists "
				+ GuessPictureTable.TABLE_NAME);
		statement.execute(GuessPictureTable.createTable());

		statement.execute("drop table if exists " + GuoXueTable.TABLE_NAME);
		statement.execute(GuoXueTable.createTable());

		statement.execute("drop table if exists " + XiaoChuTable.TABLE_NAME);
		statement.execute(XiaoChuTable.createTable());

		statement.execute("drop table if exists " + JieLongTable.TABLE_NAME);
		statement.execute(JieLongTable.createTable());

		statement.execute("drop table if exists "
				+ ShouJiYanKuaiTable.TABLE_NAME);
		statement.execute(ShouJiYanKuaiTable.createTable());
		statement.close();
	}

	public static class StageTable implements BaseColumns {
		public static final String TABLE_NAME = "stage";

		public static final String NUMBER = "number";
		public static final int INDEX_NUMBER = 2;

		public static final String TYPE = "type";
		public static final int INDEX_TYPE = 3;

		public static final String GAME_ID = "game_id";
		public static final int INDEX_GAME_ID = 4;

		/**
		 * create table SQL.
		 * 
		 * @return
		 */
		public static final String createTable() {
			return "create table " + TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER
					+ " TEXT, " + TYPE + " INTEGER, " + GAME_ID + " INTEGER);";
		}
	}

	public static class GuessPictureTable implements BaseColumns {
		public static final String TABLE_NAME = "guess_picture";

		public static final String ANSWER = "answer";
		public static final int INDEX_ANSWER = 2;

		public static final String OTHER = "other";
		public static final int INDEX_OTHER = 3;

		public static final String PICTURE = "picture";
		public static final int INDEX_PICTURE = 4;

		/**
		 * create table SQL.
		 * 
		 * @return
		 */
		public static final String createTable() {
			return "create table " + TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + ANSWER
					+ " TEXT, " + OTHER + " TEXT, " + PICTURE + " TEXT);";
		}
	}

	public static class GuoXueTable implements BaseColumns {
		public static final String TABLE_NAME = "guoxue";

		public static final String QUESTION = "question";
		public static final int INDEX_QUESTION = 2;

		public static final String CHOICE = "choice";
		public static final int INDEX_CHOICE = 3;

		public static final String ANSWER = "answer";
		public static final int INDEX_ANSWER = 4;

		public static final String EXPLAIN = "explain";
		public static final int INDEX_EXPLAIN = 5;

		/**
		 * create table SQL.
		 * 
		 * @return
		 */
		public static final String createTable() {
			return "create table " + TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + QUESTION
					+ " TEXT, " + CHOICE + " TEXT, " + ANSWER + " INTEGER, "
					+ EXPLAIN + " TEXT);";
		}
	}

	public static class XiaoChuTable implements BaseColumns {
		public static final String TABLE_NAME = "xiaochu";

		public static final String QUESTION = "question";
		public static final int INDEX_QUESTION = 2;

		/**
		 * create table SQL.
		 * 
		 * @return
		 */
		public static final String createTable() {
			return "create table " + TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + QUESTION
					+ " TEXT);";
		}
	}

	public static class JieLongTable implements BaseColumns {
		public static final String TABLE_NAME = "jielong";

		public static final String QUESTION = "question";
		public static final int INDEX_QUESTION = 2;

		/**
		 * create table SQL.
		 * 
		 * @return
		 */
		public static final String createTable() {
			return "create table " + TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + QUESTION
					+ " TEXT);";
		}
	}

	public static class ShouJiYanKuaiTable implements BaseColumns {
		public static final String TABLE_NAME = "shoujiyankuai";

		public static final String QUESTION = "question";
		public static final int INDEX_QUESTION = 2;

		/**
		 * create table SQL.
		 * 
		 * @return
		 */
		public static final String createTable() {
			return "create table " + TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + QUESTION
					+ " TEXT);";
		}
	}

}
