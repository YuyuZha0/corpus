package com.github.poetry;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.*;
import com.github.poetry.pipeline.*;
import com.github.poetry.source.JsonFileSource;
import com.github.poetry.source.JsonLineFileSource;
import com.github.poetry.source.MultiJsonFileSource;
import com.github.poetry.source.PoetrySource;
import com.github.poetry.transform.*;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
@Slf4j
public final class IndexApp {

  public static void main(String[] args) {
    Options options = new Options();
    options.addOption(new Option("o", "output", true, "the output index path"));
    options.addOption(new Option("h", "help", false, "print help"));
    options.addOption(new Option("i", "index", true, "the input root path"));

    CommandLineParser parser = new DefaultParser();
    CommandLine commandLine;
    try {
      commandLine = parser.parse(options, args);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    if (commandLine.hasOption('h')) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("jar -jar <fileName>", options);
      return;
    }

    String indexPath = commandLine.getOptionValue('o');
    String inputRoot = commandLine.getOptionValue('i');
    log.info("creating lucene index in path: [{}], input root is: [{}]", indexPath, inputRoot);

    Pipeline pipeline =
        new PipelineBuilder<DistinctPipeline>()
            .add(DistinctPipeline::new)
            .add(next -> new RankingScorePipeline(inputRoot, next))
            .end(new WritingIndexPipeline(indexPath));

    List<? extends PoetrySource> poetrySources =
        Arrays.asList(
            createShiSource(inputRoot),
            createCiSource(inputRoot),
            createQuSource(inputRoot),
            createShiJingSource(inputRoot),
            createWudaiSource(inputRoot));

    List<List<GeneralChinesePoetry>> buffer = new ArrayList<>();
    for (PoetrySource source : poetrySources) {
      buffer.add(source.get());
    }

    pipeline.process(new IndexContext(), Iterables.concat(buffer));
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
