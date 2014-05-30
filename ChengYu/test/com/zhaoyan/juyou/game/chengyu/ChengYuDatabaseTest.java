package com.zhaoyan.juyou.game.chengyu;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChengYuDatabaseTest {

	ChengYuDatabase database;

	@Before
	public void setUp() throws Exception {
		database = new ChengYuDatabase(Config.OUT_DATABASE_FILE_PATH);
	}

	@After
	public void tearDown() throws Exception {
		database.close();
		database = null;
	}

	@Test
	public void testReadFromDataBase1() {
		ArrayList<ChengYu> list = database.readFromDataBase();
		ChengYu chengYu = null;
		for (ChengYu chengYuItem : list) {
			if (chengYuItem.name.equals(ChengYuTest.ZuoSiYouXiang)) {
				chengYu = chengYuItem;
			}
		}

		ChengYuTest.testZuoSiYouXiang(chengYu);
	}

	@Test
	public void testReadFromDataBase2() {
		ArrayList<ChengYu> list = database.readFromDataBase();
		ChengYu chengYu = null;
		for (ChengYu chengYuItem : list) {
			if (chengYuItem.name.equals(ChengYuTest.ZuoTiYouQing)) {
				chengYu = chengYuItem;
			}
		}
		ChengYuTest.testZuoTiYouQing(chengYu);
	}
}
