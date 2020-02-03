package com.github.poetry.entity;

import org.apache.lucene.document.Document;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
public interface FieldStrategy {

  String getName();

  void appendTo(GeneralChinesePoetry poetry, Document doc);

  default String getValueAsString(Document doc) {
    return doc.getField(getName()).stringValue();
  }

  default double getValueAsDouble(Document doc) {
    return doc.getField(getName()).numericValue().doubleValue();
  }
}
