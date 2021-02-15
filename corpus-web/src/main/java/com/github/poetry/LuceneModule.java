package com.github.poetry;

import com.github.poetry.text.ChineseAnalyzerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

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
  IndexSearcher provideIndexSearcher(@Named("cli.index") String indexPath) throws IOException {
    FSDirectory fsDirectory = FSDirectory.open(Paths.get(indexPath));
    DirectoryReader reader = DirectoryReader.open(fsDirectory);
    Runtime.getRuntime()
        .addShutdownHook(
            Executors.defaultThreadFactory()
                .newThread(
                    () -> {
                      try {
                        reader.close();
                        fsDirectory.close();
                      } catch (Exception e) {
                        log.error("Invoke shutdown hook failed: ", e);
                      }
                    }));
    return new IndexSearcher(reader);
  }
}
