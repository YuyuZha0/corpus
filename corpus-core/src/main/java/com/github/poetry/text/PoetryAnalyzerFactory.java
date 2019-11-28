package com.github.poetry.text;

import com.github.poetry.stop.ChinesePunctuationSet;
import com.github.poetry.stop.HtmlTagSet;
import com.github.poetry.stop.TraditionalChineseStopWordSet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author zhaoyuyu
 * @since 2019/11/21
 */
public final class PoetryAnalyzerFactory implements Supplier<Analyzer> {

  private static CharArraySet createStopWordSet() {
    List<String> temp = new ArrayList<>();
    temp.addAll(new ChinesePunctuationSet().get());
    temp.addAll(new TraditionalChineseStopWordSet().get());
    temp.addAll(new HtmlTagSet().get());
    return CharArraySet.unmodifiableSet(new CharArraySet(temp, true));
  }

  @Override
  public Analyzer get() {
    return new StandardAnalyzer(StopWordsHolder.STOP_WORDS);
  }

  private static final class StopWordsHolder {
    private static final CharArraySet STOP_WORDS = createStopWordSet();
  }
}
