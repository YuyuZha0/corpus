package com.github.poetry;

import com.github.poetry.rank.RankingStat;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
public class QueryScoreTest {

  private final Gson gson = new Gson();

  @Test
  public void test() throws Exception {
    String json =
        "{\n"
            + "        \"baidu\": 752000,\n"
            + "        \"author\": \"柳永\",\n"
            + "        \"rhythmic\": \"西施\",\n"
            + "        \"so360\": 47300,\n"
            + "        \"bing\": 4220,\n"
            + "        \"bing_en\": 31600,\n"
            + "        \"google\": 1010000\n"
            + "    }";
    RankingStat stat = gson.fromJson(json, RankingStat.class);

    Assert.assertEquals(752000L, stat.getBaidu());
    Assert.assertEquals("柳永", stat.getAuthor());
    Assert.assertEquals("西施", stat.getTitle());
    Assert.assertEquals(47300L, stat.getSo360());
    Assert.assertEquals(4220L, stat.getBing());
    Assert.assertEquals(31600L, stat.getBingEn());
    Assert.assertEquals(1010000L, stat.getGoogle());

    //Assert.assertEquals(11.173526472371558, stat.calcScore(), 1e-4);
  }
}
