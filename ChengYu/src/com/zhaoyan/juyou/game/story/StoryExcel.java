package com.zhaoyan.juyou.game.story;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.zhaoyan.juyou.common.Log;

public class StoryExcel {
	private static final String TAG = StoryExcel.class.getSimpleName();

	private String mExcelFilePath;
	private XSSFWorkbook mWorkbook;
	private InputStream mInputStream;

	public StoryExcel(String excelFilePath) {
		mExcelFilePath = excelFilePath;
	}

	public void open() {
		try {
			mInputStream = new FileInputStream(new File(mExcelFilePath));
			mWorkbook = new XSSFWorkbook(mInputStream);
		} catch (FileNotFoundException e1) {
			Log.e(TAG, "open() error " + e1);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void close() {
		if (mInputStream != null) {
			try {
				mInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mWorkbook = null;
	}

	public List<Type> readTypes() {
		ArrayList<Type> types = new ArrayList<>();
		try {
			XSSFSheet sheet = mWorkbook.getSheet(TypeSheet.SHEET_NAME);
			XSSFRow row;
			XSSFCell cell;
			Type type;
			for (int i = sheet.getFirstRowNum() + 1; i < sheet
					.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				type = new Type();
				
				cell = row.getCell(TypeSheet.COLUMN_ID_INDEX);
				type.id = (int) cell.getNumericCellValue();

				cell = row.getCell(TypeSheet.COLUMN_TYPE_INDEX);
				type.typeNumber = (int) cell.getNumericCellValue();

				cell = row.getCell(TypeSheet.COLUMN_NAME_INDEX);
				type.typeName = cell.getStringCellValue();

				cell = row.getCell(TypeSheet.COLUMN_FOLDER_INDEX);
				type.folder = cell.getStringCellValue();
				
				cell = row.getCell(TypeSheet.COLUMN_TABLENAME_INDEX);
				type.tableName = cell.getStringCellValue();

				types.add(type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return types;
	}

	public List<StoryData> readStoryDatas(String sheetName) {
		ArrayList<StoryData> storyDatas = new ArrayList<>();
		try {
			XSSFSheet sheet = mWorkbook.getSheet(sheetName);
			XSSFRow row;
			XSSFCell cell;
			StoryData storyData;
			for (int i = sheet.getFirstRowNum() + 1; i < sheet
					.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				storyData = new StoryData();
				
				cell = row.getCell(StorySheet.COLUMN_ID_INDEX);
				storyData.id = (int) cell.getNumericCellValue();

				cell = row
						.getCell(StorySheet.COLUMN_TYPE_INDEX);
				storyData.type = (int) cell.getNumericCellValue();

				cell = row
						.getCell(StorySheet.COLUMN_TITLE_INDEX);
				storyData.title = cell.getStringCellValue();

				cell = row.getCell(StorySheet.COLUMN_FILENAME_INDEX);
				storyData.fileName = cell.getStringCellValue();
				
				cell = row.getCell(StorySheet.COLUMN_SIZE_INDEX);
				storyData.size = (long) cell.getNumericCellValue();

				//set duration 0
				storyData.duration = 0;
				storyDatas.add(storyData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return storyDatas;
	}

	class TypeSheet {
		public static final String SHEET_NAME = "类型";

		public static final String COLUMN_ID = "ID";
		public static final int COLUMN_ID_INDEX = 0;

		public static final String COLUMN_TYPE = "类型编号";
		public static final int COLUMN_TYPE_INDEX = 1;

		public static final String COLUMN_NAME = "类型名称";
		public static final int COLUMN_NAME_INDEX = 2;

		public static final String COLUMN_FOLDER = "存放目录名称";
		public static final int COLUMN_FOLDER_INDEX = 3;
		
		public static final String COLUMN_TABLENAME = "表名称";
		public static final int COLUMN_TABLENAME_INDEX = 4;
	}

	class StorySheet {
		public static final String BEAR_SHEET_NAME = "小熊维尼晚安故事";
		public static final String CHENGYU_SHEET_NAME = "成语故事";
		public static final String SLEEP_SHEET_NAME = "睡前故事";
		public static final String CHILD_SHEET_NAME = "幼儿故事";
		public static final String FAIRY_TALE_SHEET_NAME = "世界童话故事";
		public static final String HISTORY_WORLD_SHEET_NAME = "世界历史故事";
		public static final String GOLDCAT_SHEET_NAME = "金猫传奇";
		public static final String XIYOUJI_SHEET_NAME = "西游记";
		public static final String CHILD_SONG_SHEET_NAME = "儿童歌曲大全";
		public static final String HISTORY_CN_SHEET_NAME = "中国历史故事";

		public static final String COLUMN_ID = "ID";
		public static final int COLUMN_ID_INDEX = 0;

		public static final String COLUMN_TYPE = "类型编号";
		public static final int COLUMN_TYPE_INDEX = 1;

		public static final String COLUMN_TITLE = "标题";
		public static final int COLUMN_TITLE_INDEX = 2;

		public static final String COLUMN_FILENAME = "文件名";
		public static final int COLUMN_FILENAME_INDEX = 3;
		
		public static final String COLUMN_SIZE = "文件大小";
		public static final int COLUMN_SIZE_INDEX = 4;
	}

}
