package com.github.poetry.lucene;

import com.google.common.io.CharSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.Reader;
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
    if (StringUtils.isEmpty(s)) {
      return Collections.emptyList();
    }
    try (Reader reader = CharSource.wrap(s).openStream()) {
      List<String> result = new ArrayList<>();
      TokenStream tokenStream = analyzer.tokenStream("", reader);
      tokenStream.reset();
      while (tokenStream.incrementToken()) {
        result.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
      }
      tokenStream.end();
      tokenStream.close();
      return result;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<String> apply(String s) {
    return tokenize(s);
  }
}
