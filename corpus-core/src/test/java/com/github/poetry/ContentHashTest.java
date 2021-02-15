package com.github.poetry;

import com.github.poetry.text.TextUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhaoyuyu
 * @since 2019/11/28
 */
public class ContentHashTest {

  @Test
  public void test1() {
    String s1 = "自南朝之宫体[13]，扇北里[14]之倡风。何止言之不文[15]，所谓秀而不实。";
    String s2 = "自南朝之宫体扇北里之倡风何止言之不文所谓秀而不实";

    Assert.assertEquals(TextUtil.contentHash(s1), TextUtil.contentHash(s2));
  }
}
