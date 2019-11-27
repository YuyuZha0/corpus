package com.github.yuyu;

import com.github.yuyu.entity.ClassicPoetry;
import com.github.yuyu.source.ChuCiSource;
import com.github.yuyu.source.PoetrySource;
import com.github.yuyu.source.QuanTangShiSource;
import com.github.yuyu.source.ShiJingSource;
import com.github.yuyu.source.SongCi300Source;
import com.github.yuyu.source.SongTangWudaiCiSource;
import com.github.yuyu.source.TangShi300Source;
import com.github.yuyu.source.YueFuSource;
import com.github.yuyu.text.AnalyzerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
@Slf4j
public final class IndexApp {

  public static void main(String[] args) throws IOException {
    if (args == null || args.length < 1)
      throw new IllegalArgumentException("index path is required!");
    String indexPath = args[0];

    log.info("creating lucene index in path[{}]...", indexPath);
    List<PoetrySource> poetrySources =
        Arrays.asList(
            new ChuCiSource(),
            new ShiJingSource(),
            new SongCi300Source(),
            new SongTangWudaiCiSource(),
            new TangShi300Source(),
            new YueFuSource(),
            new QuanTangShiSource());

    Set<String> contentSet = new HashSet<>(100000);
    try (FSDirectory fsDirectory = FSDirectory.open(Paths.get(indexPath))) {

      int count = 0;
      IndexWriterConfig config = new IndexWriterConfig(new AnalyzerFactory().get());
      config.setOpenMode(OpenMode.CREATE_OR_APPEND);
      try (IndexWriter indexWriter = new IndexWriter(fsDirectory, config)) {
        for (PoetrySource poetrySource : poetrySources) {
          for (ClassicPoetry poetry : poetrySource.get()) {
            if (contentSet.add(poetry.getContentIdentifier())) {
              indexWriter.addDocument(poetry.toLuceneDocument());
              ++count;
            } else {
              log.info(
                  "[{}:{}] seems to be a duplication!", poetry.getTitle(), poetry.getAuthor());
            }
          }
          indexWriter.flush();
          indexWriter.commit();
        }

        log.info("[{}] documents indexed!", count);
      }
    }
  }
}
