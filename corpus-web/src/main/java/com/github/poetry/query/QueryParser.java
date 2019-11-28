package com.github.poetry.query;

import com.github.poetry.entity.FieldEnum;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import lombok.NonNull;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author zhaoyuyu
 * @since 2019/11/21
 */
final class QueryParser implements Function<String, Query> {

  private static final float TERM_BOOST_FACTOR = 5f;

  private final Analyzer analyzer;

  QueryParser(Analyzer analyzer) {
    this.analyzer = analyzer;
  }

  @SuppressWarnings("UnstableApiUsage")
  private static List<String> splitWithBlank(String s) {
    return Splitter.on(CharMatcher.breakingWhitespace()).omitEmptyStrings().splitToList(s);
  }

  private static int phraseQuerySlop(String[] tokens) {
    switch (tokens.length) {
      case 0:
      case 1:
      case 2:
        return 0;
      case 3:
        return 1;
      case 4:
      case 5:
        return 2;
      default:
        return 3;
    }
  }

  private Query makeCompositeQuery(String s) {
    BooleanQuery.Builder builder = new Builder();
    builder.add(makeTermQuery(FieldEnum.AUTHOR, s), Occur.SHOULD);
    builder.add(makeTermQuery(FieldEnum.DYNASTY, s), Occur.SHOULD);
    builder.add(makeTermQuery(FieldEnum.TYPE, s), Occur.SHOULD);
    String[] tokens = tokenize(s);
    if (tokens.length == 0) {
      return builder.build();
    }
    if (tokens.length == 1) {
      String token = tokens[0];
      builder.add(makeTermQuery(FieldEnum.TITLE, token), Occur.SHOULD);
      builder.add(makeTermQuery(FieldEnum.SUBTITLE, token), Occur.SHOULD);
      builder.add(makeTermQuery(FieldEnum.CONTENT, token), Occur.SHOULD);
      return builder.build();
    }
    builder.add(makeTokenizedQuery(FieldEnum.TITLE, tokens), Occur.SHOULD);
    builder.add(makeTokenizedQuery(FieldEnum.SUBTITLE, tokens), Occur.SHOULD);
    builder.add(makeTokenizedQuery(FieldEnum.CONTENT, tokens), Occur.SHOULD);
    return builder.build();
  }

  private Query makeTermQuery(FieldEnum fieldEnum, String s) {
    return new BoostQuery(new TermQuery(new Term(fieldEnum.fieldName, s)), TERM_BOOST_FACTOR);
  }

  private Query makeTokenizedQuery(FieldEnum fieldEnum, String[] tokens) {
    return new PhraseQuery(phraseQuerySlop(tokens), fieldEnum.fieldName, tokens);
  }

  private String[] tokenize(String s) {
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
    return result.toArray(new String[0]);
  }

  @Override
  public Query apply(@NonNull String s) {
    List<String> parts = splitWithBlank(s);
    if (parts.isEmpty()) {
      return new MatchNoDocsQuery();
    }
    if (parts.size() == 1) {
      return makeCompositeQuery(parts.get(0));
    }
    BooleanQuery.Builder builder = new Builder();
    for (String part : parts) {
      builder.add(makeCompositeQuery(part), Occur.MUST);
    }
    return builder.build();
  }
}
