package com.zhaoyan.juyou.game.knowledge;

import java.io.File;

public class Config {
	public static final String RES_FOLDER = "res/knowledge";
	public static final String EXCEL_FILE_NAME = "knowledge.xlsx";

	public static String getExcelFilePath() {
		return RES_FOLDER + File.separator + EXCEL_FILE_NAME;
	}

	public static final String OUT_FOLDER = "out/knowledge";
	public static final String OUT_DB_FILE_NAME = "knowledage.db";

	public static String getOutDatabasePath() {
		return OUT_FOLDER + File.separator + OUT_DB_FILE_NAME;
	}
}
