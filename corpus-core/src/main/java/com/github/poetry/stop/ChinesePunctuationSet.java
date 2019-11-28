package com.github.poetry.stop;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public final class ChinesePunctuationSet implements StopWordSet {

  @Override
  public Set<String> get() {
    return ImmutableSet.of(
        "\uff0c", "\uff01", "\uff1f", "\uff1b", "\uff1a", "\uff08", "\uff09", "\uff3b", "\uff3d",
        "\u3010", "\u3011", "\u3002", "\ufe12", "\u300e", "\u300f", "\ufe41", "\ufe42", "\u300c",
        "\u300d", "\u3001", "\u3000");
  }
}
