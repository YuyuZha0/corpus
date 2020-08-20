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
public final class Ci implements Serializable {

  private final String author;

  private final List<String> paragraphs;

  private final String rhythmic;

  @JsonCreator
  public Ci(
      @JsonProperty("author") String author,
      @JsonProperty("paragraphs") List<String> paragraphs,
      @JsonProperty("rhythmic") String rhythmic) {
    this.author = author;
    this.paragraphs = paragraphs;
    this.rhythmic = rhythmic;
  }
}
