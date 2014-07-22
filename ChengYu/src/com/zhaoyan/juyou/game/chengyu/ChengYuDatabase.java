package com.zhaoyan.juyou.game.chengyu;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.zhaoyan.juyou.common.BaseColumns;
import com.zhaoyan.juyou.common.SqliteDatabase;

public class ChengYuDatabase {
	private Connection mConnection;

	public ChengYuDatabase(String databaseFilePath) {
		mConnection = SqliteDatabase.connect(databaseFilePath);
	}

	/**
	 * Write all chengyu into database.
	 * 
	 * @param list
	 * @return
	 */
	public int writeIntoDatabase(ArrayList<ChengYu> list) {
		Statement statement = null;
		int id = 1;
		try {
			statement = createStatement();
			for (ChengYu chengyu : list) {
				insertChengYu(id, chengyu, statement);
				id++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return id - 1;
	}

	public ArrayList<ChengYu> readFromDataBase() {
		ArrayList<ChengYu> chengYuList = new ArrayList<ChengYu>();
		Statement statement = null;

		try {
			statement = createStatement();
			String sql = "select * from " + ChengYuTable.TABLE_NAME + ";";
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				chengYuList.add(readChengYu(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return chengYuList;
	}

	private ChengYu readChengYu(ResultSet resultSet) throws SQLException {
		ChengYu chengYu = new ChengYu();
		chengYu.comment = resultSet.getString(ChengYuTable.INDEX_COMMENT);
		chengYu.frequently = resultSet.getInt(ChengYuTable.INDEX_FREQUENTLY);
		chengYu.example = resultSet.getString(ChengYuTable.INDEX_EXAMPLE);
		chengYu.name = resultSet.getString(ChengYuTable.INDEX_NAME);
		chengYu.original = resultSet.getString(ChengYuTable.INDEX_ORIGINAL);
		chengYu.pinyin = resultSet.getString(ChengYuTable.INDEX_PINYIN);
		return chengYu;
	}

	private void insertChengYu(int id, ChengYu chengYu, Statement statement)
			throws SQLException {
		String sql = null;
		statement = createStatement();
		sql = "insert into " + ChengYuDatabase.ChengYuTable.TABLE_NAME
				+ " values(" + id + ", '" + chengYu.name + "','"
				+ chengYu.pinyin + "','" + chengYu.comment + "','"
				+ chengYu.original + "','" + chengYu.example + "',"
				+ chengYu.frequently + ");";
		try {
			statement.execute(sql);
		} catch (Exception e) {
			System.err.println("error sql = " + sql);
		}
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
		// first index is _id.
		public static final String NAME = "name";
		public static final int INDEX_NAME = 2;

		public static final String PINYIN = "pinyin";
		public static final int INDEX_PINYIN = 3;

		public static final String COMMENT = "comment";
		public static final int INDEX_COMMENT = 4;

		public static final String ORIGINAL = "original";
		public static final int INDEX_ORIGINAL = 5;

		public static final String EXAMPLE = "example";
		public static final int INDEX_EXAMPLE = 6;

		public static final String FREQUENTLY = "frequently";
		public static final int INDEX_FREQUENTLY = 7;

		/**
		 * create table SQL.
		 * 
		 * @return
		 */
		public static final String createTable() {
			return "create table " + TABLE_NAME
					+ " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
					+ " TEXT, " + PINYIN + " TEXT, " + COMMENT + " TEXT, "
					+ ORIGINAL + " TEXT, " + EXAMPLE + " TEXT, " + FREQUENTLY
					+ " INTEGER);";
		}

	}
}
