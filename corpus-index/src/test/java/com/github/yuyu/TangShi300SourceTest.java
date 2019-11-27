package com.github.yuyu;

import com.github.yuyu.entity.ClassicPoetry;
import com.github.yuyu.source.TangShi300Source;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
public class TangShi300SourceTest {

  private List<ClassicPoetry> list;

  @Before
  public void loadList() {
    TangShi300Source source = new TangShi300Source();
    this.list = source.get();
  }

  @Test
  public void test() {
    Assert.assertNotNull(list);

    Assert.assertEquals("贼退示官吏并序", list.get(0).getTitle());
    Assert.assertEquals("元结", list.get(1).getAuthor());
    Assert.assertEquals("乐府", list.get(7).getType());
    Assert.assertEquals(
        "凤凰台上凤凰游，凤去台空江自流。吴宫花草埋幽径，晋代衣冠成古丘。三山半茖青天外，二水中分白鹭洲。总为浮云能蔽日，长安不见使人愁。",
        list.get(list.size() - 1).getContent());
  }
}
