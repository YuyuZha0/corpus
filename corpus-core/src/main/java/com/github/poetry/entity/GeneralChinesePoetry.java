package com.github.poetry.entity;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
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
import java.lang.Character.UnicodeScript;
import java.util.EnumMap;
import java.util.function.BiFunction;

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

  private static final PoetryFieldEnum[] FIELD_ENUMS = PoetryFieldEnum.values();
  // private static final Pattern CN_PTN = Pattern.compile("\\b[\u4e00-\u9fa5]|\u4e00-\u9fa5]\\b");
  private final String title;
  private final String subtitle;
  private final String author;
  private final String content;
  private final String dynasty;
  private final String type;

  public static GeneralChinesePoetry fromLuceneDocument(@NonNull Document document)
      throws Exception {
    return new GeneralChinesePoetry(
        getFieldValue(PoetryFieldEnum.TITLE, document),
        getFieldValue(PoetryFieldEnum.SUBTITLE, document),
        getFieldValue(PoetryFieldEnum.AUTHOR, document),
        getFieldValue(PoetryFieldEnum.CONTENT, document),
        getFieldValue(PoetryFieldEnum.DYNASTY, document),
        getFieldValue(PoetryFieldEnum.TYPE, document));
  }

  private static String getFieldValue(PoetryFieldEnum fieldEnum, Document document) {
    IndexableField field = document.getField(fieldEnum.fieldName);
    if (field != null) {
      return field.stringValue();
    }
    return null;
  }

  private static boolean isChineseCharacter(String s, int index) {
    int codePoint = s.codePointAt(index);
    return Character.UnicodeScript.of(codePoint) == UnicodeScript.HAN;
  }

  public GeneralChinesePoetry map(@NonNull BiFunction<PoetryFieldEnum, String, String> mapper) {
    return new GeneralChinesePoetry(
        mapper.apply(PoetryFieldEnum.TITLE, title),
        mapper.apply(PoetryFieldEnum.SUBTITLE, subtitle),
        mapper.apply(PoetryFieldEnum.AUTHOR, author),
        mapper.apply(PoetryFieldEnum.CONTENT, content),
        mapper.apply(PoetryFieldEnum.DYNASTY, dynasty),
        mapper.apply(PoetryFieldEnum.TYPE, type));
  }

  public EnumMap<PoetryFieldEnum, String> toEnumMap() {
    EnumMap<PoetryFieldEnum, String> map = new EnumMap<>(PoetryFieldEnum.class);
    for (PoetryFieldEnum fieldEnum : FIELD_ENUMS) {
      map.put(fieldEnum, fieldEnum.get(this));
    }
    return map;
  }

  public Document toLuceneDocument() {
    Document document = new Document();
    for (PoetryFieldEnum fieldEnum : FIELD_ENUMS) {
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

  @SuppressWarnings("UnstableApiUsage")
  public long contentHash() {
    String content = this.content;
    if (content == null || content.isEmpty()) return 0L;
    int len = content.length();
    Hasher hasher = Hashing.murmur3_128().newHasher(len);
    for (int i = 0; i < len; ++i) {
      if (isChineseCharacter(content, i)) {
        hasher.putChar(content.charAt(i));
      }
    }
    return hasher.hash().asLong();
  }
}
