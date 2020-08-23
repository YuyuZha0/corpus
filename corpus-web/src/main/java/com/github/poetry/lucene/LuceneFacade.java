package com.github.poetry.lucene;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author zhaoyuyu
 * @since 2019/11/21
 */
@Slf4j
@Singleton
public final class LuceneFacade implements Function<QueryParams, List<GeneralChinesePoetry>> {

  private final IndexSearcher indexSearcher;
  private final QueryParser queryParser;
  private final HighlighterFactory highlighterFactory;

  @Inject
  public LuceneFacade(
      IndexSearcher indexSearcher, QueryParser queryParser, HighlighterFactory highlighterFactory) {
    this.indexSearcher = indexSearcher;
    this.queryParser = queryParser;
    this.highlighterFactory = highlighterFactory;
  }

  @Override
  public List<GeneralChinesePoetry> apply(@NonNull QueryParams queryParams) {
    Query query = queryParser.apply(queryParams.getQuery());

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

    Consumer<GeneralChinesePoetry> highlighter = highlighterFactory.apply(queryParams);
    try {
      List<GeneralChinesePoetry> poetryList = new ArrayList<>(size);
      for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
        Document document = indexSearcher.doc(scoreDoc.doc);
        GeneralChinesePoetry poetry = GeneralChinesePoetry.fromLuceneDocument(document);
        highlighter.accept(poetry);
        poetryList.add(poetry);
      }
      return poetryList;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
