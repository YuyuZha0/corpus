package com.github.poetry.json;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
@RequiredArgsConstructor
@Getter
public final class ShiJingItem {

  @SerializedName("title")
  private final String title;

  @SerializedName("chapter")
  private final String chapter;

  @SerializedName("section")
  private final String section;

  @SerializedName("content")
  private final List<String> content;
}
