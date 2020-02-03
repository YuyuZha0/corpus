package com.github.poetry.entity;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
public interface FieldStrategy {

  String getName();

  void appendTo(GeneralChinesePoetry poetry, Document doc);

  default String getValueAsString(Document doc) {
    IndexableField field = doc.getField(getName());
    if (field != null) {
      return field.stringValue();
    }
    return null;
  }

  default double getValueAsDouble(Document doc) {
    IndexableField field = doc.getField(getName());
    if (field != null) {
      return field.numericValue().doubleValue();
    }
    return 0D;
  }
}
