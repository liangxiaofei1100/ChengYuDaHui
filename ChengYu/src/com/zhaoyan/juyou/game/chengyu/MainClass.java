package com.zhaoyan.juyou.game.chengyu;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import com.zhaoyan.juyou.common.pinyin.PinYinUtil;

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
		// Get all caici chengyu.
		ArrayList<String> caiciChengYuList = getCaiCiChengYuFromExcelFile();
		log("CaiCi ChengYu start...");
		boolean isChengYuExist = false;
		for (String caiciChengYu : caiciChengYuList) {
			for (ChengYu chengYu : chengyuList) {
				if (chengYu.name.equals(caiciChengYu)) {
					chengYu.caici = 1;
					isChengYuExist = true;
					break;
				}
			}
			if (!isChengYuExist) {
				log("CaiCi ChengYu not exist " + caiciChengYu);
			}
			isChengYuExist = false;
		}
		log("CaiCi ChengYu end.");
		// Write into database.
		writeIntoDataBase(chengyuList);
		log("Finished.");
	}

	private static ArrayList<String> getCaiCiChengYuFromExcelFile() {
		log("getCaiCiChengYuFromExcelFile start...");
		long start = System.currentTimeMillis();
		ChengYuExcelFile excelFile = new ChengYuExcelFile();
		ArrayList<String> caiciList = excelFile
				.readAllCaiCi(Config.CHENGYU_EXCEL_FILE_PATH);
		long end = System.currentTimeMillis();
		log("getCaiCiChengYuFromExcelFile end. total " + caiciList.size()
				+ ", cost time: " + (end - start) + " ms");
		return caiciList;
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
