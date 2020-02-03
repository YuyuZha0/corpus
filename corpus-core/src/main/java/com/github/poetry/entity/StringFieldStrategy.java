package com.github.poetry.entity;

import com.github.poetry.text.TextUtils;
import lombok.NonNull;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;

import java.util.function.Function;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
public final class StringFieldStrategy implements FieldStrategy {

  private final String name;
  private final Function<? super GeneralChinesePoetry, String> getter;

  public StringFieldStrategy(
      @NonNull String name, @NonNull Function<? super GeneralChinesePoetry, String> getter) {
    this.name = name;
    this.getter = getter;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void appendTo(GeneralChinesePoetry poetry, Document doc) {
    String text = getter.apply(poetry);
    if (TextUtils.isBlank(text)) {
      return;
    }
    doc.add(new StringField(getName(), text, Store.YES));
  }

  @Override
  public double getValueAsDouble(Document doc) {
    throw new UnsupportedOperationException();
  }
}
