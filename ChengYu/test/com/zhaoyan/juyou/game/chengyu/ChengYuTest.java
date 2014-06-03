package com.zhaoyan.juyou.game.chengyu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChengYuTest {
	public static final String WU_WANG_BU_LI_LINE = "无往不利 拼音：wú wǎng bù lì释义：所到之处，没有不顺利的。指处处行得通，办得好。出处：清·李汝珍《镜花缘》第九十回贫道今日幸把些尘垢全都拭净，此后是皓月当空，一无渣滓，诸位才女定是无往不利。示例：无";
	public static final String WU_WANG_BU_LI = "无往不利";

	public static void testWuWangBuLi(ChengYu chengYu) {
		System.out.println(chengYu);
		assertNotNull(chengYu);
		assertEquals(chengYu.name, "无往不利");
		assertEquals(chengYu.pinyin, "wú wǎng bù lì");
		assertEquals(chengYu.comment, "所到之处，没有不顺利的。指处处行得通，办得好。");
		assertEquals(chengYu.original,
				"清·李汝珍《镜花缘》第九十回贫道今日幸把些尘垢全都拭净，此后是皓月当空，一无渣滓，诸位才女定是无往不利。");
		assertEquals(chengYu.example, "无");
		assertEquals(chengYu.english, "");
		assertEquals(chengYu.similar, "");
		assertEquals(chengYu.opposite, "");
		assertEquals(chengYu.story, "");

	}

	public static final String ZUO_SI_YOU_XIANG_LINE = "左思右想 拼音：zuǒ sī yòu xiǎng释义：多方面想了又想。出处：明·冯梦龙《东周列国志》：“第八十八回左思右想，欲求自脱之计。” 示例：如此左思右想，一时五内沸然。出自：清·曹雪芹《红楼梦》第三十四回。 近义词：思前想后、冥思苦想、绞尽脑汁  反义词：义无反顾、不假思索  英译： think of this way and that ";
	public static final String ZUO_SI_YOU_XIANG = "左思右想";

	public static void testZuoSiYouXiang(ChengYu chengYu) {
		System.out.println(chengYu);
		// check it.
		assertNotNull(chengYu);
		assertEquals(chengYu.name, "左思右想");
		assertEquals(chengYu.pinyin, "zuǒ sī yòu xiǎng");
		assertEquals(chengYu.comment, "多方面想了又想。");
		assertEquals(chengYu.original, "明·冯梦龙《东周列国志》：“第八十八回左思右想，欲求自脱之计。”");
		assertEquals(chengYu.example, "如此左思右想，一时五内沸然。出自：清·曹雪芹《红楼梦》第三十四回。");
		assertEquals(chengYu.english, "think of this way and that");
		assertEquals(chengYu.similar, "思前想后、冥思苦想、绞尽脑汁");
		assertEquals(chengYu.opposite, "义无反顾、不假思索");
		assertEquals(chengYu.story, "");
	}
}
