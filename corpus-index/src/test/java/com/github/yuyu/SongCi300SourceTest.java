package com.github.yuyu;

import com.github.yuyu.entity.ClassicPoetry;
import com.github.yuyu.source.SongCi300Source;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
public class SongCi300SourceTest {

  private List<ClassicPoetry> list;

  @Before
  public void loadList() {
    SongCi300Source songCi300Source = new SongCi300Source();
    list = songCi300Source.get();
  }

  @Test
  public void test() {
    Assert.assertNotNull(list);
    Assert.assertEquals("木兰花", list.get(0).getTitle());
    Assert.assertEquals("张先", list.get(7).getAuthor());
    Assert.assertEquals(
        "髻子伤春懒更梳。晚风庭院落梅初。淡云来往月疏疏。<br>玉鸭熏\uD872\uDF3B闲瑞脑，朱樱斗帐掩流苏。通犀还解辟寒无。",
        list.get(list.size() - 1).getContent());
  }
}
