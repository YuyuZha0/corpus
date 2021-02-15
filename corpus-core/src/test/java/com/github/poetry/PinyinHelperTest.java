package com.github.poetry;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.junit.Test;

/**
 * @author zhaoyuyu
 * @since 2020/8/23
 */
public class PinyinHelperTest {

  @Test
  public void test() throws Exception {
    String s1 = "自南朝之宫体[13]，扇北里[14]之倡风。何止言之不文[15]，所谓秀而不实。";
    String s2 = "自南朝之宫体扇北里之倡风何止言之不文所谓秀而不实";

    String ss1 = PinyinHelper.getShortPinyin(s1);
    String ss2 = PinyinHelper.getShortPinyin(s2);

    System.out.println(new JaroWinklerSimilarity().apply(ss1, ss2));
  }
}
