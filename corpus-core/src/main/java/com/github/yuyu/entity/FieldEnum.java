package com.github.yuyu.entity;

import lombok.NonNull;

import java.util.function.Function;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
public enum FieldEnum {
  TITLE("title", true, ClassicPoetry::getTitle),
  SUBTITLE("subtitle", true, ClassicPoetry::getSubtitle),
  AUTHOR("author", false, ClassicPoetry::getAuthor),
  CONTENT("content", true, ClassicPoetry::getContent),
  DYNASTY("dynasty", false, ClassicPoetry::getDynasty),
  TYPE("type", false, ClassicPoetry::getType);

  public final String fieldName;
  public final boolean tokenizable;
  final Function<? super ClassicPoetry, String> getter;

  FieldEnum(String fieldName, boolean tokenizable, Function<? super ClassicPoetry, String> getter) {
    this.fieldName = fieldName;
    this.tokenizable = tokenizable;
    this.getter = getter;
  }

  public String get(@NonNull ClassicPoetry classicPoetry) {
    return getter.apply(classicPoetry);
  }
}
