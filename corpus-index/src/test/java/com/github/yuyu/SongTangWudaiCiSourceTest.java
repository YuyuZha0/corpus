package com.github.yuyu;

import com.github.yuyu.entity.ClassicPoetry;
import com.github.yuyu.source.PoetrySource;
import com.github.yuyu.source.SongTangWudaiCiSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
public class SongTangWudaiCiSourceTest {

  private List<ClassicPoetry> list;

  @Before
  public void loadList() {
    PoetrySource poetrySource = new SongTangWudaiCiSource();
    list = poetrySource.get();
  }

  @Test
  public void test() {
    Assert.assertNotNull(list);

    Assert.assertEquals("□阳", list.get(0).getAuthor());
    Assert.assertEquals("宋", list.get(2).getDynasty());
    Assert.assertEquals(
        "紫府延龄，遥池开宴。人间好事都如愿。凤雏喜带桂宫香，东床镇压鸾台彦。 酒泛金杯，香飘龙篆。大家齐把瑶觞献。中兴天子急贤才，相期共入金銮殿。",
        list.get(11).getContent());
    Assert.assertEquals("书怀", list.get(list.size() - 2).getSubtitle());
    Assert.assertEquals("柳梢青", list.get(list.size() - 1).getTitle());
  }
}
