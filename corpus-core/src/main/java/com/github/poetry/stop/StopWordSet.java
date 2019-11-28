package com.github.poetry.stop;

import org.apache.lucene.analysis.CharArraySet;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public interface StopWordSet extends Supplier<Set<String>> {

  default CharArraySet charArraySet() {
    return new CharArraySet(get(), false);
  }

  @Override
  Set<String> get();
}
