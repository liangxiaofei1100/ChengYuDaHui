package com.zhaoyan.juyou.game.knowledge;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.zhaoyan.juyou.common.BaseColumns;
import com.zhaoyan.juyou.common.Log;
import com.zhaoyan.juyou.common.SqliteDatabase;
import com.zhaoyan.juyou.game.knowledge.data.GuessPictureGame;
import com.zhaoyan.juyou.game.knowledge.data.GuoXueGame;
import com.zhaoyan.juyou.game.knowledge.data.JieLongGame;
import com.zhaoyan.juyou.game.knowledge.data.ShouJiYanKuaiGame;
import com.zhaoyan.juyou.game.knowledge.data.Stage;
import com.zhaoyan.juyou.game.knowledge.data.XiaoChuGame;

public class KnowledgeDatabase {
	private static final String TAG = KnowledgeDatabase.class.getSimpleName();

	private String mDatabaseFilePath;
	private Connection mConnection;

	public KnowledgeDatabase(String databaseFilePath) {
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

	public void addStage(List<Stage> stages) {
		for (Stage stage : stages) {
			addStage(stage);
		}
	}

	public void addStage(Stage stage) {
		String sql = "insert into " + StageTable.TABLE_NAME + " values("
				+ stage.id + ",'" + stage.stageNumber + "'," + stage.gameType
				+ "," + stage.gameId + ");";
		try {
			Statement statement = mConnection.createStatement();
			statement.execute(sql);
		} catch (Exception e) {
			Log.e(TAG, "error sql = " + sql + e);
		}
	}

	public void addGuessPicture(List<GuessPictureGame> games) {
		for (GuessPictureGame game : games) {
			addGuessPicture(game);
		}
	}

	public void addGuessPicture(GuessPictureGame game) {
		String sql = "insert into " + GuessPictureTable.TABLE_NAME + " values("
				+ game.id + ",'" + game.answerChengYu + "','"
				+ game.otherChengYu + "','" + game.picture + "');";
		try {
			Statement statement = mConnection.createStatement();
			statement.execute(sql);
		} catch (Exception e) {
			Log.e(TAG, "error sql = " + sql + e);
		}
	}

	public void addGuoXue(List<GuoXueGame> games) {
		for (GuoXueGame game : games) {
			addGuoXue(game);
		}
	}

	public void addGuoXue(GuoXueGame game) {
		String sql = "insert into " + GuoXueTable.TABLE_NAME + " values("
				+ game.id + ",'" + game.question + "','" + game.choice + "',"
				+ game.answer + ",'" + game.explain + "');";
		try {
			Statement statement = mConnection.createStatement();
			statement.execute(sql);
		} catch (Exception e) {
			Log.e(TAG, "error sql = " + sql + e);
		}
	}

	public void addXiaoChu(List<XiaoChuGame> games) {
		for (XiaoChuGame game : games) {
			addXiaoChu(game);
		}
	}

	public void addXiaoChu(XiaoChuGame game) {
		String sql = "insert into " + XiaoChuTable.TABLE_NAME + " values("
				+ game.id + ",'" + game.question + "');";
		try {
			Statement statement = mConnection.createStatement();
			statement.execute(sql);
		} catch (Exception e) {
			Log.e(TAG, "error sql = " + sql + e);
		}
	}

	public void addJieLong(List<JieLongGame> games) {
		for (JieLongGame game : games) {
			addJieLong(game);
		}
	}

	public void addJieLong(JieLongGame game) {
		String sql = "insert into " + JieLongTable.TABLE_NAME + " values("
				+ game.id + ",'" + game.question + "');";
		try {
			Statement statement = mConnection.createStatement();
			statement.execute(sql);
		} catch (Exception e) {
			Log.e(TAG, "error sql = " + sql + e);
		}
	}

	public void addShouJiYanKuai(List<ShouJiYanKuaiGame> games) {
		for (ShouJiYanKuaiGame game : games) {
			addShouJiYanKuai(game);
		}
	}

	public void addShouJiYanKuai(ShouJiYanKuaiGame game) {
		String sql = "insert into " + ShouJiYanKuaiTable.TABLE_NAME
				+ " values(" + game.id + ",'" + game.question + "');";
		try {
			Statement statement = mConnection.createStatement();
			statement.execute(sql);
		} catch (Exception e) {
			Log.e(TAG, "error sql = " + sql + e);
		}
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
