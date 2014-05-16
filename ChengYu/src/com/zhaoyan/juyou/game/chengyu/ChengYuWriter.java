package com.zhaoyan.juyou.game.chengyu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

public class ChengYuWriter {
	private String mChengYuFilePath;
	private ChengYuDatabase mDatabase;
	private static final String SEPERATOR_PINYIN = "拼音：";
	private static final String SEPERATOR_COMMENT = "释义：";
	private static final String SEPERATOR_ORIGINAL = "出处：";
	private static final String SEPERATOR_EXAMPLE = "示例：";

	public ChengYuWriter(String chengyuFilePath, ChengYuDatabase database) {
		mChengYuFilePath = chengyuFilePath;
		mDatabase = database;
	}

	public void writeToDataBase() throws FileNotFoundException {
		File file = new File(mChengYuFilePath);
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line = null;
		int id = 1;
		int lineNumber = 0;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				lineNumber++;
				// ignore empty line.
				if (line.trim().length() == 0) {
					continue;
				}
				// write one ChengYu into dababase.
				writeOneChengYuIntoDatabase(id, line, lineNumber);
				id++;
			}
			System.out.println("writeToDataBase total number: " + id);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeOneChengYuIntoDatabase(int id, String chengyu,
			int lineNumber) {
		String name = null;
		String pinyin = null;
		String comment = null;
		String original = null;
		String example = null;
		try {
			name = handleErrorFormate(getSubString(chengyu, null,
					SEPERATOR_PINYIN));
			pinyin = handleErrorFormate(getSubString(chengyu, SEPERATOR_PINYIN,
					SEPERATOR_COMMENT));
			comment = handleErrorFormate(getSubString(chengyu,
					SEPERATOR_COMMENT, SEPERATOR_ORIGINAL));
			original = handleErrorFormate(getSubString(chengyu,
					SEPERATOR_ORIGINAL, SEPERATOR_EXAMPLE));
			example = handleErrorFormate(getSubString(chengyu,
					SEPERATOR_EXAMPLE, null));
		} catch (Exception e1) {
			System.err.println("Seperate line error. lineNumber = "
					+ lineNumber + ", chengyu = " + chengyu);
			e1.printStackTrace();
			return;
		}

		Statement statement = null;
		String sql = null;
		try {
			statement = mDatabase.createStatement();
			sql = "insert into " + ChengYuDatabase.ChengYuTable.TABLE_NAME
					+ " values(" + id + ", '" + name + "','" + pinyin + "','"
					+ comment + "','" + original + "','" + example + "');";
			statement.execute(sql);
			// System.out.println("write id " + id + " ,name " + name +
			// " done");
		} catch (SQLException e) {
			System.err.println("Insert into database error. lineNumber = "
					+ lineNumber + ", chengyu = " + chengyu);
			System.err.println("Sql = " + sql);
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private String getSubString(String string, String start, String end)
			throws Exception {
		int startIndex = 0;
		int endIndex = string.length();
		// get the start index
		if (start != null) {
			int i = string.indexOf(start);
			if (i == -1) {
				throw new Exception("start string not found." + string);
			} else {
				startIndex = i + start.length();
			}
		}
		// get the end index
		if (end != null) {
			int i = string.indexOf(end);
			if (i == -1) {
				throw new Exception("end string not found." + string);
			} else {
				endIndex = i;
			}
		}
		// get sub string
		String result = string.substring(startIndex, endIndex);
		return result;
	}

	/**
	 * Because the chengyu txt file has some chengyu in wrong format, so handle
	 * some errors by program.
	 * 
	 * @param string
	 * @return
	 * @throws Exception
	 */
	private String handleErrorFormate(String string) throws Exception {
		// trim whitespace.
		string = string.trim();
		// handle useless "”"
		if (string.contains("”") && !string.contains("“")) {
			string = string.replace("”", "");
		}
		// handle "'"
		string = handleSingleQuote(string);
		return string;
	}

	/**
	 * handle "'" error, because "'" will cause error when insert into
	 * database.</br>
	 * 
	 * <b>Notice:</b> This method can only handle one pair "‘ ’"，if there are
	 * more "‘ ’" pairs, exception may throw.
	 * 
	 * @param string
	 * @return
	 * @throws Exception
	 */
	private String handleSingleQuote(String string) throws Exception {
		int index = string.indexOf("'");
		if (index != -1) {
			int indexQuoteLeft = string.indexOf("‘");
			if (indexQuoteLeft == -1) {
				string = string.replaceFirst("'", "‘");
			} else {
				if (indexQuoteLeft < index) {
					// xxx‘xxx’xxx'xxx
					int indexQuoteRight = string.indexOf("’");
					if (indexQuoteRight != -1 && indexQuoteRight < index) {
						throw new Exception("Not handled");
					} else {
						// xxx‘xxx'xxx
						string = string.replaceFirst("'", "’");
					}
				} else {
					// xxx'xxx'xxx‘xxx
					string = string.replaceFirst("'", "‘");
				}
			}

			// handle again.
			return handleSingleQuote(string);
		} else {
			return string;
		}
	}
}
