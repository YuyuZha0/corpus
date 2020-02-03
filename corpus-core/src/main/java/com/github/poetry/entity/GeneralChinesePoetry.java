package com.github.poetry.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.lucene.document.Document;

import java.io.Serializable;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public final class GeneralChinesePoetry implements Serializable {

  private static final PoetryFieldEnum[] FIELD_ENUMS = PoetryFieldEnum.values();
  // private static final Pattern CN_PTN = Pattern.compile("\\b[\u4e00-\u9fa5]|\u4e00-\u9fa5]\\b");
  private String title;
  private String subtitle;
  private String author;
  private String content;
  private String dynasty;
  private String type;
  private double score = 1D;

  public static GeneralChinesePoetry fromLuceneDocument(@NonNull Document document) {
    return new GeneralChinesePoetry(
        PoetryFieldEnum.TITLE.strategy.getValueAsString(document),
        PoetryFieldEnum.SUBTITLE.strategy.getValueAsString(document),
        PoetryFieldEnum.AUTHOR.strategy.getValueAsString(document),
        PoetryFieldEnum.CONTENT.strategy.getValueAsString(document),
        PoetryFieldEnum.DYNASTY.strategy.getValueAsString(document),
        PoetryFieldEnum.TYPE.strategy.getValueAsString(document),
        PoetryFieldEnum.SCORE.strategy.getValueAsDouble(document));
  }

  public void setScore(double score) {
    if (score <= 0D) {
      throw new IllegalArgumentException("score > 0 : " + score);
    }
    this.score = score;
  }

  public Document toLuceneDocument() {
    Document document = new Document();
    for (PoetryFieldEnum fieldEnum : FIELD_ENUMS) {
      fieldEnum.strategy.appendTo(this, document);
    }
    return document;
  }
}
