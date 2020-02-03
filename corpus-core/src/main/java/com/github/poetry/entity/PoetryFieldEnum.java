package com.github.poetry.entity;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
public enum PoetryFieldEnum {
  TITLE(new TextFieldStrategy("title", GeneralChinesePoetry::getTitle)),
  SUBTITLE(new TextFieldStrategy("subtitle", GeneralChinesePoetry::getSubtitle)),
  AUTHOR(new StringFieldStrategy("author", GeneralChinesePoetry::getAuthor)),
  CONTENT(new TextFieldStrategy("content", GeneralChinesePoetry::getContent)),
  DYNASTY(new StringFieldStrategy("dynasty", GeneralChinesePoetry::getDynasty)),
  TYPE(new StringFieldStrategy("type", GeneralChinesePoetry::getType)),
  SCORE(new DoubleFieldStrategy("score", GeneralChinesePoetry::getScore));

  public final String fieldName;
  public final FieldStrategy<?> strategy;

  PoetryFieldEnum(FieldStrategy<?> strategy) {
    this.fieldName = strategy.getName();
    this.strategy = strategy;
  }
}
