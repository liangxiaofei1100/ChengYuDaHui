package com.zhaoyan.juyou.game.chengyu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ChengYuFile {
	public static final String SEPERATOR_PINYIN = "拼音：";
	public static final String SEPERATOR_COMMENT = "释义：";
	public static final String SEPERATOR_ORIGINAL = "出处：";
	public static final String SEPERATOR_EXAMPLE = "示例：";
	public static final String SEPERATOR_ENGLISH = "英译：";
	public static final String SEPERATOR_OPPOSITE = "反义词：";
	public static final String SEPERATOR_SIMILAR = "近义词：";
	public static final String SEPERATOR_STORY = "成语故事：";

	public static final String NONE = "无";

	public static final String OPPOSITE_SIMILAR_SEPERATOR = "、";

	private String REGEX_SEPERATOR_ALL = ChengYuFile.SEPERATOR_PINYIN + "|"
			+ ChengYuFile.SEPERATOR_COMMENT + "|"
			+ ChengYuFile.SEPERATOR_ORIGINAL + "|"
			+ ChengYuFile.SEPERATOR_EXAMPLE + "|"
			+ ChengYuFile.SEPERATOR_SIMILAR + "|"
			+ ChengYuFile.SEPERATOR_OPPOSITE + "|"
			+ ChengYuFile.SEPERATOR_ENGLISH + "|" + ChengYuFile.SEPERATOR_STORY;

	private String mChengYuFilePath;

	public ChengYuFile(String chengyuFilePath) {
		mChengYuFilePath = chengyuFilePath;
	}

	public ArrayList<ChengYu> readAll() throws FileNotFoundException {
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
				ChengYu chengYu = readChengYu(line, lineNumber);
				list.add(chengYu);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * @param list
	 * @throws IOException
	 */
	public void writeIntoFile(ArrayList<ChengYu> list) throws IOException {
		File outFile = new File(Config.OUT_TEXT_FILE_PATH);
		if (outFile.exists()) {
			outFile.delete();
		}
		FileWriter writer = new FileWriter(outFile);
		String line = "";
		for (ChengYu chengyu : list) {
			line = chengyu.name + " " + ChengYuFile.SEPERATOR_PINYIN
					+ chengyu.pinyin + ChengYuFile.SEPERATOR_COMMENT
					+ chengyu.comment + ChengYuFile.SEPERATOR_ORIGINAL
					+ chengyu.original + ChengYuFile.SEPERATOR_EXAMPLE
					+ chengyu.example + ChengYuFile.SEPERATOR_ENGLISH
					+ chengyu.english + ChengYuFile.SEPERATOR_SIMILAR
					+ chengyu.similar + ChengYuFile.SEPERATOR_OPPOSITE
					+ chengyu.opposite + ChengYuFile.SEPERATOR_STORY
					+ chengyu.story;
			writer.append(line);
			writer.append("\n\n");
		}
		writer.flush();
		writer.close();
	}

	/**
	 * Make sure all similar and opposite ChengYu in our ChengYu database.
	 * 
	 * @param list
	 */
	public boolean checkSimilarAndOpposite(ArrayList<ChengYu> list) {
		boolean checkPass = true;

		ArrayList<String> nameList = new ArrayList<>();
		for (ChengYu chengYu : list) {
			nameList.add(chengYu.name);
		}

		for (ChengYu chengYu : list) {
			boolean checSimilar = checkSimilarAndOpposite(nameList,
					chengYu.similar, chengYu);
			boolean checkOpposite = checkSimilarAndOpposite(nameList,
					chengYu.opposite, chengYu);
			
			if (!checSimilar || !checkOpposite) {
				checkPass = false;
			}
		}

		return checkPass;
	}

	private boolean checkSimilarAndOpposite(ArrayList<String> nameList,
			String similarOrOpposite, ChengYu chengYu) {
		boolean checkPass = true;
		if (!similarOrOpposite.equals("")) {
			String similarOrOppositeAll[] = similarOrOpposite
					.split(OPPOSITE_SIMILAR_SEPERATOR);
			for (String similarOrOppositeOne : similarOrOppositeAll) {
				similarOrOppositeOne = similarOrOppositeOne.trim();
				if (similarOrOppositeOne.endsWith("。")) {
					similarOrOppositeOne = similarOrOppositeOne.substring(0,
							similarOrOppositeOne.indexOf("。"));
					System.err.println("similarOrOpposite format error: end with 。 Chengyu: " + chengYu.name);
				}
				if (!nameList.contains(similarOrOppositeOne)) {
					System.err.println("similarOrOpposite not found. ChengYu: "
							+ chengYu.name + ", similarOrOpposite: " + similarOrOppositeOne);
					checkPass = false;
				}
			}
		}

		return checkPass;
	}

	protected ChengYu readChengYu(String line, int lineNumber) {
		ChengYu chengyu = new ChengYu();

		// Separate all sessions.
		String strs[] = line.split(REGEX_SEPERATOR_ALL);
		for (String str : strs) {
			// Separate one session to get the session's meaning.
			int i = line.indexOf(str);
			String subString = line.substring(0, i).trim();
			// Handle error format
			try {
				str = handleErrorFormat(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Replace none with "";
			if (NONE.equals(str)) {
				str = "";
			}

			if (subString.equals("")) {
				chengyu.name = str;
			} else if (subString.endsWith(SEPERATOR_PINYIN)) {
				chengyu.pinyin = str;
			} else if (subString.endsWith(SEPERATOR_COMMENT)) {
				chengyu.comment = str;
			} else if (subString.endsWith(SEPERATOR_ORIGINAL)) {
				chengyu.original = str;
			} else if (subString.endsWith(SEPERATOR_EXAMPLE)) {
				chengyu.example = str;
			} else if (subString.endsWith(SEPERATOR_ENGLISH)) {
				chengyu.english = str;
			} else if (subString.endsWith(SEPERATOR_SIMILAR)) {
				chengyu.similar = str;
			} else if (subString.endsWith(SEPERATOR_OPPOSITE)) {
				chengyu.opposite = str;
			} else if (subString.endsWith(SEPERATOR_STORY)) {
				chengyu.story = str;
			}
		}

		return chengyu;
	}

	/**
	 * Get substring between start and end.
	 * 
	 * @param string
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public static String subString(String string, String start, String end)
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
	private String handleErrorFormat(String string) throws Exception {
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
