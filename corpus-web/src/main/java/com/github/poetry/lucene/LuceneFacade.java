package com.github.poetry.lucene;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.entity.PoetryFieldEnum;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author zhaoyuyu
 * @since 2019/11/21
 */
@Slf4j
@Singleton
public final class LuceneFacade implements Function<QueryParams, List<GeneralChinesePoetry>> {

  private static final int MAX_HIGHLIGHT = Integer.MAX_VALUE - 8;

  private final IndexSearcher indexSearcher;
  private final QueryParser queryParser;
  private final Analyzer highlightAnalyzer;

  @Inject
  public LuceneFacade(
      IndexSearcher indexSearcher,
      QueryParser queryParser,
      @Named("standardAnalyzer") Analyzer analyzer) {
    this.indexSearcher = indexSearcher;
    this.queryParser = queryParser;
    this.highlightAnalyzer = analyzer;
  }

  @Override
  public List<GeneralChinesePoetry> apply(@NonNull QueryParams queryParams) {
    Query query = queryParser.apply(queryParams.getQuery());
    Formatter formatter = queryParams.getLuceneFormatter();
    QueryScorer queryScorer = new QueryScorer(query);
    Highlighter highlighter = new Highlighter(formatter, queryScorer);
    highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, MAX_HIGHLIGHT));
    highlighter.setMaxDocCharsToAnalyze(MAX_HIGHLIGHT);

    TopDocs topDocs;
    try {
      topDocs = indexSearcher.search(query, queryParams.getMaxSize());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    int size = topDocs.scoreDocs.length;
    if (size == 0) {
      return Collections.emptyList();
    }
    try {

      List<GeneralChinesePoetry> poetryList = new ArrayList<>(size);
      for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
        Document document = indexSearcher.doc(scoreDoc.doc);
        GeneralChinesePoetry poetry = GeneralChinesePoetry.fromLuceneDocument(document);
        poetry.setTitle(doHighlight(highlighter, PoetryFieldEnum.TITLE, poetry.getTitle()));
        poetry.setSubtitle(
            doHighlight(highlighter, PoetryFieldEnum.SUBTITLE, poetry.getSubtitle()));
        poetry.setAuthor(doHighlight(highlighter, PoetryFieldEnum.AUTHOR, poetry.getAuthor()));
        poetry.setContent(doHighlight(highlighter, PoetryFieldEnum.CONTENT, poetry.getContent()));
        poetryList.add(poetry);
      }

      return poetryList;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String doHighlight(Highlighter highlighter, PoetryFieldEnum poetryFieldEnum, String text)
      throws Exception {
    if (StringUtils.isNotEmpty(text)) {
      String hl = highlighter.getBestFragment(highlightAnalyzer, poetryFieldEnum.fieldName, text);
      return hl != null ? hl : text;
    }
    return text;
  }
}
