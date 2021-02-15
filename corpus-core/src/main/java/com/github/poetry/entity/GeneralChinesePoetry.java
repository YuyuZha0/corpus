package com.github.poetry.entity;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.lucene.document.Document;

import java.io.Serializable;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public final class GeneralChinesePoetry implements Serializable {

  private static final DocField[] FIELD_ENUMS = DocField.values();
  // private static final Pattern CN_PTN = Pattern.compile("\\b[\u4e00-\u9fa5]|\u4e00-\u9fa5]\\b");
  private String title;
  private String subtitle;
  private String author;
  private String content;
  private String dynasty;
  private String type;
  private double score = 1D;

  public static GeneralChinesePoetry fromLuceneDocument(@NonNull Document document) {
    GeneralChinesePoetry poetry = new GeneralChinesePoetry();
    for (DocField docField : FIELD_ENUMS) {
      docField.getStrategy().readDoc(document, poetry);
    }
    return poetry;
  }

  public void setScore(double score) {
    Preconditions.checkArgument(score > 0, "illegal score: %s", score);
    this.score = score;
  }

  public Document toLuceneDocument() {
    Document document = new Document();
    for (DocField fieldEnum : FIELD_ENUMS) {
      fieldEnum.getStrategy().writeDoc(this, document);
    }
    return document;
  }
}
