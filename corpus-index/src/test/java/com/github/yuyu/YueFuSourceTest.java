package com.github.yuyu;

import com.github.yuyu.entity.ClassicPoetry;
import com.github.yuyu.source.PoetrySource;
import com.github.yuyu.source.YueFuSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
public class YueFuSourceTest {

  private List<ClassicPoetry> list;

  @Before
  public void loadList() {
    PoetrySource poetrySource = new YueFuSource();
    list = poetrySource.get();
  }

  @Test
  public void test() {
    Assert.assertNotNull(list);
    Assert.assertEquals("五十六首之三十四", list.get(1).getTitle());
    Assert.assertEquals("十索，四首之一", list.get(3).getSubtitle());
    Assert.assertEquals("丁六娘", list.get(5).getAuthor());
    Assert.assertEquals(
        "天挺圣哲，三方维纲。川岳伊宁，七耀重光。茂育万物，众庶咸康。道用潜通，仁施遐扬。德厚坤极，功高昊苍，舞象盛容，德以歌章。八音既节，龙跃凤翔。皇基永树，二仪等长。",
        list.get(list.size() - 2).getContent());
    Assert.assertEquals("", list.get(list.size() - 1).getDynasty());
  }
}
