package com.zhaoyan.juyou.game.knowledge;

import java.sql.SQLException;
import java.util.List;

import com.zhaoyan.juyou.common.Log;
import com.zhaoyan.juyou.game.knowledge.data.GuessPictureGame;
import com.zhaoyan.juyou.game.knowledge.data.GuoXueGame;
import com.zhaoyan.juyou.game.knowledge.data.JieLongGame;
import com.zhaoyan.juyou.game.knowledge.data.ShouJiYanKuaiGame;
import com.zhaoyan.juyou.game.knowledge.data.Stage;
import com.zhaoyan.juyou.game.knowledge.data.XiaoChuGame;

public class KnowledageMainClass {
	private static final String TAG = KnowledageMainClass.class.getSimpleName();

	public static void main(String[] args) {
		Log.d(TAG, "Start");
		Log.d(TAG, "Read start...");
		KnowledgeExcel knowledgeExcel = new KnowledgeExcel(
				Config.getExcelFilePath());
		knowledgeExcel.open();

		List<Stage> stages = knowledgeExcel.readStage();
		Log.d(TAG, "Read stage finished. stage total number: " + stages.size());

		List<GuessPictureGame> guessPictureGames = knowledgeExcel
				.readGuessPicture();
		Log.d(TAG, "Read guessPictureGames finished. total number: "
				+ guessPictureGames.size());

		List<GuoXueGame> guoxueGames = knowledgeExcel.readGuoXue();
		Log.d(TAG,
				"Read guoxueGames finished. total number: "
						+ guoxueGames.size());

		List<XiaoChuGame> xiaochuGames = knowledgeExcel.readXiaoChu();
		Log.d(TAG,
				"Read xiaochuGames finished. total number: "
						+ xiaochuGames.size());

		List<JieLongGame> jieLongGames = knowledgeExcel.readJieLong();
		Log.d(TAG,
				"Read jieLongGames finished. total number: "
						+ jieLongGames.size());

		List<ShouJiYanKuaiGame> shouJiYanKuaiGames = knowledgeExcel
				.readShouJiYanKuai();
		Log.d(TAG, "Read shouJiYanKuaiGames finished. total number: "
				+ shouJiYanKuaiGames.size());

		knowledgeExcel.close();
		Log.d(TAG, "Read end.");
		Log.d(TAG, "Write start...");
		KnowledgeDatabase database = new KnowledgeDatabase(
				Config.getOutDatabasePath());
		database.open();
		try {
			database.createTables();
			database.addStage(stages);
			database.addGuessPicture(guessPictureGames);
			database.addGuoXue(guoxueGames);
			database.addXiaoChu(xiaochuGames);
			database.addJieLong(jieLongGames);
			database.addShouJiYanKuai(shouJiYanKuaiGames);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		database.close();
		Log.d(TAG, "Write end.");
		Log.d(TAG, "End.");
	}
}
