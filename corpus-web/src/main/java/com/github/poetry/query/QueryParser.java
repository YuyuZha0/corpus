package com.github.poetry.query;

import com.github.poetry.entity.PoetryFieldEnum;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import lombok.NonNull;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.DoubleValuesSource;
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

  private static final float TERM_BOOST_FACTOR = 3f;
  private static final float DISJUNCTION_MAX_TIE_BREAKER = 0.15f;

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

  private Query wrapWithScore(Query in) {
    return FunctionScoreQuery.boostByValue(in, DoubleValuesSource.fromDoubleField("score"));
  }

  private Query makeCompositeQuery(String s) {
    List<Query> disjuncts = new ArrayList<>(6);
    disjuncts.add(makeTermQuery(PoetryFieldEnum.AUTHOR, s));
    disjuncts.add(makeTermQuery(PoetryFieldEnum.DYNASTY, s));
    disjuncts.add(makeTermQuery(PoetryFieldEnum.TYPE, s));
    String[] tokens = tokenize(s);
    if (tokens.length == 0) {
      return new DisjunctionMaxQuery(disjuncts, DISJUNCTION_MAX_TIE_BREAKER);
    }
    if (tokens.length == 1) {
      String token = tokens[0];
      disjuncts.add(makeTermQuery(PoetryFieldEnum.TITLE, token));
      disjuncts.add(makeTermQuery(PoetryFieldEnum.SUBTITLE, token));
      disjuncts.add(makeTermQuery(PoetryFieldEnum.CONTENT, token));
      return new DisjunctionMaxQuery(disjuncts, DISJUNCTION_MAX_TIE_BREAKER);
    }
    disjuncts.add(makeTokenizedQuery(PoetryFieldEnum.TITLE, tokens));
    disjuncts.add(makeTokenizedQuery(PoetryFieldEnum.SUBTITLE, tokens));
    disjuncts.add(makeTokenizedQuery(PoetryFieldEnum.CONTENT, tokens));
    return new DisjunctionMaxQuery(disjuncts, DISJUNCTION_MAX_TIE_BREAKER);
  }

  private Query makeTermQuery(PoetryFieldEnum fieldEnum, String s) {
    return new BoostQuery(new TermQuery(new Term(fieldEnum.fieldName, s)), TERM_BOOST_FACTOR);
  }

  private Query makeTokenizedQuery(PoetryFieldEnum fieldEnum, String[] tokens) {
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
      return wrapWithScore(makeCompositeQuery(parts.get(0)));
    }
    BooleanQuery.Builder builder = new Builder();
    for (String part : parts) {
      builder.add(makeCompositeQuery(part), Occur.MUST);
    }
    return wrapWithScore(builder.build());
  }
}
