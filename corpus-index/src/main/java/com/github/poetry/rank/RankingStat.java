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

  @SerializedName(value = "title", alternate = "rhythmic")
  private final String title;

  @SerializedName("author")
  private final String author;

  @SerializedName("baidu")
  private final int baidu;

  @SerializedName("so360")
  private final int so360;

  @SerializedName("bing")
  private final int bing;

  @SerializedName("bingLen")
  private final int bingLen;

  @SerializedName("google")
  private final int google;

  public double calcScore() {
    return Math.log(
        Math.max(1, baidu)
            * Math.max(1, so360)
            * Math.max(1, bingLen)
            * Math.max(1, bingLen)
            * Math.max(1, google));
  }
}
