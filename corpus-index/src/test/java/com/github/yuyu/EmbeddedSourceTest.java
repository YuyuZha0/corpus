package com.github.yuyu;

import com.github.yuyu.entity.ClassicPoetry;
import com.github.yuyu.source.PoetrySource;
import com.github.yuyu.source.QuanTangShiSource.EmbeddedSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/22
 */
public class EmbeddedSourceTest {

  @Test
  public void test1() {
    PoetrySource poetrySource = new EmbeddedSource("全唐诗/卷892 韦庄 牛峤.txt");
    List<ClassicPoetry> list = poetrySource.get();
    Assert.assertEquals(33, list.size());
    Assert.assertEquals("江城子（城一作神。一名水晶帘。）", list.get(2).getTitle());
    Assert.assertEquals(
        "记得去年，烟暖杏园花正发。雪飘香，江草绿。柳丝长。　　钿车纤手卷帘望，眉学春山样。凤钗低袅翠鬟上，落梅妆。", list.get(28).getContent());
  }

  @Test
  public void test2() {
    PoetrySource poetrySource =
        new EmbeddedSource("全唐诗/卷727 胡令能 严郾 蒋肱 张迥 张友正 伍唐珪 孙棨 颜荛 张为 马冉 周镛 刘赞 任翻 荆浩 张直 陈光.txt");
    List<ClassicPoetry> list = poetrySource.get();
    Assert.assertEquals(48, list.size());
    Assert.assertEquals("任翻", list.get(37).getAuthor());
    Assert.assertEquals(
        "带剑谁家子，春朝紫陌游。结边霞聚锦，悬处月随钩。<br>"
            + "彩缕回文出，雄芒练影浮。叶依花里艳，霜向锷中秋。<br>"
            + "的皪宜骢马，斓斒映绮裘。应须待报国，一刎月支头。",
        list.get(9).getContent());
  }

  @Test
  public void test3() {
    PoetrySource poetrySource = new EmbeddedSource("全唐诗/卷161-185 李白.txt");
    List<ClassicPoetry> list = poetrySource.get();
    Assert.assertEquals(766, list.size());
    Assert.assertEquals("战城南", list.get(7).getTitle());
    Assert.assertEquals(
        "焰随红日去，烟逐暮云飞。（令一日赋山火诗云：野火烧山后，人归火不归。思轧不属，白从旁缀其下句，令惭止）。<br>"
            + "绿鬓随波散，红颜逐浪无。因何逢伍相，应是想秋胡。（白从令观涨，有女子溺死江上。令赋诗云：二八谁家女，漂来倚岸芦。鸟窥眉上翠，鱼弄口旁珠。令复苦吟，白辄应声继之。）<br>"
            + "举袖露条脱，招我饭胡麻。（见二老堂诗话）。",
        list.get(list.size() - 1).getContent());
  }

  @Test
  public void test4() {
    PoetrySource poetrySource = new EmbeddedSource("全唐诗/卷005 文德皇后 则天皇后 徐贤妃 上官昭容 杨贵妃 江妃.txt");
    List<ClassicPoetry> list = poetrySource.get();
    Assert.assertEquals(86, list.size());
    Assert.assertEquals("句", list.get(list.size() - 3).getTitle());
    Assert.assertEquals(
        "万岁通天元年，铸九鼎成。上各写本州山川物产之象，令著作郎贾膺<br>"
            + "福。殿中丞薛昌容，凤阁主事李元振。司农录事钟绍京等分题，左尚<br>"
            + "令曹元廨画。令南北卫士十余万人并仗内大牛白象曳之，自玄武门入<br>"
            + "，后自制蔡州永昌鼎歌，见唐会要。<br>"
            + "羲农首出，轩昊膺期。唐虞继踵，汤禹乘时。<br>"
            + "天下光宅，海内雍熙。上玄降鉴，方建隆基。（中有隆基字，开元中，姚崇等以启运休兆，请宣付史馆。）",
        list.get(1).getContent());
    Assert.assertEquals("第四", list.get(5).getSubtitle());
  }
}
