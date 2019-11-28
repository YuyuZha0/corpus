package com.github.poetry;

import com.github.poetry.transform.TransformUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public class TransformUtilsTest {

  @Test
  public void testMidDotSplit() {
    Assert.assertArrayEquals(
        new String[] {"定西番", "紫塞月明千里"}, TransformUtils.splitWithMidDot("定西番·紫塞月明千里"));
    Assert.assertArrayEquals(
        new String[] {"诈妮子调风月", "圣药王"}, TransformUtils.splitWithMidDot("诈妮子调风月・圣药王"));
  }

  @Test
  public void testBlankSplit() {
    Assert.assertArrayEquals(
        new String[] {"咏史", "荀彧 其四"}, TransformUtils.splitWithBlank("咏史 荀彧 其四"));
    Assert.assertArrayEquals(
        new String[] {"彭门解嘲二首", "二"}, TransformUtils.splitWithBlank("彭门解嘲二首 二"));
  }
}
