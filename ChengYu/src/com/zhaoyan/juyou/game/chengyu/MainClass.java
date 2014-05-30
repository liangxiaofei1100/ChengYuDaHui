package com.zhaoyan.juyou.game.chengyu;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Convert ChengYu from chengyu.txt to chengyu.db.
 * 
 */
public class MainClass {

	public static void main(String[] args) throws ClassNotFoundException,
			FileNotFoundException {
		// Read all from file.
		log("Read start...");
		long readStart = System.currentTimeMillis();
		ChengYuFile chengYuFile = new ChengYuFile(Config.CHENGYU_FILE_PATH);
		ArrayList<ChengYu> chengyuList = chengYuFile.readAll();
		long readEnd = System.currentTimeMillis();
		log("Read end. total: " + chengyuList.size() + ", cost time: "
				+ (readEnd - readStart) + " ms");
		// Check Similar and Opposite.
		log("Check Similar and Opposite start ");
		long checkStart = System.currentTimeMillis();
		boolean checkResult = chengYuFile.checkSimilarAndOpposite(chengyuList);
		long checkEnd = System.currentTimeMillis();
		log("Check Similar and Opposite end. checkResult " + checkResult
				+ ", cost time: " + (checkEnd - checkStart) + "ms.");

		// Write into database.
		log("Connect database.");
		ChengYuDatabase database = new ChengYuDatabase(
				Config.OUT_DATABASE_FILE_PATH);
		try {
			// For read test.
			boolean isWrite = true;
			if (isWrite) {
				// create table in database.
				log("Create database tables.");
				database.createTables();
				// write chengyu into database.
				log("Write to database start...");
				long start = System.currentTimeMillis();
				database.writeIntoDatabase(chengyuList);
				long end = System.currentTimeMillis();
				log("Write to database end. cost time: " + (end - start)
						+ " ms.");
			}
			log("Read from database start...");
			ArrayList<ChengYu> chengYu = database.readFromDataBase();
			log("Read from database end. total " + chengYu.size());
			chengYuFile.writeIntoFile(chengYu);
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				database.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		log("Finished.");
	}

	private static void log(String log) {
		System.out.println(log);
	}
}
