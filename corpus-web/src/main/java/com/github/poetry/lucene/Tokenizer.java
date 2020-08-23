package com.github.poetry.lucene;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author zhaoyuyu
 * @since 2020/8/23
 */
@RequiredArgsConstructor
public final class Tokenizer implements Function<String, List<String>> {

  @Getter private final Analyzer analyzer;

  List<String> tokenize(String s) {
    if (s == null || s.isEmpty()) return Collections.emptyList();
    List<String> result = new ArrayList<>();
    TokenStream tokenStream = analyzer.tokenStream("", new StringReader(s));
    try {
      tokenStream.reset();
      while (tokenStream.incrementToken()) {
        result.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
      }
      tokenStream.end();
      tokenStream.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  @Override
  public List<String> apply(String s) {
    return tokenize(s);
  }
}
