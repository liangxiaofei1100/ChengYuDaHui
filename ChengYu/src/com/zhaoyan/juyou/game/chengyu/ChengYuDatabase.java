package com.zhaoyan.juyou.game.chengyu;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.zhaoyan.juyou.common.BaseColumns;
import com.zhaoyan.juyou.common.SqliteDatabase;

public class ChengYuDatabase {
	private Connection mConnection;

	public ChengYuDatabase(String databaseFilePath) {
		mConnection = SqliteDatabase.connect(databaseFilePath);
	}
	
	public Connection getConnection() {
		return mConnection;
	}
	
	public Statement createStatement() throws SQLException {
		return mConnection.createStatement();
	}

	public void close() throws SQLException {
		mConnection.close();
	}

	public void createTables() throws SQLException {
		Statement statement = mConnection.createStatement();
		createChengYuTable(statement);
		statement.close();
	}

	private void createChengYuTable(Statement statement) throws SQLException {
		statement.execute("drop table if exists " + ChengYuTable.TABLE_NAME);
		statement.execute(ChengYuTable.createTable());
	}

	/**
	 * Table column names, used for database.
	 * 
	 */
	public static class ChengYuTable implements BaseColumns {
		public static final String TABLE_NAME = "chengyu";

		// columns
		public static final String NAME = "name";

		public static final String PINYIN = "pinyin";

		public static final String COMMENT = "comment";

		public static final String ORIGINAL = "original";

		public static final String EXAMPLE = "example";

		/**
		 * create table SQL.
		 * 
		 * @return
		 */
		public static final String createTable() {
			return "create table " + TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
					+ " TEXT, " + PINYIN + " TEXT, " + COMMENT + " TEXT, "
					+ ORIGINAL + " TEXT, " + EXAMPLE + " TEXT);";
		}

	}
}
