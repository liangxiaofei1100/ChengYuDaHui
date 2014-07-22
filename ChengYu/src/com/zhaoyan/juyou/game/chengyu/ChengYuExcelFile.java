package com.zhaoyan.juyou.game.chengyu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Read and write ChengYu excel file.
 * 
 */
public class ChengYuExcelFile {
	private XSSFWorkbook mWorkbook;
	private static final String SHEET_CHENGYU = "成语";

	private static final String COLUMN_NAME = "成语";
	private static final int COLUMN_NAME_INDEX = 0;

	private static final String COLUMN_PINYIN = "拼音";
	private static final int COLUMN_PINYIN_INDEX = 1;

	private static final String COLUMN_COMMENT = "释义";
	private static final int COLUMN_COMMENT_INDEX = 2;

	private static final String COLUMN_ORIGINAL = "出处";
	private static final int COLUMN_ORIGINAL_INDEX = 3;

	private static final String COLUMN_EXAMPLE = "示例";
	private static final int COLUMN_EXAMPLE_INDEX = 4;

	private static final String COLUMN_FREQUENTLY = "百科链接";
	private static final int COLUMN_FREQUENTLY_INDEX = 5;

	private static final String COLUMN_SIMILAR = "近义词";
	private static final int COLUMN_SIMILAR_INDEX = 6;

	private static final String COLUMN_OPPOSITE = "反义词";
	private static final int COLUMN_OPPOSITE_INDEX = 7;

	private static final String COLUMN_STORY = "成语故事";
	private static final int COLUMN_STORY_INDEX = 8;

	public ChengYuExcelFile() {

	}

	public ArrayList<ChengYu> readAll(String filePath) {
		ArrayList<ChengYu> chengYuList = new ArrayList<>();
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}
		try {
			mWorkbook = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = mWorkbook.getSheet(SHEET_CHENGYU);
			XSSFRow row;
			XSSFCell cell;
			ChengYu chengYu;
			for (int i = sheet.getFirstRowNum() + 1; i < sheet
					.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				chengYu = new ChengYu();
				cell = row.getCell(COLUMN_NAME_INDEX);
				chengYu.name = cell.getStringCellValue();
				cell = row.getCell(COLUMN_PINYIN_INDEX);
				chengYu.pinyin = cell.getStringCellValue();
				cell = row.getCell(COLUMN_COMMENT_INDEX);
				chengYu.comment = cell.getStringCellValue();
				cell = row.getCell(COLUMN_ORIGINAL_INDEX);
				if (cell != null) {
					chengYu.original = cell.getStringCellValue();
				}
				cell = row.getCell(COLUMN_EXAMPLE_INDEX);
				if (cell != null) {
					chengYu.example = cell.getStringCellValue();
				}
				cell = row.getCell(COLUMN_FREQUENTLY_INDEX);
				System.out.println("row: " + i + ", chengyu = " + chengYu.name);
				if (cell == null
						|| cell.getStringCellValue().trim().length() == 0) {
					chengYu.frequently = 0;
				} else {
					chengYu.frequently = 1;
				}
				cell = row.getCell(COLUMN_SIMILAR_INDEX);
				if (cell != null) {
					chengYu.similar = cell.getStringCellValue();
				}
				cell = row.getCell(COLUMN_OPPOSITE_INDEX);
				if (cell != null) {
					chengYu.opposite = cell.getStringCellValue();
				}
				cell = row.getCell(COLUMN_STORY_INDEX);
				if (cell != null) {
					chengYu.story = cell.getStringCellValue();
				}
				chengYuList.add(chengYu);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return chengYuList;
	}

	public void writeIntoExcel(String filePath, ArrayList<ChengYu> list)
			throws IOException {
		mWorkbook = new XSSFWorkbook();
		XSSFSheet sheet = mWorkbook.createSheet(SHEET_CHENGYU);
		int rowIndex = 0;
		XSSFRow row;
		XSSFCell cell;
		// First Row
		row = sheet.createRow(rowIndex);
		cell = row.createCell(COLUMN_NAME_INDEX);
		cell.setCellValue(COLUMN_NAME);
		cell = row.createCell(COLUMN_PINYIN_INDEX);
		cell.setCellValue(COLUMN_PINYIN);
		cell = row.createCell(COLUMN_COMMENT_INDEX);
		cell.setCellValue(COLUMN_COMMENT);
		cell = row.createCell(COLUMN_ORIGINAL_INDEX);
		cell.setCellValue(COLUMN_ORIGINAL);
		cell = row.createCell(COLUMN_EXAMPLE_INDEX);
		cell.setCellValue(COLUMN_EXAMPLE);
		cell = row.createCell(COLUMN_FREQUENTLY_INDEX);
		cell.setCellValue(COLUMN_FREQUENTLY);
		cell = row.createCell(COLUMN_SIMILAR_INDEX);
		cell.setCellValue(COLUMN_SIMILAR);
		cell = row.createCell(COLUMN_OPPOSITE_INDEX);
		cell.setCellValue(COLUMN_OPPOSITE);
		cell = row.createCell(COLUMN_STORY_INDEX);
		cell.setCellValue(COLUMN_STORY);
		// Write ChengYu
		for (ChengYu chengYu : list) {
			rowIndex++;
			row = sheet.createRow(rowIndex);
			cell = row.createCell(COLUMN_NAME_INDEX);
			cell.setCellValue(chengYu.name);
			cell = row.createCell(COLUMN_PINYIN_INDEX);
			cell.setCellValue(chengYu.pinyin);
			cell = row.createCell(COLUMN_COMMENT_INDEX);
			cell.setCellValue(chengYu.comment);
			cell = row.createCell(COLUMN_ORIGINAL_INDEX);
			cell.setCellValue(chengYu.original);
			cell = row.createCell(COLUMN_EXAMPLE_INDEX);
			cell.setCellValue(chengYu.example);
			cell = row.createCell(COLUMN_FREQUENTLY_INDEX);
			cell.setCellValue(chengYu.frequently);
			cell = row.createCell(COLUMN_SIMILAR_INDEX);
			cell.setCellValue(chengYu.similar);
			cell = row.createCell(COLUMN_OPPOSITE_INDEX);
			cell.setCellValue(chengYu.opposite);
			cell = row.createCell(COLUMN_STORY_INDEX);
			cell.setCellValue(chengYu.story);
		}

		// Write into file.
		FileOutputStream os = new FileOutputStream(filePath);
		mWorkbook.write(os);
		os.close();
	}
}
