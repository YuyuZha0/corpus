package com.github.poetry.entity;

import lombok.NonNull;

import java.util.function.Function;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
public enum FieldEnum {
  TITLE("title", true, GeneralChinesePoetry::getTitle),
  SUBTITLE("subtitle", true, GeneralChinesePoetry::getSubtitle),
  AUTHOR("author", false, GeneralChinesePoetry::getAuthor),
  CONTENT("content", true, GeneralChinesePoetry::getContent),
  DYNASTY("dynasty", false, GeneralChinesePoetry::getDynasty),
  TYPE("type", false, GeneralChinesePoetry::getType);

  public final String fieldName;
  public final boolean tokenizable;
  final Function<? super GeneralChinesePoetry, String> getter;

  FieldEnum(String fieldName, boolean tokenizable, Function<? super GeneralChinesePoetry, String> getter) {
    this.fieldName = fieldName;
    this.tokenizable = tokenizable;
    this.getter = getter;
  }

  public String get(@NonNull GeneralChinesePoetry generalChinesePoetry) {
    return getter.apply(generalChinesePoetry);
  }
}
