package com.github.poetry.pipeline;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.text.ChineseAnalyzerFactory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Path;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 */
@Slf4j
public final class WritingIndexPipeline extends EndPipeline {

  private static final int FLUSH_COUNT = 10000;
  private final Path indexPath;

  public WritingIndexPipeline(@NonNull Path indexPath) {
    super(WritingIndexPipeline.class.getSimpleName());
    this.indexPath = indexPath;
  }

  @Override
  public void process(IndexContext ctx, Iterable<GeneralChinesePoetry> poetries) {

    try (FSDirectory fsDirectory = FSDirectory.open(indexPath)) {
      IndexWriterConfig config = new IndexWriterConfig(new ChineseAnalyzerFactory().get());
      config.setOpenMode(OpenMode.CREATE_OR_APPEND);
      int indexCount = 0;
      try (IndexWriter indexWriter = new IndexWriter(fsDirectory, config)) {
        for (GeneralChinesePoetry poetry : poetries) {
          indexWriter.addDocument(poetry.toLuceneDocument());
          if (++indexCount % FLUSH_COUNT == 0) {
            indexWriter.flush();
            indexWriter.commit();
            log.info("current indexed count: {}", indexCount);
          }
        }
        indexWriter.flush();
        indexWriter.commit();
      }
      log.info("indexing finished, [{}] indexed.", indexCount);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
