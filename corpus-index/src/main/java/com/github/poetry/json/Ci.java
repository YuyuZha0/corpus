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
public final class Ci implements Serializable {

  @SerializedName("author")
  private final String author;

  @SerializedName("paragraphs")
  private final List<String> paragraphs;

  @SerializedName("rhythmic")
  private final String rhythmic;
}
