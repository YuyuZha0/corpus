package com.github.poetry.rank;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.math.Stats;
import lombok.Getter;
import lombok.Setter;

import java.util.function.DoubleSupplier;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
public final class RankingStat implements DoubleSupplier {

  private static final double MIN_SCORE = 1;

  private final String title;

  private final String author;

  private final long baidu;

  private final long so360;

  private final long bing;

  private final long bingEn;

  private final long google;

  @JsonIgnore private int baiduLevel;

  @JsonIgnore private int so360Level;

  @JsonIgnore private int bingLevel;

  @JsonIgnore private int bingEnLevel;

  @JsonIgnore private int googleLevel;

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

  @SuppressWarnings("UnstableApiUsage")
  public double calcScore() {
    return Stats.meanOf(baiduLevel, so360Level, bingLevel, bingEnLevel, googleLevel);
  }

  @Override
  public double getAsDouble() {
    return calcScore();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("title", title)
        .add("author", author)
        .add("baidu", baidu)
        .add("so360", so360)
        .add("bing", bing)
        .add("bingEn", bingEn)
        .add("google", google)
        .toString();
  }
}
