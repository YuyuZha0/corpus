package com.github.poetry.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ShiJingItem {

  private final String title;

  private final String chapter;

  private final String section;

  private final List<String> content;

  @JsonCreator
  public ShiJingItem(
      @JsonProperty("title") String title,
      @JsonProperty("chapter") String chapter,
      @JsonProperty("section") String section,
      @JsonProperty("content") List<String> content) {
    this.title = title;
    this.chapter = chapter;
    this.section = section;
    this.content = content;
  }
}
