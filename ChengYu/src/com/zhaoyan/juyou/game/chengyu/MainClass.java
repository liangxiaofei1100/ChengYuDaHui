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
		log("Start...");
		// Read all from file.
		ArrayList<ChengYu> chengyuList = readFromExcelFile();
		// Write into database.
		writeIntoDataBase(chengyuList);
		log("Finished.");
	}

	private static ArrayList<ChengYu> readFromExcelFile() {
		log("Read start...");
		long readStart = System.currentTimeMillis();
		ChengYuExcelFile excelFile = new ChengYuExcelFile();
		ArrayList<ChengYu> chengyuList = excelFile
				.readAll(Config.CHENGYU_EXCEL_FILE_PATH);
		long readEnd = System.currentTimeMillis();
		log("Read end. total: " + chengyuList.size() + ", cost time: "
				+ (readEnd - readStart) + " ms");
		// Check Similar and Opposite.
//		log("Check Similar and Opposite start ");
//		long checkStart = System.currentTimeMillis();
//		boolean checkResult = ChengYuFile.checkSimilarAndOpposite(chengyuList);
//		long checkEnd = System.currentTimeMillis();
//		log("Check Similar and Opposite end. checkResult " + checkResult
//				+ ", cost time: " + (checkEnd - checkStart) + "ms.");
		return chengyuList;
	}

	private static void writeIntoDataBase(ArrayList<ChengYu> chengYus) {
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
				database.writeIntoDatabase(chengYus);
				long end = System.currentTimeMillis();
				log("Write to database end. cost time: " + (end - start)
						+ " ms.");
			}
			log("Read from database start...");
			ArrayList<ChengYu> chengYu = database.readFromDataBase();
			log("Read from database end. total " + chengYu.size());
			ChengYuExcelFile chengYuExcelFile = new ChengYuExcelFile();
			try {
				chengYuExcelFile.writeIntoExcel(Config.OUT_EXCEL_FILE_PATH, chengYu);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {
				database.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void log(String log) {
		System.out.println(log);
	}
}
