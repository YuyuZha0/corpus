package com.github.poetry;

import com.github.poetry.text.ChineseAnalyzerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author fishzhao
 * @since 2020-08-20
 */
@Slf4j
final class LuceneModule extends AbstractModule {

  @Provides
  @Singleton
  @Named("indexAnalyzer")
  Analyzer provideIndexAnalyzer() {
    return new ChineseAnalyzerFactory().get();
  }

  @Provides
  @Singleton
  @Named("standardAnalyzer")
  Analyzer provideHighlightAnalyzer() {
    return new StandardAnalyzer();
  }

  @Provides
  @Singleton
  IndexSearcher provideIndexSearcher(@Named("cli.index") String indexPath, Vertx vertx)
      throws IOException {
    FSDirectory fsDirectory = FSDirectory.open(Paths.get(indexPath));
    DirectoryReader reader = DirectoryReader.open(fsDirectory);
    ((VertxInternal) vertx)
        .addCloseHook(
            p -> {
              try {
                reader.close();
                fsDirectory.close();
                p.tryComplete();
              } catch (Exception e) {
                p.tryFail(e);
              }
            });
    return new IndexSearcher(reader);
  }
}
