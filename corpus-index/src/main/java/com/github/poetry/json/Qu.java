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
public final class Qu implements Serializable {

  private final String dynasty;

  private final String author;

  private final List<String> paragraphs;

  private final String title;

  @JsonCreator
  public Qu(
      @JsonProperty("dynasty") String dynasty,
      @JsonProperty("author") String author,
      @JsonProperty("paragraphs") List<String> paragraphs,
      @JsonProperty("title") String title) {
    this.dynasty = dynasty;
    this.author = author;
    this.paragraphs = paragraphs;
    this.title = title;
  }
}
