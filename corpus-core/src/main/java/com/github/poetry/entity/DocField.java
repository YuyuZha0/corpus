package com.github.poetry.entity;

import lombok.Getter;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
public enum DocField {
  TITLE(
      new TextFieldStrategy(
          "title", GeneralChinesePoetry::getTitle, GeneralChinesePoetry::setTitle)),
  SUBTITLE(
      new TextFieldStrategy(
          "subtitle", GeneralChinesePoetry::getSubtitle, GeneralChinesePoetry::setSubtitle)),
  AUTHOR(
      new StringFieldStrategy(
          "author", GeneralChinesePoetry::getAuthor, GeneralChinesePoetry::setAuthor)),
  CONTENT(
      new TextFieldStrategy(
          "content", GeneralChinesePoetry::getContent, GeneralChinesePoetry::setContent)),
  DYNASTY(
      new StringFieldStrategy(
          "dynasty", GeneralChinesePoetry::getDynasty, GeneralChinesePoetry::setDynasty)),
  TYPE(
      new StringFieldStrategy(
          "type", GeneralChinesePoetry::getType, GeneralChinesePoetry::setType)),
  SCORE(
      new DoubleFieldStrategy(
          "score", GeneralChinesePoetry::getScore, GeneralChinesePoetry::setScore));

  @Getter private final FieldStrategy strategy;

  DocField(FieldStrategy strategy) {
    this.strategy = strategy;
  }

  public String getName() {
    return getStrategy().getName();
  }
}
