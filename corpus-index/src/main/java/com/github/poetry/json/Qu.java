package com.github.poetry.json;

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
public final class Qu implements Serializable {

  @SerializedName("dynasty")
  private final String dynasty;

  @SerializedName("author")
  private final String author;

  @SerializedName("paragraphs")
  private final List<String> paragraphs;

  @SerializedName("title")
  private final String title;
}
