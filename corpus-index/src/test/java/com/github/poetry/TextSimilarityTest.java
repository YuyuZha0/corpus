package com.github.poetry;

import org.apache.commons.text.similarity.JaccardSimilarity;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.junit.Test;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 */
public class TextSimilarityTest {

  @Test
  public void test() {
    String s1 = "自南朝之宫体[13]，扇北里[14]之倡风。何止言之不文[15]，所谓秀而不实。";
    String s2 = "自南朝之宫体扇北里之倡风何止言之不文所谓秀而不实";

    JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
    System.out.println("jacard:" + jaccardSimilarity.apply(s1, s2));

    JaroWinklerSimilarity jaroWinklerSimilarity = new JaroWinklerSimilarity();
    System.out.println("jaroWinkler:" + jaroWinklerSimilarity.apply(s1, s2));
  }

  @Test
  public void test1() {
    String s1 = "自南朝之宫体扇北里之倡风何止言之不文所谓秀而不实。[13]，[14][15]，。htdhgggggggggggggggggggtrtr";
    String s2 = "自南朝之宫体扇北里之倡风何止言之不文所谓秀而不实";

    JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
    System.out.println("jacard:" + jaccardSimilarity.apply(s1, s2));

    JaroWinklerSimilarity jaroWinklerSimilarity = new JaroWinklerSimilarity();
    System.out.println("jaroWinkler:" + jaroWinklerSimilarity.apply(s1, s2));
  }
}
