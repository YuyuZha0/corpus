package com.github.poetry.rank;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RankingKey {

  private final String author;

  private final String title;

  static RankingKey of(String title, String author) {
    if (title != null && title.length() > 16) {
      title = title.substring(0, 16);
    }
    return new RankingKey(author, title);
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
