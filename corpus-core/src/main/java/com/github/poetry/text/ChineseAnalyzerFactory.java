package com.github.poetry.text;

import com.github.poetry.stop.ChinesePunctuationSet;
import com.github.poetry.stop.ClassicChineseStopWordSet;
import com.github.poetry.stop.HtmlTagSet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author zhaoyuyu
 * @since 2019/11/21
 */
public final class ChineseAnalyzerFactory implements Supplier<Analyzer> {

  private static CharArraySet createStopWordSet() {
    List<Set<String>> setList =
        Arrays.asList(
            new ChinesePunctuationSet().get(),
            new ClassicChineseStopWordSet().get(),
            new HtmlTagSet().get());
    CharArraySet charArraySet = new CharArraySet(setList.stream().mapToInt(Set::size).sum(), true);
    setList.forEach(charArraySet::addAll);
    return CharArraySet.unmodifiableSet(charArraySet);
  }

  @Override
  public Analyzer get() {
    return new StandardAnalyzer(StopWordsHolder.INSTANCE.STOP_WORDS);
  }

  private enum StopWordsHolder {
    INSTANCE;

    private final CharArraySet STOP_WORDS = createStopWordSet();
  }
}
