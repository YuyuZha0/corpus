package com.github.poetry.lucene;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author fishzhao
 * @since 2020-08-20
 */
@Getter
@ToString
public final class QueryParams implements Serializable {

  private final String query;
  private final String preTag;
  private final String postTag;
  private final int maxSize;

  public QueryParams(@NonNull String query, String preTag, String postTag, int maxSize) {
    this.query = query;
    this.preTag = preTag;
    this.postTag = postTag;
    this.maxSize = maxSize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    QueryParams that = (QueryParams) o;
    return maxSize == that.maxSize
        && query.equals(that.query)
        && Objects.equals(preTag, that.preTag)
        && Objects.equals(postTag, that.postTag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(query, preTag, postTag, maxSize);
  }

  Formatter getLuceneFormatter() {
    if (StringUtils.isNotEmpty(preTag) && StringUtils.isNotEmpty(postTag)) {
      return new SimpleHTMLFormatter(preTag, postTag);
    }
    return new SimpleHTMLFormatter();
  }
}
