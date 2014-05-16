package com.zhaoyan.juyou.game.chengyu;

import java.io.FileNotFoundException;
import java.sql.SQLException;

/**
 * Convert ChengYu from chengyu.txt to chengyu.db.
 *
 */
public class MainClass {

	public static void main(String[] args) throws ClassNotFoundException {
		System.out.println("Create database.");
		ChengYuDatabase database = new ChengYuDatabase(
				Config.OUT_DATABASE_FILE_PATH);
		ChengYuWriter writer = new ChengYuWriter(Config.CHENGYU_FILE_PATH,
				database);
		try {
			System.out.println("Create tables in database");
			// create table in database.
			database.createTables();
			// write chengyu into database.
			System.out.println("write chengyu into database begin, please waite ......");
			long start = System.currentTimeMillis();
			writer.writeToDataBase();
			long end = System.currentTimeMillis();
			System.out.println("write chengyu into database finished, cost time " + (end - start) + " ms.");
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				database.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Finished");
	}

}
