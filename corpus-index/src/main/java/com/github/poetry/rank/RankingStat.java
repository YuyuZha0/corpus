package com.github.poetry.rank;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
@RequiredArgsConstructor
@ToString
@Getter
public final class RankingStat {

  private static final double MIN_SCORE = 1;

  @SerializedName(value = "title", alternate = "rhythmic")
  private final String title;

  @SerializedName("author")
  private final String author;

  @SerializedName("baidu")
  private final long baidu;

  @SerializedName("so360")
  private final long so360;

  @SerializedName("bing")
  private final long bing;

  @SerializedName("bing_en")
  private final long bingEn;

  @SerializedName("google")
  private final long google;

  public double calcScore() {
    return Math.log(
        Math.max(MIN_SCORE, baidu)
            * Math.max(MIN_SCORE, so360)
            * Math.max(MIN_SCORE, bing)
            * Math.max(MIN_SCORE, bingEn)
            * Math.max(MIN_SCORE, google));
  }
}
