package com.zhaoyan.juyou.game.story;

import java.io.File;

public class Config {
	public static final String RES_FOLDER = "res/story";
	public static final String EXCEL_FILE_NAME = "story.xlsx";

	public static String getExcelFilePath() {
		return RES_FOLDER + File.separator + EXCEL_FILE_NAME;
	}

	public static final String OUT_FOLDER = "out/story";
	public static final String OUT_DB_FILE_NAME = "story.db";

	public static String getOutDatabasePath() {
		return OUT_FOLDER + File.separator + OUT_DB_FILE_NAME;
	}
}
