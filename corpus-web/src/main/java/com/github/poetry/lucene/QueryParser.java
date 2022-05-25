package com.github.poetry.lucene;

import com.github.poetry.entity.DocField;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.NonNull;
import org.apache.lucene.analysis.Analyzer;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author zhaoyuyu
 * @since 2019/11/21
 */
@Singleton
@Deprecated
public final class QueryParser implements Function<String, Query> {

  private static final float TERM_BOOST_FACTOR = 3f;
  private static final float DISJUNCTION_MAX_TIE_BREAKER = 0.15f;

  private final Tokenizer tokenizer;

  @Inject
  public QueryParser(@Named("indexAnalyzer") Analyzer analyzer) {
    this.tokenizer = new Tokenizer(analyzer);
  }

  private static List<String> splitWithBlank(String s) {
    return Splitter.on(CharMatcher.breakingWhitespace()).omitEmptyStrings().splitToList(s);
  }

  static int phraseQuerySlop(String[] tokens) {
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
    return FunctionScoreQuery.boostByValue(
        in, DoubleValuesSource.fromDoubleField(DocField.SCORE.getName()));
  }

  private Query makeCompositeQuery(String s) {
    List<Query> disjuncts = new ArrayList<>(6);
    disjuncts.add(makeTermQuery(DocField.AUTHOR, s));
    disjuncts.add(makeTermQuery(DocField.DYNASTY, s));
    disjuncts.add(makeTermQuery(DocField.TYPE, s));
    String[] tokens = tokenizer.apply(s).toArray(new String[0]);
    if (tokens.length == 0) {
      return new DisjunctionMaxQuery(disjuncts, DISJUNCTION_MAX_TIE_BREAKER);
    }
    if (tokens.length == 1) {
      String token = tokens[0];
      disjuncts.add(makeTermQuery(DocField.TITLE, token));
      disjuncts.add(makeTermQuery(DocField.SUBTITLE, token));
      disjuncts.add(makeTermQuery(DocField.CONTENT, token));
      return new DisjunctionMaxQuery(disjuncts, DISJUNCTION_MAX_TIE_BREAKER);
    }
    disjuncts.add(makeTokenizedQuery(DocField.TITLE, tokens));
    disjuncts.add(makeTokenizedQuery(DocField.SUBTITLE, tokens));
    disjuncts.add(makeTokenizedQuery(DocField.CONTENT, tokens));
    return new DisjunctionMaxQuery(disjuncts, DISJUNCTION_MAX_TIE_BREAKER);
  }

  private Query makeTermQuery(DocField docField, String s) {
    return new BoostQuery(new TermQuery(new Term(docField.getName(), s)), TERM_BOOST_FACTOR);
  }

  private Query makeTokenizedQuery(DocField docField, String[] tokens) {
    return new PhraseQuery(phraseQuerySlop(tokens), docField.getName(), tokens);
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
