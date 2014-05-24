package com.zhaoyan.juyou.game.chengyu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ChengYuWriter {
	private String mChengYuFilePath;
	private ChengYuDatabase mDatabase;
	private static final String SEPERATOR_PINYIN = "拼音：";
	private static final String SEPERATOR_COMMENT = "释义：";
	private static final String SEPERATOR_ORIGINAL = "出处：";
	private static final String SEPERATOR_EXAMPLE = "示例：";
	private static final String SYMBOL_NAME = "～";
	private static final String SYMBOL_FROM = "★";
	private static final String SYMBOL_NOW = "◇";

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
		int lineNumber = 0;
		ArrayList<ChengYu> list = new ArrayList<ChengYu>();
		try {
			while ((line = bufferedReader.readLine()) != null) {
				lineNumber++;
				// ignore empty line.
				if (line.trim().length() == 0) {
					continue;
				}
				// write one ChengYu into dababase.
				addChengYuToList(line, lineNumber, list);
			}
			System.out.println("read finish, total number: " + list.size());
			System.out.println("writeToDataBase begin.");
			int n = writeChengYuIntoDatabase(list);
			System.out.println("writeToDataBase finish. total number: " + n);
			System.out.println("write to file begin.");
			writeChengYuIntoFile(list);
			System.out.println("write to file end.");
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

	private void writeChengYuIntoFile(ArrayList<ChengYu> list)
			throws IOException {
		File outFile = new File(Config.OUT_TEXT_FILE_PATH);
		if (outFile.exists()) {
			outFile.delete();
		}
		FileWriter writer = new FileWriter(outFile);
		String line = "";
		for (ChengYu chengyu : list) {
			line = chengyu.name + " " + SEPERATOR_PINYIN + chengyu.pinyin
					+ SEPERATOR_COMMENT + chengyu.comment + SEPERATOR_ORIGINAL
					+ chengyu.original + SEPERATOR_EXAMPLE + chengyu.example;
			writer.append(line);
			writer.append("\n\n");
		}
		writer.flush();
		writer.close();
	}

	private int writeChengYuIntoDatabase(ArrayList<ChengYu> list) {
		int id = 1;
		for (ChengYu chengyu : list) {
			insertChengYu(id, chengyu.name, chengyu.pinyin, chengyu.comment,
					chengyu.original, chengyu.example);
			id++;
		}
		return id - 1;
	}

	private void insertChengYu(int id, String name, String pinyin,
			String comment, String original, String example) {
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
			System.err.println("Sql = " + sql + " id = " + id + ", name = "
					+ name);
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

	private void addChengYuToList(String chengyu, int lineNumber,
			ArrayList<ChengYu> list) {
		String name = null;
		String pinyin = null;
		String comment = null;
		String original = null;
		String example = null;
		try {
			name = handleErrorFormate(getSubString(chengyu, null,
					SEPERATOR_PINYIN));
			// Handle symbols.
			chengyu = handleSymbols(name, chengyu);

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
		boolean isRepeat = false;
		for (ChengYu item : list) {
			if (item.name.equals(name)) {
				isRepeat = true;
				break;
			}
		}
		if (!isRepeat) {
			list.add(new ChengYu(name, pinyin, comment, original, example));
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
			// Handle symbols.
			chengyu = handleSymbols(name, chengyu);

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

	private String handleSymbols(String name, String chengyu) {
		chengyu = chengyu.replaceAll(SYMBOL_FROM, "出自：");
		chengyu = chengyu.replaceAll(SYMBOL_NOW, "后");
		chengyu = chengyu.replaceAll(SYMBOL_NAME, name);
		return chengyu;
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
