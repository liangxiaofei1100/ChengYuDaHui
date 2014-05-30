package com.zhaoyan.juyou.game.chengyu;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChengYuFileTest {
	private String TEST_CHENGYU_LINE1 = "左思右想 拼音：zuǒ sī yòu xiǎng释义：多方面想了又想。出处：明·冯梦龙《东周列国志》：“第八十八回左思右想，欲求自脱之计。” 示例：如此左思右想，一时五内沸然。出自：清·曹雪芹《红楼梦》第三十四回。 英译： think of this way and that  反义词：义无反顾.，不假思索。 近义词：思前想后、冥思苦想、绞尽脑汁、冥思遐想。 ";
	private String TEST_CHENGYU_LINE2 = "左提右挈 拼音：zuǒ tí yòu qiè释义：挈是指带领，举起。比喻共相扶持。也形容父母对子女的照顾。出处：《史记·张耳陈余列传》：“夫以一赵尚易燕，况以两贤王左提右挈，而责杀王之罪，灭燕易矣。” 示例：果能举左提右挈之实，宁复有南强北胜之虞？出自：蔡东藩、许厪父《民国通俗演义》第八十七回。 近义词：左挈右提。 成语故事：秦朝末年，各地农民纷纷起义，秦末农民起义军将领武臣率兵攻克邯郸后，自立为赵王。武臣派部将韩广北上夺取燕地。韩广占领燕地后，自立为燕王。武臣闻报大怒，立即带领左、右校尉张耳、陈馀前去伐燕。武臣带少数将校深入燕地了解敌情，被燕军发现，经过一场激战，终因寡不敌众，被燕军俘获。张耳、陈余设法营救未果。为救武臣，张耳、陈馀派人前去游说韩广。赵使面见韩广后，欺骗说：“张耳、陈馀愿意让您把武臣杀掉，这样他俩便可平分赵国，自立为王。如果两个赵王‘左提右挈’（互相提携、扶持），要消灭燕国就太容易了。”韩广一听，赶紧放武臣回去了。";
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
		ChengYu chengYu = chengYuFile.readChengYu(TEST_CHENGYU_LINE1, 0);
		ChengYuTest.testZuoSiYouXiang(chengYu);
	}

	@Test
	public void testReadChengYu2() throws FileNotFoundException {
		ChengYu chengYu = chengYuFile.readChengYu(TEST_CHENGYU_LINE2, 0);
		ChengYuTest.testZuoTiYouQing(chengYu);
	}

}
