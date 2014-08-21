package com.zhaoyan.juyou.game.chengyudahui;


public class DBConfig {

	public static final String PACKAGE_NAME = "com.zhaoyan.juyou.game.chengyudahui";
	
	public static final String DB_CHENGYU_NAME = "chengyu.db";
	public static final String DB_WORD_NAME = "word.db";
	public static final String DB_GUOXUE_NAME = "guoxue.db";
	public static final String DB_DICTATE_NAME = "dictate.db";
	public static final String DB_STORY_NAME = "story.db";
	
	public static final String FILES_DIR = "/data/data/" + PACKAGE_NAME + "/files";
	public static final String DATABASE_DIR = FILES_DIR + "/database";
	
	public static final String WORD_DB_PATH = DATABASE_DIR + "/" + DB_WORD_NAME;
	public static final String GUOXUE_DB_PATH =  DATABASE_DIR + "/" + DB_GUOXUE_NAME;
	public static final String CHENGYU_DB_PATH =  DATABASE_DIR + "/" + DB_CHENGYU_NAME;
	public static final String DICTATE_DB_PATH = DATABASE_DIR + "/" + DB_DICTATE_NAME;
	public static final String STORY_DB_PATH = DATABASE_DIR + "/" + DB_STORY_NAME;
	
	//story start
	public static final int STORY_BEAR = 1;
	public static final int STORY_CHENGYU = 2;
	public static final int STORY_SLEEP = 3;
	public static final int STORY_CHILD = 4;
	public static final int STORY_FAIRY_TALE = 5;
	public static final int STORY_HISTORY = 6;
	public static final int STORY_GOLD_CAT = 7;
	public static final int STORY_XIYOUJI = 8;
	public static final int STORY_CHILD_SONG = 9;
	//story end
	
	//test
	public static final String FILE_KNOWLEDGE1 = "knowledge1.xml";
	public static final String kNOWLEDGE_FILES1 = FILES_DIR + "/knowledge1.xml";
	//test

}
