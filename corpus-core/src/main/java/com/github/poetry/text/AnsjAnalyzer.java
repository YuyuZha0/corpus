package com.github.poetry.text;

import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.Analysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author zhaoyuyu
 * @since 2022/5/24
 */
public final class AnsjAnalyzer extends Analyzer {

  private final List<Set<String>> stopWords;

  public AnsjAnalyzer(List<Set<String>> stopWords) {
    this.stopWords = stopWords;
  }

  @Override
  protected TokenStreamComponents createComponents(String s) {
    Analysis analysis = new IndexAnalysis();
    if (s != null && !s.isEmpty()) {
      analysis.resetContent(new StringReader(s));
    }
    Tokenizer tokenizer = new AnsjTokenizer(analysis, stopRecognitions(), null);
    return new TokenStreamComponents(tokenizer);
  }

  private List<StopRecognition> stopRecognitions() {
    if (stopWords == null) {
      return null;
    }
    StopRecognition stopRecognition = new StopRecognition();
    for (Set<String> set : stopWords) {
      stopRecognition.insertStopWords(set);
    }
    return Collections.singletonList(stopRecognition);
  }
}
