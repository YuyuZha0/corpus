package com.github.poetry.json;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
@RequiredArgsConstructor
@Getter
public final class Shi implements Serializable {

  @SerializedName("author")
  private final String author;

  @SerializedName("paragraphs")
  private final List<String> paragraphs;

  @SerializedName("title")
  private final String title;

  @SerializedName("id")
  private final String id;
}
