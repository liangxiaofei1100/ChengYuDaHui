package com.zhaoyan.juyou.game.knowledge;

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
import com.zhaoyan.juyou.game.knowledge.data.GuessPictureGame;
import com.zhaoyan.juyou.game.knowledge.data.GuoXueGame;
import com.zhaoyan.juyou.game.knowledge.data.JieLongGame;
import com.zhaoyan.juyou.game.knowledge.data.ShouJiYanKuaiGame;
import com.zhaoyan.juyou.game.knowledge.data.Stage;
import com.zhaoyan.juyou.game.knowledge.data.XiaoChuGame;

public class KnowledgeExcel {
	private static final String TAG = KnowledgeExcel.class.getSimpleName();

	private String mExcelFilePath;
	private XSSFWorkbook mWorkbook;
	private InputStream mInputStream;

	public KnowledgeExcel(String excelFilePath) {
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

	public List<Stage> readStage() {
		ArrayList<Stage> stages = new ArrayList<>();
		try {
			XSSFSheet sheet = mWorkbook.getSheet(GuanKaSheet.SHEET_NAME);
			XSSFRow row;
			XSSFCell cell;
			Stage stage;
			for (int i = sheet.getFirstRowNum() + 1; i < sheet
					.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				stage = new Stage();

				cell = row.getCell(GuanKaSheet.COLUMN_GUANKAID_INDEX);
				stage.id = (int) cell.getNumericCellValue();

				cell = row.getCell(GuanKaSheet.COLUMN_GUANKAHUIHE_INDEX);
				stage.stageNumber = cell.getStringCellValue();

				cell = row.getCell(GuanKaSheet.COLUMN_YOUXILEIXING_INDEX);
				stage.gameType = (int) cell.getNumericCellValue();

				cell = row.getCell(GuanKaSheet.COLUMN_YOUXIID_INDEX);
				stage.gameId = (int) cell.getNumericCellValue();

				stages.add(stage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return stages;
	}

	public List<GuessPictureGame> readGuessPicture() {
		ArrayList<GuessPictureGame> games = new ArrayList<>();
		try {
			XSSFSheet sheet = mWorkbook
					.getSheet(KanTuCaiChengYuSheet.SHEET_NAME);
			XSSFRow row;
			XSSFCell cell;
			GuessPictureGame game;
			for (int i = sheet.getFirstRowNum() + 1; i < sheet
					.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				game = new GuessPictureGame();

				cell = row.getCell(KanTuCaiChengYuSheet.COLUMN_ID_INDEX);
				game.id = (int) cell.getNumericCellValue();

				cell = row
						.getCell(KanTuCaiChengYuSheet.COLUMN_DAANCHENGYU_INDEX);
				game.answerChengYu = cell.getStringCellValue();

				cell = row
						.getCell(KanTuCaiChengYuSheet.COLUMN_QITACHENGYU_INDEX);
				game.otherChengYu = cell.getStringCellValue();

				cell = row.getCell(KanTuCaiChengYuSheet.COLUMN_TUPIAN_INDEX);
				game.picture = cell.getStringCellValue();

				games.add(game);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return games;
	}

	public List<GuoXueGame> readGuoXue() {
		ArrayList<GuoXueGame> games = new ArrayList<>();
		try {
			XSSFSheet sheet = mWorkbook.getSheet(GuoXueSheet.SHEET_NAME);
			XSSFRow row;
			XSSFCell cell;
			GuoXueGame game;
			for (int i = sheet.getFirstRowNum() + 1; i < sheet
					.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				game = new GuoXueGame();
				
				cell = row.getCell(GuoXueSheet.COLUMN_ID_INDEX);
				game.id = (int) cell.getNumericCellValue();

				cell = row.getCell(GuoXueSheet.COLUMN_TIMU_INDEX);
				game.question = cell.getStringCellValue();

				cell = row.getCell(GuoXueSheet.COLUMN_XUANXIANG_INDEX);
				game.choice = cell.getStringCellValue();

				cell = row.getCell(GuoXueSheet.COLUMN_DAAN_INDEX);
				game.answer = (int) cell.getNumericCellValue();

				cell = row.getCell(GuoXueSheet.COLUMN_JIESHI_INDEX);
				game.explain = cell.getStringCellValue();

				games.add(game);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return games;
	}

	public List<XiaoChuGame> readXiaoChu() {
		ArrayList<XiaoChuGame> games = new ArrayList<>();
		try {
			XSSFSheet sheet = mWorkbook.getSheet(XiaoChuSheet.SHEET_NAME);
			XSSFRow row;
			XSSFCell cell;
			XiaoChuGame game;
			for (int i = sheet.getFirstRowNum() + 1; i < sheet
					.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				game = new XiaoChuGame();

				cell = row.getCell(XiaoChuSheet.COLUMN_ID_INDEX);
				game.id = (int) cell.getNumericCellValue();

				cell = row.getCell(XiaoChuSheet.COLUMN_TIMU_INDEX);
				game.question = cell.getStringCellValue();

				games.add(game);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return games;
	}

	public List<JieLongGame> readJieLong() {
		ArrayList<JieLongGame> games = new ArrayList<>();
		try {
			XSSFSheet sheet = mWorkbook.getSheet(JieLongSheet.SHEET_NAME);
			XSSFRow row;
			XSSFCell cell;
			JieLongGame game;
			for (int i = sheet.getFirstRowNum() + 1; i < sheet
					.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				game = new JieLongGame();

				cell = row.getCell(JieLongSheet.COLUMN_ID_INDEX);
				game.id = (int) cell.getNumericCellValue();

				cell = row.getCell(JieLongSheet.COLUMN_TIMU_INDEX);
				game.question = cell.getStringCellValue();

				games.add(game);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return games;
	}

	public List<ShouJiYanKuaiGame> readShouJiYanKuai() {
		ArrayList<ShouJiYanKuaiGame> games = new ArrayList<>();
		try {
			XSSFSheet sheet = mWorkbook.getSheet(ShouJiYanKuaiSheet.SHEET_NAME);
			XSSFRow row;
			XSSFCell cell;
			ShouJiYanKuaiGame game;
			for (int i = sheet.getFirstRowNum() + 1; i < sheet
					.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				game = new ShouJiYanKuaiGame();

				cell = row.getCell(ShouJiYanKuaiSheet.COLUMN_ID_INDEX);
				game.id = (int) cell.getNumericCellValue();

				cell = row.getCell(ShouJiYanKuaiSheet.COLUMN_TIMU_INDEX);
				game.question = cell.getStringCellValue();

				games.add(game);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return games;
	}

	class GuanKaSheet {
		public static final String SHEET_NAME = "关卡";

		public static final String COLUMN_GUANKAID = "关卡ID";
		public static final int COLUMN_GUANKAID_INDEX = 0;

		public static final String COLUMN_GUANKAHUIHE = "关卡回合";
		public static final int COLUMN_GUANKAHUIHE_INDEX = 1;

		public static final String COLUMN_YOUXILEIXING = "游戏类型";
		public static final int COLUMN_YOUXILEIXING_INDEX = 2;

		public static final String COLUMN_YOUXIID = "游戏ID";
		public static final int COLUMN_YOUXIID_INDEX = 3;
	}

	class KanTuCaiChengYuSheet {
		public static final String SHEET_NAME = "看图猜成语";

		public static final String COLUMN_ID = "游戏ID";
		public static final int COLUMN_ID_INDEX = 0;

		public static final String COLUMN_DAANCHENGYU = "答案成语";
		public static final int COLUMN_DAANCHENGYU_INDEX = 1;

		public static final String COLUMN_QITACHENGYU = "其他成语";
		public static final int COLUMN_QITACHENGYU_INDEX = 2;

		public static final String COLUMN_TUPIAN = "图片";
		public static final int COLUMN_TUPIAN_INDEX = 3;
	}

	class GuoXueSheet {
		public static final String SHEET_NAME = "国学";

		public static final String COLUMN_ID = "游戏ID";
		public static final int COLUMN_ID_INDEX = 0;

		public static final String COLUMN_TIMU = "题目";
		public static final int COLUMN_TIMU_INDEX = 1;

		public static final String COLUMN_XUANXIANG = "选项";
		public static final int COLUMN_XUANXIANG_INDEX = 2;

		public static final String COLUMN_DAAN = "答案";
		public static final int COLUMN_DAAN_INDEX = 3;

		public static final String COLUMN_JIESHI = "解释";
		public static final int COLUMN_JIESHI_INDEX = 4;
	}

	class XiaoChuSheet {
		public static final String SHEET_NAME = "成语消除";

		public static final String COLUMN_ID = "游戏ID";
		public static final int COLUMN_ID_INDEX = 0;

		public static final String COLUMN_TIMU = "题目";
		public static final int COLUMN_TIMU_INDEX = 1;
	}

	class JieLongSheet {
		public static final String SHEET_NAME = "成语接龙";

		public static final String COLUMN_ID = "游戏ID";
		public static final int COLUMN_ID_INDEX = 0;

		public static final String COLUMN_TIMU = "题目";
		public static final int COLUMN_TIMU_INDEX = 1;
	}

	class ShouJiYanKuaiSheet {
		public static final String SHEET_NAME = "手疾眼快";

		public static final String COLUMN_ID = "游戏ID";
		public static final int COLUMN_ID_INDEX = 0;

		public static final String COLUMN_TIMU = "题目";
		public static final int COLUMN_TIMU_INDEX = 1;
	}

}
