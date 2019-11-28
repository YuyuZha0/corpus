package com.github.poetry.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
public final class GeneralChinesePoetry implements Serializable {

  private static final FieldEnum[] FIELD_ENUMS = FieldEnum.values();
  private static final Pattern CN_PTN = Pattern.compile("\\b[\u4e00-\u9fa5]|\u4e00-\u9fa5]\\b");
  private final String title;
  private final String subtitle;
  private final String author;
  private final String content;
  private final String dynasty;
  private final String type;

  public static GeneralChinesePoetry fromLuceneDocument(@NonNull Document document) throws Exception {
    return new GeneralChinesePoetry(
        getFieldValue(FieldEnum.TITLE, document),
        getFieldValue(FieldEnum.SUBTITLE, document),
        getFieldValue(FieldEnum.AUTHOR, document),
        getFieldValue(FieldEnum.CONTENT, document),
        getFieldValue(FieldEnum.DYNASTY, document),
        getFieldValue(FieldEnum.TYPE, document));
  }

  private static String getFieldValue(FieldEnum fieldEnum, Document document) {
    IndexableField field = document.getField(fieldEnum.fieldName);
    if (field != null) {
      return field.stringValue();
    }
    return null;
  }

  public GeneralChinesePoetry map(@NonNull BiFunction<FieldEnum, String, String> mapper) {
    return new GeneralChinesePoetry(
        mapper.apply(FieldEnum.TITLE, title),
        mapper.apply(FieldEnum.SUBTITLE, subtitle),
        mapper.apply(FieldEnum.AUTHOR, author),
        mapper.apply(FieldEnum.CONTENT, content),
        mapper.apply(FieldEnum.DYNASTY, dynasty),
        mapper.apply(FieldEnum.TYPE, type));
  }

  public EnumMap<FieldEnum, String> toEnumMap() {
    EnumMap<FieldEnum, String> map = new EnumMap<>(FieldEnum.class);
    for (FieldEnum fieldEnum : FIELD_ENUMS) {
      map.put(fieldEnum, fieldEnum.get(this));
    }
    return map;
  }

  public Document toLuceneDocument() {
    Document document = new Document();
    for (FieldEnum fieldEnum : FIELD_ENUMS) {
      String value = fieldEnum.get(this);
      if (StringUtils.isBlank(value)) continue;
      if (fieldEnum.tokenizable) {
        TextField textField = new TextField(fieldEnum.fieldName, value, Store.YES);
        document.add(textField);
      } else {
        StringField stringField = new StringField(fieldEnum.fieldName, value, Store.YES);
        document.add(stringField);
      }
    }
    return document;
  }
}
