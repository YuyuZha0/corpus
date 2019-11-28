package com.github.poetry.stop;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public final class HtmlTagSet implements StopWordSet {

  @Override
  public Set<String> get() {
    return ImmutableSet.of("<p>", "</p>", "<B>", "<div>", "</div>", "<span>", "</span>", "<br>");
  }
}
