package com.github.poetry.rank;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RankingKey {

  private final String author;

  private final String title;

  static RankingKey of(String title, String author) {
    return new RankingKey(author, StringUtils.abbreviate(title, 16));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RankingKey that = (RankingKey) o;

    if (author != null ? !author.equals(that.author) : that.author != null) return false;
    return title != null ? title.equals(that.title) : that.title == null;
  }

  @Override
  public int hashCode() {
    int result = author != null ? author.hashCode() : 0;
    result = 31 * result + (title != null ? title.hashCode() : 0);
    return result;
  }
}
