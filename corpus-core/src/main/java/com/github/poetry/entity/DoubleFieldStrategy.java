package com.github.poetry.entity;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;

import java.util.function.ObjDoubleConsumer;
import java.util.function.ToDoubleFunction;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
@RequiredArgsConstructor
public final class DoubleFieldStrategy implements FieldStrategy {

  private final String name;

  private final ToDoubleFunction<? super GeneralChinesePoetry> getter;

  private final ObjDoubleConsumer<? super GeneralChinesePoetry> setter;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void writeDoc(GeneralChinesePoetry poetry, Document doc) {
    double score = getter.applyAsDouble(poetry);
    doc.add(new StoredField(getName(), score));
    doc.add(new DoubleDocValuesField(getName(), score));
  }

  @Override
  public void readDoc(Document doc, GeneralChinesePoetry poetry) {
    IndexableField field = doc.getField(getName());
    if (field != null) {
      setter.accept(poetry, field.numericValue().doubleValue());
    }
  }
}
