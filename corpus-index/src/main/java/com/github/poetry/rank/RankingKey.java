package com.github.poetry.rank;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

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
    return Objects.equals(author, that.author) && Objects.equals(title, that.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(author, title);
  }
}
