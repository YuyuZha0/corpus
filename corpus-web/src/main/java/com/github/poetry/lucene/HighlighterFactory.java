package com.github.poetry.lucene;

import com.github.poetry.entity.DocField;
import com.github.poetry.entity.GeneralChinesePoetry;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author zhaoyuyu
 * @since 2020/8/23
 */
@Singleton
public final class HighlighterFactory
    implements Function<QueryParams, Consumer<GeneralChinesePoetry>> {

  private static final int MAX_HIGHLIGHT = Integer.MAX_VALUE - 8;

  private final Tokenizer tokenizer;

  @Inject
  public HighlighterFactory(@Named("standardAnalyzer") Analyzer analyzer) {
    this.tokenizer = new Tokenizer(analyzer);
  }

  private static PhraseQuery makePhraseQuery(DocField docField, String[] tokens) {
    return new PhraseQuery(QueryParser.phraseQuerySlop(tokens), docField.getName(), tokens);
  }

  @Override
  public Consumer<GeneralChinesePoetry> apply(QueryParams queryParams) {
    Formatter formatter = queryParams.getLuceneFormatter();
    QueryScorer queryScorer = new QueryScorer(makeQuery(queryParams.getQuery()));
    Highlighter highlighter = new Highlighter(formatter, queryScorer);
    highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, MAX_HIGHLIGHT));
    highlighter.setMaxDocCharsToAnalyze(MAX_HIGHLIGHT);

    return poetry -> {
      try {
        poetry.setTitle(doHighlight(highlighter, DocField.TITLE, poetry.getTitle()));
        poetry.setSubtitle(doHighlight(highlighter, DocField.SUBTITLE, poetry.getSubtitle()));
        poetry.setAuthor(doHighlight(highlighter, DocField.AUTHOR, poetry.getAuthor()));
        poetry.setContent(doHighlight(highlighter, DocField.CONTENT, poetry.getContent()));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };
  }

  private Query makeQuery(String query) {
    BooleanQuery.Builder builder = new Builder();
    builder.add(new TermQuery(new Term(DocField.AUTHOR.getName(), query)), Occur.SHOULD);
    String[] tokens = tokenizer.apply(query).toArray(new String[0]);
    builder.add(makePhraseQuery(DocField.SUBTITLE, tokens), Occur.SHOULD);
    builder.add(makePhraseQuery(DocField.TITLE, tokens), Occur.SHOULD);
    builder.add(makePhraseQuery(DocField.CONTENT, tokens), Occur.SHOULD);

    return builder.build();
  }

  private String doHighlight(Highlighter highlighter, DocField docField, String text)
      throws Exception {
    if (StringUtils.isNotEmpty(text)) {
      String hl = highlighter.getBestFragment(tokenizer.getAnalyzer(), docField.getName(), text);
      return hl != null ? hl : text;
    }
    return text;
  }
}
