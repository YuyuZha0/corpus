package com.github.poetry.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class WuDaiItem implements Serializable {

  private final String title;

  private final String author;

  private final String rhythmic;

  private final List<String> paragraphs;

  private final List<String> notes;

  @JsonCreator
  public WuDaiItem(
      @JsonProperty("title") String title,
      @JsonProperty("author") String author,
      @JsonProperty("rhythmic") String rhythmic,
      @JsonProperty("paragraphs") List<String> paragraphs,
      @JsonProperty("notes") List<String> notes) {
    this.title = title;
    this.author = author;
    this.rhythmic = rhythmic;
    this.paragraphs = paragraphs;
    this.notes = notes;
  }
}
