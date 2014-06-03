package com.zhaoyan.juyou.game.chengyu;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChengYuFileTest {
	private ChengYuFile chengYuFile;

	@Before
	public void setUp() throws Exception {
		chengYuFile = new ChengYuFile(Config.CHENGYU_FILE_PATH);
	}

	@After
	public void tearDown() throws Exception {
		chengYuFile = null;
	}

	@Test
	public void testReadChengYu1() throws FileNotFoundException {
		ChengYu chengYu = chengYuFile.readChengYu(
				ChengYuTest.ZUO_SI_YOU_XIANG_LINE, 0);
		ChengYuTest.testZuoSiYouXiang(chengYu);
	}

	@Test
	public void testReadChengYu2() throws FileNotFoundException {
		ChengYu chengYu = chengYuFile.readChengYu(
				ChengYuTest.WU_WANG_BU_LI_LINE, 0);
		ChengYuTest.testWuWangBuLi(chengYu);

	}

}
