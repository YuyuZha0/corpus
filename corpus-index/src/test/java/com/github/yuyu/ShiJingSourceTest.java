package com.github.yuyu;

import com.github.yuyu.entity.ClassicPoetry;
import com.github.yuyu.source.PoetrySource;
import com.github.yuyu.source.ShiJingSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
public class ShiJingSourceTest {

  private List<ClassicPoetry> list;

  @Before
  public void loadList() {
    PoetrySource poetrySource = new ShiJingSource();
    list = poetrySource.get();
  }

  @Test
  public void test() {
    Assert.assertNotNull(list);
    Assert.assertEquals("关雎", list.get(0).getSubtitle());
    Assert.assertEquals("周南", list.get(5).getTitle());
    Assert.assertEquals(
        "挞彼殷武，奋伐荆楚。罙入其阻，裒荆之旅。有截其所，汤孙之绪。<br>"
            + "维女荆楚，居国南乡。昔有成汤，自彼氐羌，莫敢不来享，莫敢不来王，曰商是常。<br>"
            + "天命多辟，设都于禹之绩。岁事来辟，勿予祸适，稼穑匪解。<br>"
            + "天命降监，下民有严。不僭不滥，不敢怠遑。命于下国，封建厥福。<br>"
            + "商邑翼翼，四方之极。赫赫厥声，濯濯厥灵。寿考且宁，以保我后生。<br>"
            + "陟彼景山，松柏丸丸。是断是迁，方斲是虔。松桷有梴，旅楹有闲，寝成孔安。",
        list.get(list.size() - 1).getContent());
  }
}
