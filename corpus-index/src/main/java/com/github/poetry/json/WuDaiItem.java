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
public final class WuDaiItem implements Serializable {

  @SerializedName("title")
  private final String title;

  @SerializedName("author")
  private final String author;

  @SerializedName("rhythmic")
  private final String rhythmic;

  @SerializedName("paragraphs")
  private final List<String> paragraphs;

  @SerializedName("notes")
  private final List<String> notes;
}
