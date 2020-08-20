package com.github.poetry.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
@Getter
public final class Shi implements Serializable {

  private final String author;

  private final List<String> paragraphs;

  private final String title;

  private final String id;

  @JsonCreator
  public Shi(
      @JsonProperty("author") String author,
      @JsonProperty("paragraphs") List<String> paragraphs,
      @JsonProperty("title") String title,
      @JsonProperty("id") String id) {
    this.author = author;
    this.paragraphs = paragraphs;
    this.title = title;
    this.id = id;
  }
}
