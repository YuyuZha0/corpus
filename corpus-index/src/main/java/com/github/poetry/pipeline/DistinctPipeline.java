package com.github.poetry.pipeline;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.text.TextUtils;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.JaccardSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 */
@Slf4j
public final class DistinctPipeline extends ForwardingPipeline {

  private static final double SIMILAR_THRESHOLD = 0.9;
  private final JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();

  public DistinctPipeline(Pipeline next) {
    super(DistinctPipeline.class.getSimpleName(), next);
  }

  private static String reserveHanChar(String content) {
    if (content == null || content.isEmpty()) return content;
    int len = content.length();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < len; ++i) {
      if (TextUtils.isChineseCharacter(content, i)) {
        builder.append(content.charAt(i));
      }
    }
    return builder.toString();
  }

  @SuppressWarnings("UnstableApiUsage")
  private void distinctForEachAuthor(
      String author, List<GeneralChinesePoetry> authorPoetryList, List<GeneralChinesePoetry> dump) {
    int size = authorPoetryList.size();
    if (size < 2) {
      dump.addAll(authorPoetryList);
      return;
    }
    GeneralChinesePoetry[] a = authorPoetryList.toArray(new GeneralChinesePoetry[0]);
    MutableGraph<Integer> poetryIdGraph =
        GraphBuilder.undirected().allowsSelfLoops(false).expectedNodeCount(size).build();

    for (int i = 0; i < size; ++i) {
      poetryIdGraph.addNode(i);
    }
    String prevContent = reserveHanChar(a[0].getContent());
    for (int i = 1; i < a.length; ++i) {
      String content = reserveHanChar(a[i].getContent());
      if (isSimilar(prevContent, content)) {
        poetryIdGraph.putEdge(i - 1, i);
      }
      prevContent = content;
    }

    int distinctCount = 0;

    for (int i = 0; i < size; ++i) {
      if (poetryIdGraph.nodes().contains(i)) {
        Set<Integer> similarSet = Graphs.reachableNodes(poetryIdGraph, i);
        GeneralChinesePoetry poetry = a[i];
        for (Integer id : similarSet) {
          if (a[id].getTitle().length() > poetry.getTitle().length()) {
            poetry = a[id];
          }
          poetryIdGraph.removeNode(id);
        }
        poetryIdGraph.removeNode(i);
        dump.add(poetry);
        distinctCount++;
      }
    }

    log.info("distinct[{}/{}] finished for author [{}]", distinctCount, size, author);
  }

  private boolean isSimilar(String s1, String s2) {
    return s1.equals(s2) || jaccardSimilarity.apply(s1, s2) >= SIMILAR_THRESHOLD;
  }

  @Override
  public void process(IndexContext ctx, Iterable<GeneralChinesePoetry> poetries) {

    Map<String, List<GeneralChinesePoetry>> authorPoetryMap = new HashMap<>();
    int count = 0;
    for (GeneralChinesePoetry poetry : poetries) {
      authorPoetryMap
          .compute(poetry.getAuthor(), (k, v) -> v == null ? new ArrayList<>() : v)
          .add(poetry);
      ++count;
    }
    log.info("[{}] authors found, [{}] records found.", authorPoetryMap.size(), count);
    List<GeneralChinesePoetry> dump = new ArrayList<>(count);

    authorPoetryMap.forEach((k, v) -> distinctForEachAuthor(k, v, dump));

    log.info("distinct finished, [{}] records after distinct.", dump.size());

    ctx.setApproxCount(dump.size());

    forward(ctx, dump);
  }
}
