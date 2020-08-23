package com.github.poetry;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
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

    System.out.println(PinyinHelper.convertToPinyinString(s1, ",", PinyinFormat.WITHOUT_TONE));
    System.out.println(PinyinHelper.convertToPinyinString(s2, ",", PinyinFormat.WITHOUT_TONE));
  }
}
