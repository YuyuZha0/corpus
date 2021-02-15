package com.github.poetry.entity;

import com.github.poetry.text.TextUtil;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
@RequiredArgsConstructor
public final class TextFieldStrategy implements FieldStrategy {

  private final String name;
  private final Function<? super GeneralChinesePoetry, String> getter;
  private final BiConsumer<? super GeneralChinesePoetry, String> setter;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void writeDoc(GeneralChinesePoetry poetry, Document doc) {
    String text = getter.apply(poetry);
    if (TextUtil.isBlank(text)) {
      return;
    }
    doc.add(new TextField(getName(), text, Store.YES));
  }

  @Override
  public void readDoc(Document doc, GeneralChinesePoetry poetry) {
    IndexableField field = doc.getField(getName());
    if (field != null) {
      setter.accept(poetry, field.stringValue());
    }
  }
}
