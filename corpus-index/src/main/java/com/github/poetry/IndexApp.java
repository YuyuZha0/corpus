package com.github.poetry;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.Ci;
import com.github.poetry.json.Qu;
import com.github.poetry.json.Shi;
import com.github.poetry.json.ShiJingItem;
import com.github.poetry.json.WuDaiItem;
import com.github.poetry.source.JsonFileSource;
import com.github.poetry.source.JsonLineFileSource;
import com.github.poetry.source.PoetrySource;
import com.github.poetry.text.PoetryAnalyzerFactory;
import com.github.poetry.transform.CiTransformer;
import com.github.poetry.transform.PoetryTransformer;
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
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
@Slf4j
public final class IndexApp {

  private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[a-z0-9.\\-]+\\.json");

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
    try (FSDirectory fsDirectory = FSDirectory.open(Paths.get(indexPath))) {

      IndexWriterConfig config = new IndexWriterConfig(new PoetryAnalyzerFactory().get());
      config.setOpenMode(OpenMode.CREATE_OR_APPEND);

      try (IndexWriter indexWriter = new IndexWriter(fsDirectory, config)) {
        addFromFiles(new File(inputRoot + "/json"), indexWriter, Shi.class, ShiTransformer::new);
        addFromFiles(new File(inputRoot + "/ci"), indexWriter, Ci.class, f -> new CiTransformer());
        addFromFile(
            indexWriter,
            new JsonLineFileSource<>(
                new File(inputRoot + "/yuanqu/yuanqu.json"), Qu.class, new QuTransformer()));
        addFromFile(
            indexWriter,
            new JsonFileSource<>(
                new File(inputRoot + "/shijing/shijing.json"),
                ShiJingItem.class,
                new ShiJingItemTransformer()));
        addFromFiles(
            new File(inputRoot + "/wudai/huajianji"),
            indexWriter,
            WuDaiItem.class,
            f -> new WuDaiItemTransformer());
        addFromFile(
            indexWriter,
            new JsonFileSource<>(
                new File(inputRoot + "/wudai/nantang/poetrys.json"),
                WuDaiItem.class,
                new WuDaiItemTransformer()));
      }
      log.info("indexing finished!");
    }
  }

  private static <T> void addFromFiles(
      File root,
      IndexWriter writer,
      Class<? extends T> clazz,
      Function<File, ? extends PoetryTransformer<? super T>> factory)
      throws IOException {
    if (!root.exists() || !root.isDirectory()) {
      throw new IllegalArgumentException("illegal root:" + root.getAbsolutePath());
    }
    File[] files =
        root.listFiles((f, n) -> !n.contains("author") && FILE_NAME_PATTERN.matcher(n).matches());
    Objects.requireNonNull(files);
    log.info("[{}] file(s) found.", files.length);
    for (File file : files) {
      JsonFileSource<?> jsonFileSource = new JsonFileSource<>(file, clazz, factory.apply(file));
      addFromFile(writer, jsonFileSource);
    }
  }

  private static void addFromFile(IndexWriter writer, PoetrySource source) throws IOException {

    List<GeneralChinesePoetry> poetryList = source.get();
    for (GeneralChinesePoetry generalChinesePoetry : poetryList) {
      writer.addDocument(generalChinesePoetry.toLuceneDocument());
    }
    writer.flush();
    writer.commit();
  }
}
