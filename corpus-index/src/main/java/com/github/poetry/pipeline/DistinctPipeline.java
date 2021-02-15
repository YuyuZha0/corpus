package com.github.poetry.pipeline;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.text.TextUtil;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 */
@Slf4j
public final class DistinctPipeline extends ForwardingPipeline {

  private static final double SIMILAR_THRESHOLD = 0.9;
  private static final int MAX_CMP_LEN = 64;
  private final JaroWinklerSimilarity jaroWinklerSimilarity = new JaroWinklerSimilarity();

  public DistinctPipeline(Pipeline next) {
    super(DistinctPipeline.class.getSimpleName(), next);
  }

  private static String toRawRepresentation(String content) {
    if (StringUtils.isEmpty(content)) {
      return content;
    }
    StringBuilder builder = new StringBuilder(MAX_CMP_LEN);
    for (int i = 0, len = content.length(); i < len; ++i) {
      if (TextUtil.isChineseCharacter(content, i)) {
        builder.append(content.charAt(i));
      }
      if (builder.length() > MAX_CMP_LEN) {
        break;
      }
    }
    return builder.toString();
  }

  private static String formatDiv(double a, double b) {
    return new DecimalFormat("#.##%").format(a / b);
  }

  @SuppressWarnings("UnstableApiUsage")
  private List<GeneralChinesePoetry> distinctForEachAuthor(
      String author, List<GeneralChinesePoetry> authorPoetryList) {
    final int size = authorPoetryList.size();
    if (size < 2) {
      return authorPoetryList;
    }
    List<GeneralChinesePoetry> dump = new ArrayList<>(size);
    GeneralChinesePoetry[] a = authorPoetryList.toArray(new GeneralChinesePoetry[0]);
    MutableGraph<Integer> poetryIdGraph =
        GraphBuilder.undirected().allowsSelfLoops(false).expectedNodeCount(size).build();
    String[] contents = new String[size];

    for (int i = 0; i < size; ++i) {
      poetryIdGraph.addNode(i);
      contents[i] = toRawRepresentation(a[i].getContent());
    }
    for (int i = 0; i < contents.length - 1; ++i) {
      for (int j = i + 1; j < contents.length; ++j) {
        if (isSimilar(contents[i], contents[j])) {
          poetryIdGraph.putEdge(i, j);
        }
      }
    }

    int distinctCount = 0;

    for (int i = 0; i < size; ++i) {
      if (poetryIdGraph.nodes().contains(i)) {
        Set<Integer> similarSet = Graphs.reachableNodes(poetryIdGraph, i);
        GeneralChinesePoetry poetry = a[i];
        for (Integer id : similarSet) {
          poetry = prefer(poetry, a[id]);
          poetryIdGraph.removeNode(id);
        }
        poetryIdGraph.removeNode(i);
        dump.add(poetry);
        distinctCount++;
      }
    }

    log.info(
        "distinct[{}/{}:{}] finished for author [{}]",
        distinctCount,
        size,
        formatDiv(distinctCount, size),
        author);

    return dump;
  }

  private GeneralChinesePoetry prefer(GeneralChinesePoetry a, GeneralChinesePoetry b) {
    if (a.getTitle() == null) {
      return b;
    }
    if (b.getTitle() == null) {
      return a;
    }
    return a.getTitle().length() > b.getTitle().length() ? a : b;
  }

  private boolean isSimilar(String s1, String s2) {
    return jaroWinklerSimilarity.apply(s1, s2) >= SIMILAR_THRESHOLD;
  }

  @Override
  public void process(IndexContext ctx, Iterable<GeneralChinesePoetry> poetries) {

    Map<String, List<GeneralChinesePoetry>> authorPoetryMap = new HashMap<>(512);
    for (GeneralChinesePoetry poetry : poetries) {
      authorPoetryMap
          .compute(
              StringUtils.trimToEmpty(poetry.getAuthor()),
              (k, v) -> v == null ? new ArrayList<>() : v)
          .add(poetry);
    }
    int poetryCount = authorPoetryMap.values().stream().mapToInt(List::size).sum();
    log.info("[{}] authors found, [{}] records found.", authorPoetryMap.size(), poetryCount);
    BlockingQueue<GeneralChinesePoetry> distinctResultQueue = new ArrayBlockingQueue<>(poetryCount);
    List<CompletableFuture<?>> futureList = new ArrayList<>();
    ExecutorService executor = Executors.newWorkStealingPool(5);
    authorPoetryMap.forEach(
        (k, v) -> {
          CompletableFuture<?> future =
              CompletableFuture.supplyAsync(() -> distinctForEachAuthor(k, v), executor)
                  .whenComplete(
                      ((generalChinesePoetries, throwable) ->
                          distinctResultQueue.addAll(generalChinesePoetries)));
          futureList.add(future);
        });
    CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
    //noinspection UnstableApiUsage
    MoreExecutors.shutdownAndAwaitTermination(executor, Duration.ofSeconds(2));

    int distinctResultSize = distinctResultQueue.size();
    log.info(
        "distinct finished, [{}/{}:{}] records after distinct.",
        distinctResultSize,
        poetryCount,
        formatDiv(distinctResultSize, poetryCount));

    ctx.setApproxCount(distinctResultSize);
    List<GeneralChinesePoetry> poetries1 = new ArrayList<>(distinctResultSize);
    distinctResultQueue.drainTo(poetries1);
    forward(ctx, poetries1);
  }
}
