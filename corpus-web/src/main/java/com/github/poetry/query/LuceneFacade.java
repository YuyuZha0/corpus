package com.github.poetry.query;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.entity.PoetryFieldEnum;
import com.github.poetry.text.PoetryAnalyzerFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author zhaoyuyu
 * @since 2019/11/21
 */
@Slf4j
public final class LuceneFacade {

  private static final int MAX_HIGHLIGHT = Integer.MAX_VALUE - 8;

  private final Analyzer analyzer;
  private final IndexSearcher indexSearcher;
  private final QueryParser queryParser;

  public LuceneFacade(@NonNull String indexPath) {
    this.analyzer = new PoetryAnalyzerFactory().get();
    this.queryParser = new QueryParser(analyzer);
    try {
      FSDirectory fsDirectory = FSDirectory.open(Paths.get(indexPath));
      DirectoryReader reader = DirectoryReader.open(fsDirectory);
      this.indexSearcher = new IndexSearcher(reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<GeneralChinesePoetry> search(
      String stringQuery, String preTag, String postTag, int resultSize) throws Exception {

    Query query = queryParser.apply(stringQuery);
    Formatter formatter;
    if (preTag == null || postTag == null) {
      formatter = new SimpleHTMLFormatter();
    } else {
      formatter = new SimpleHTMLFormatter(preTag, postTag);
    }

    QueryScorer queryScorer = new QueryScorer(query);
    Highlighter highlighter = new Highlighter(formatter, queryScorer);
    highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, MAX_HIGHLIGHT));
    highlighter.setMaxDocCharsToAnalyze(MAX_HIGHLIGHT);
    TextTransformer textTransformer = new TextTransformer(analyzer, highlighter);

    TopDocs topDocs = indexSearcher.search(query, resultSize);

    List<GeneralChinesePoetry> result = new ArrayList<>(resultSize);

    for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
      Document document = indexSearcher.doc(scoreDoc.doc);
      GeneralChinesePoetry poetry = GeneralChinesePoetry.fromLuceneDocument(document);
      // 高亮
      poetry.setTitle(textTransformer.apply(PoetryFieldEnum.TITLE, poetry.getTitle()));
      poetry.setSubtitle(textTransformer.apply(PoetryFieldEnum.SUBTITLE, poetry.getSubtitle()));
      poetry.setContent(textTransformer.apply(PoetryFieldEnum.CONTENT, poetry.getContent()));

      result.add(poetry);
    }
    return result;
  }

  @RequiredArgsConstructor
  private static final class TextTransformer
      implements BiFunction<PoetryFieldEnum, String, String> {

    private final Analyzer analyzer;
    private final Highlighter highlighter;

    @Override
    public String apply(PoetryFieldEnum fieldEnum, String text) {
      if (text != null) {
        try {
          String hl = highlighter.getBestFragment(analyzer, fieldEnum.fieldName, text);
          return hl != null ? hl : text;
        } catch (IOException | InvalidTokenOffsetsException e) {
          log.error("highlight failed:", e);
        }
      }
      return text;
    }
  }
}
