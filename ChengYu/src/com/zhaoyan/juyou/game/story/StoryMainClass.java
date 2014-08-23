package com.zhaoyan.juyou.game.story;

import java.sql.SQLException;
import java.util.List;

import com.zhaoyan.juyou.common.Log;
import com.zhaoyan.juyou.game.story.StoryDatabase.StoryItemTable;
import com.zhaoyan.juyou.game.story.StoryExcel.StorySheet;

public class StoryMainClass {
	private static final String TAG = StoryMainClass.class.getSimpleName();

	public static void main(String[] args) {
		Log.d(TAG, "Start");
		Log.d(TAG, "Read start...");
		StoryExcel storyExcel = new StoryExcel(Config.getExcelFilePath());
		storyExcel.open();

		List<Type> types = storyExcel.readTypes();
		Log.d(TAG, "Read stage finished. stage total number: " + types.size());

		List<StoryData> bearDatas = storyExcel.readStoryDatas(StorySheet.BEAR_SHEET_NAME);
		Log.d(TAG, "Read bearDatas finished. total number: "+ bearDatas.size());

		List<StoryData> chengyuDatas = storyExcel.readStoryDatas(StorySheet.CHENGYU_SHEET_NAME);
		Log.d(TAG, "Read chengyuDatas finished. total number: "+ chengyuDatas.size());

		List<StoryData> sleepDatas = storyExcel.readStoryDatas(StorySheet.SLEEP_SHEET_NAME);
		Log.d(TAG, "Read sleepDatas finished. total number: "+ sleepDatas.size());

		List<StoryData> childDatas = storyExcel.readStoryDatas(StorySheet.CHILD_SHEET_NAME);
		Log.d(TAG, "Read childDatas finished. total number: "+ childDatas.size());

		List<StoryData> fairyDatas = storyExcel.readStoryDatas(StorySheet.FAIRY_TALE_SHEET_NAME);
		Log.d(TAG, "Read fairyDatas finished. total number: "+ fairyDatas.size());
		
		List<StoryData> historyWorldDatas = storyExcel.readStoryDatas(StorySheet.HISTORY_WORLD_SHEET_NAME);
		Log.d(TAG, "Read historyWorldDatas finished. total number: "+ historyWorldDatas.size());
		
		List<StoryData> goldcatDatas = storyExcel.readStoryDatas(StorySheet.GOLDCAT_SHEET_NAME);
		Log.d(TAG, "Read bearDatas finished. total number: "+ goldcatDatas.size());
		
		List<StoryData> xiyoujiDatas = storyExcel.readStoryDatas(StorySheet.XIYOUJI_SHEET_NAME);
		Log.d(TAG, "Read xiyoujiDatas finished. total number: "+ xiyoujiDatas.size());
		
		List<StoryData> childSongDatas = storyExcel.readStoryDatas(StorySheet.CHILD_SONG_SHEET_NAME);
		Log.d(TAG, "Read childSongDatas finished. total number: "+ childSongDatas.size());
		
		List<StoryData> historyCNDatas = storyExcel.readStoryDatas(StorySheet.HISTORY_CN_SHEET_NAME);
		Log.d(TAG, "Read historyCNDatas finished. total number: "+ historyCNDatas.size());

		storyExcel.close();
		Log.d(TAG, "Read end.");
		Log.d(TAG, "Write start...");
		StoryDatabase database = new StoryDatabase(
				Config.getOutDatabasePath());
		database.open();
		try {
			database.createTables();
//			database.addTypes(types);
//			System.out.println("==========start bear==============");
//			database.addStoryItem(bearDatas, StoryItemTable.BEAR_TABLE_NAME);
//			System.out.println("==========start chengyu==============");
//			database.addStoryItem(chengyuDatas, StoryItemTable.CHENGYU_TABLE_NAME);
//			System.out.println("==========start sleep==============");
//			database.addStoryItem(sleepDatas, StoryItemTable.SLEEP_TABLE_NAME);
//			System.out.println("==========start child==============");
//			database.addStoryItem(childDatas, StoryItemTable.CHILD_TABLE_NAME);
//			System.out.println("==========start fairy==============");
//			database.addStoryItem(fairyDatas, StoryItemTable.FAIRY_TALE_TABLE_NAME);
//			System.out.println("==========start history world==============");
//			database.addStoryItem(historyWorldDatas, StoryItemTable.HISTORY_WORLD_TABLE_NAME);
//			System.out.println("==========start goldcat==============");
//			database.addStoryItem(goldcatDatas, StoryItemTable.GOLD_CAT_TABLE_NAME);
			System.out.println("==========start xiyouji==============");
			database.addStoryItem(xiyoujiDatas, StoryItemTable.XIYOUJI_TABLE_NAME);
//			System.out.println("==========start childsong==============");
//			database.addStoryItem(childSongDatas, StoryItemTable.CHILD_SONG_TABLE_NAME);
//			System.out.println("==========start history cn==============");
//			database.addStoryItem(historyCNDatas, StoryItemTable.HISTORY_CN_TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		database.close();
		Log.d(TAG, "Write end.");
		Log.d(TAG, "End.");
	}
}
