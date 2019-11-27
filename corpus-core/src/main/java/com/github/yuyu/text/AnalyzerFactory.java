package com.github.yuyu.text;

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
public final class AnalyzerFactory implements Supplier<Analyzer> {

  @Override
  public Analyzer get() {
    List<String> temp = new ArrayList<>();
    temp.addAll(StopWordUtils.getTraditionalChineseStopWordsSet());
    temp.addAll(StopWordUtils.getFullWidthPunctuationSet());
    CharArraySet stopWords = new CharArraySet(temp, false);
    return new StandardAnalyzer(CharArraySet.unmodifiableSet(stopWords));
  }
}
