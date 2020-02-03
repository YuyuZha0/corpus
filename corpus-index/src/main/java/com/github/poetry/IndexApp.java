package com.github.poetry;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.Ci;
import com.github.poetry.json.Qu;
import com.github.poetry.json.Shi;
import com.github.poetry.json.ShiJingItem;
import com.github.poetry.json.WuDaiItem;
import com.github.poetry.rank.RankingScoreManager;
import com.github.poetry.source.JsonFileSource;
import com.github.poetry.source.JsonLineFileSource;
import com.github.poetry.source.MultiJsonFileSource;
import com.github.poetry.source.PoetrySource;
import com.github.poetry.text.PoetryAnalyzerFactory;
import com.github.poetry.text.TextUtils;
import com.github.poetry.transform.CiTransformer;
import com.github.poetry.transform.QuTransformer;
import com.github.poetry.transform.ShiJingItemTransformer;
import com.github.poetry.transform.ShiTransformer;
import com.github.poetry.transform.WuDaiItemTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
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
    Options options = new Options();
    options.addOption(new Option("o", "output", true, "the output index path"));
    options.addOption(new Option("h", "help", false, "print help"));
    options.addOption(new Option("i", "index", true, "the input root path"));

    CommandLineParser parser = new DefaultParser();
    CommandLine commandLine = null;
    try {
      commandLine = parser.parse(options, args);
    } catch (ParseException ignore) {
    }
    if (commandLine == null || commandLine.hasOption('h')) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("jar -jar <fileName>", options);
      return;
    }

    String indexPath = commandLine.getOptionValue('o');
    String inputRoot = commandLine.getOptionValue('i');
    log.info("creating lucene index in path: [{}], input root is: [{}]", indexPath, inputRoot);

    RankingScoreManager rankingScoreManager = RankingScoreManager.create(inputRoot);

    List<? extends PoetrySource> poetrySources =
        Arrays.asList(
            createShiSource(inputRoot),
            createCiSource(inputRoot),
            createQuSource(inputRoot),
            createShiJingSource(inputRoot),
            createWudaiSource(inputRoot));
    Set<Long> contentHashSet = new HashSet<>(25000);
    try (FSDirectory fsDirectory = FSDirectory.open(Paths.get(indexPath))) {
      IndexWriterConfig config = new IndexWriterConfig(new PoetryAnalyzerFactory().get());
      config.setOpenMode(OpenMode.CREATE_OR_APPEND);
      int indexCount = 0, duplicationCount = 0;
      try (IndexWriter indexWriter = new IndexWriter(fsDirectory, config)) {
        for (PoetrySource poetrySource : poetrySources) {
          List<GeneralChinesePoetry> poetryList = poetrySource.get();
          for (GeneralChinesePoetry poetry : poetryList) {
            long contentHash = TextUtils.contentHash(poetry.getContent());
            if (contentHashSet.add(contentHash)) {
              double score =
                  rankingScoreManager.getRankingScore(poetry.getTitle(), poetry.getAuthor());
              poetry.setScore(Math.max(score, 0.01D));
              indexWriter.addDocument(poetry.toLuceneDocument());
              ++indexCount;
            } else {
              ++duplicationCount;
            }
          }
          indexWriter.flush();
          indexWriter.commit();
        }
      }
      log.info("indexing finished, [{}] indexed, [{}] duplicated.", indexCount, duplicationCount);
    }
  }

  private static PoetrySource createShiSource(String root) {
    return new MultiJsonFileSource<>(
        new File(root + "/json"),
        Shi.class,
        ShiTransformer::new,
        name -> name.startsWith("poet") && name.endsWith("json"));
  }

  private static PoetrySource createCiSource(String root) {
    return new MultiJsonFileSource<>(
        new File(root + "/ci"),
        Ci.class,
        f -> new CiTransformer(),
        name -> name.startsWith("ci") && name.endsWith("json"));
  }

  private static PoetrySource createWudaiSource(String root) {
    return new MultiJsonFileSource<>(
        new File(root + "/wudai"),
        WuDaiItem.class,
        f -> new WuDaiItemTransformer(),
        name -> "poetrys.json".equals(name) || name.endsWith("juan.json"));
  }

  private static PoetrySource createShiJingSource(String root) {
    return new JsonFileSource<>(
        new File(root + "/shijing/shijing.json"), ShiJingItem.class, new ShiJingItemTransformer());
  }

  private static PoetrySource createQuSource(String root) {
    return new JsonLineFileSource<>(
        new File(root + "/yuanqu/yuanqu.json"), Qu.class, new QuTransformer());
  }
}
