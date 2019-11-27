package com.github.yuyu;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhaoyuyu
 * @since 2019/11/22
 */
public class CharMatcherText {

  @Test
  public void test1() {
    Assert.assertTrue(CharMatcher.whitespace().matches('\u3000'));
  }

  @Test
  public void test2() {
    Assert.assertTrue(StringUtils.isBlank("\u3000"));
  }
}
