package com.github.poetry.entity;

import lombok.NonNull;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.StoredField;

import java.util.function.ToDoubleFunction;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
public final class DoubleFieldStrategy implements FieldStrategy {

  private final String name;

  private final ToDoubleFunction<? super GeneralChinesePoetry> getter;

  public DoubleFieldStrategy(
      @NonNull String name, @NonNull ToDoubleFunction<? super GeneralChinesePoetry> getter) {
    this.name = name;
    this.getter = getter;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void appendTo(GeneralChinesePoetry poetry, Document doc) {
    double score = getter.applyAsDouble(poetry);
    doc.add(new StoredField(getName(), score));
    doc.add(new DoubleDocValuesField(getName(), score));
  }

  @Override
  public String getValueAsString(Document doc) {
    throw new UnsupportedOperationException();
  }
}
