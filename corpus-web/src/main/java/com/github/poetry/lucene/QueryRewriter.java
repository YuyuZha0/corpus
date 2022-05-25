package com.github.poetry.lucene;

import com.github.poetry.entity.DocField;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
  private static final float DISJUNCTION_MAX_TIE_BREAKER = 0.15f;
  private static final Map<String, DocField> VOCAB_DOC_FILED_MAP =
      ImmutableMap.<String, DocField>builder()
          .put("唐", DocField.DYNASTY)
          .put("宋", DocField.DYNASTY)
          .put("元", DocField.DYNASTY)
          .put("先秦", DocField.DYNASTY)
          .put("五代十国", DocField.DYNASTY)
          .put("诗", DocField.TYPE)
          .put("词", DocField.TYPE)
          .put("曲", DocField.TYPE)
          .build();

  private final Tokenizer tokenizer;

  @Inject
  public QueryRewriter(@Named("indexAnalyzer") Analyzer analyzer) {
    this.tokenizer = new Tokenizer(analyzer);
  }

  private static List<String> getSegments(String input) {
    List<String> segments = new ArrayList<>();
    Matcher matcher = HAN_PATTERN.matcher(input);
    while (matcher.find()) {
      String m = matcher.group();
      segments.add(m);
    }
    return segments;
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

  @Override
  public Query apply(String input) {
    List<String> segments;
    if (StringUtils.isBlank(input) || (segments = getSegments(input)).isEmpty()) {
      return new MatchNoDocsQuery();
    }
    if (segments.size() == 1) {
      return wrapWithScore(forSegment(segments.get(0)));
    }
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    for (String s : segments) {
      builder.add(forSegment(s), Occur.MUST);
    }
    return wrapWithScore(builder.build());
  }

  private Query forSegment(String segment) {
    DocField docField = VOCAB_DOC_FILED_MAP.get(segment);
    if (docField != null) {
      return new TermQuery(new Term(docField.getName(), segment));
    }

    String[] tokens = tokenizer.tokenize(segment).toArray(new String[0]);
    if (tokens.length == 1) {
      BooleanQuery.Builder builder = new BooleanQuery.Builder();
      for (DocField f :
          Arrays.asList(DocField.CONTENT, DocField.AUTHOR, DocField.TITLE, DocField.SUBTITLE)) {
        builder.add(new TermQuery(new Term(f.getName(), tokens[0])), Occur.SHOULD);
      }
      return builder.build();
    }

    int slop = phraseQuerySlop(tokens.length);
    return new DisjunctionMaxQuery(
        Arrays.asList(
            new PhraseQuery(slop, DocField.TITLE.getName(), tokens),
            new PhraseQuery(slop, DocField.SUBTITLE.getName(), tokens),
            new PhraseQuery(slop, DocField.CONTENT.getName(), tokens),
            new TermQuery(new Term(DocField.AUTHOR.getName(), segment))),
        DISJUNCTION_MAX_TIE_BREAKER);
  }

  private Query wrapWithScore(Query in) {
    return FunctionScoreQuery.boostByValue(
        in, DoubleValuesSource.fromDoubleField(DocField.SCORE.getName()));
  }
}
