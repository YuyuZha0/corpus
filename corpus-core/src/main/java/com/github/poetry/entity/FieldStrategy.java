package com.github.poetry.entity;

import org.apache.lucene.document.Document;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
public interface FieldStrategy {

  String getName();

  void writeDoc(GeneralChinesePoetry poetry, Document doc);

  void readDoc(Document doc, GeneralChinesePoetry poetry);
}
