package com.github.poetry.entity;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import java.util.function.Function;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
public interface FieldStrategy<T extends IndexableField> extends Function<GeneralChinesePoetry, T> {

  String getName();

  default String getValueAsString(Document doc) {
    return doc.getField(getName()).stringValue();
  }

  default double getValueAsDouble(Document doc) {
    return doc.getField(getName()).numericValue().doubleValue();
  }
}
