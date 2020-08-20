package com.github.poetry.rank;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
@ToString
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class RankingStat {

  private static final double MIN_SCORE = 1;

  private final String title;

  private final String author;

  private final long baidu;

  private final long so360;

  private final long bing;

  private final long bingEn;

  private final long google;

  @JsonCreator
  public RankingStat(
      @JsonProperty("title") @JsonAlias("rhythmic") String title,
      @JsonProperty("author") String author,
      @JsonProperty("baidu") long baidu,
      @JsonProperty("so360") long so360,
      @JsonProperty("bing") long bing,
      @JsonProperty("bing_en") long bingEn,
      @JsonProperty("google") long google) {
    this.title = title;
    this.author = author;
    this.baidu = baidu;
    this.so360 = so360;
    this.bing = bing;
    this.bingEn = bingEn;
    this.google = google;
  }

  public double calcScore() {

    double score = 1D;
    score *= Math.log(Math.E + baidu);
    score *= Math.log(Math.E + so360);
    score *= Math.log(Math.E + bing);
    score *= Math.log(Math.E + bingEn);
    score *= Math.log(Math.E + google);
    return Math.pow(score, 0.2);
  }
}
