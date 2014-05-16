package com.zhaoyan.juyou.common;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Provide SQLite database operations using sqlite JDBC.
 *
 */
public class SqliteDatabase {
	static {
		try {
			// load the sqlite-JDBC driver using the current class loader
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			System.err.println("load the sqlite-JDBC driver error.");
			e.printStackTrace();
		}
	}

	/**
	 * Connect sqlite database.
	 * @param databaseFilePath
	 * @return
	 */
	public static Connection connect(String databaseFilePath) {
		Connection connection = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:"
					+ databaseFilePath);
		} catch (Exception e) {
			System.err.println("create a database connection error e");
			e.printStackTrace();
		}
		return connection;
	}

}
