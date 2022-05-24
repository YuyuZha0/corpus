package com.github.poetry.lucene;

import com.github.poetry.entity.DocField;
import com.github.poetry.entity.TextFieldStrategy;
import com.github.poetry.stop.ChinesePunctuationSet;
import com.github.poetry.stop.ClassicChineseStopWordSet;
import com.google.inject.Singleton;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhaoyuyu
 * @since 2022/5/24
 */
@Singleton
public final class QueryRewriter implements Function<String, Query> {

  private static final Pattern HAN_PATTERN = Pattern.compile("[\u4e00-\u9fa5]+");
  private static final StopRecognition STOP_RECOGNITION = createStopRecognition();

  private static StopRecognition createStopRecognition() {
    StopRecognition stopRecognition = new StopRecognition();
    stopRecognition.insertStopWords(new ChinesePunctuationSet().get());
    stopRecognition.insertStopWords(new ClassicChineseStopWordSet().get());
    return stopRecognition;
  }

  private static List<Term> getTerms(String input) {
    List<Term> terms = new ArrayList<>();
    Matcher matcher = HAN_PATTERN.matcher(input);
    while (matcher.find()) {
      String m = matcher.group();
      Result result = IndexAnalysis.parse(m).recognition(STOP_RECOGNITION);
      terms.addAll(result.getTerms());
    }
    return terms;
  }

  private static int phraseQuerySlop(int len) {
    switch (len) {
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

  private static boolean isNoun(Term term) {
    String natureStr = term.getNatureStr();
    return StringUtils.isNotEmpty(natureStr)
        && natureStr.startsWith("n")
        && !"null".equals(natureStr);
  }

  private List<Query> forTerm(List<Term> terms) {
    String[] tokens = terms.stream().map(Term::getName).toArray(String[]::new);
    String[] nounTokens =
        terms.stream().filter(QueryRewriter::isNoun).map(Term::getName).toArray(String[]::new);
    List<Query> collect = new ArrayList<>();
    collect.add(makeQuery(DocField.TITLE, tokens));
    collect.add(makeQuery(DocField.SUBTITLE, tokens));
    collect.add(makeQuery(DocField.CONTENT, tokens));
    if (nounTokens.length > 0) {
      collect.add(makeQuery(DocField.DYNASTY, tokens));
      collect.add(makeQuery(DocField.AUTHOR, tokens));
      collect.add(makeQuery(DocField.TYPE, tokens));
    }
    return collect;
  }

  private Query makeQuery(DocField docField, String[] tokens) {
    if (docField.getStrategy() instanceof TextFieldStrategy) {
      return new PhraseQuery(phraseQuerySlop(tokens.length), docField.getName(), tokens);
    }
    BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
    for (String token : tokens) {
      booleanQueryBuilder.add(
          new TermQuery(new org.apache.lucene.index.Term(docField.getName(), token)), Occur.SHOULD);
    }
    return booleanQueryBuilder.build();
  }

  @Override
  public Query apply(String input) {
    List<Term> terms;
    if (StringUtils.isBlank(input) || (terms = getTerms(input)).isEmpty()) {
      return new MatchNoDocsQuery();
    }
    return wrapWithScore(new DisjunctionMaxQuery(forTerm(terms), 0.15f));
  }

  private Query wrapWithScore(Query in) {
    return FunctionScoreQuery.boostByValue(
        in, DoubleValuesSource.fromDoubleField(DocField.SCORE.getName()));
  }
}
