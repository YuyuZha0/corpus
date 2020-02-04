package com.github.poetry.pipeline;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.text.TextUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.JaccardSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  private void distinctForEachAuthor(
      String author, List<GeneralChinesePoetry> authorPoetryList, List<GeneralChinesePoetry> dump) {
    int size = authorPoetryList.size();
    log.info("[{}] records found for author [{}].", size, author);
    if (size < 2) return;
    String[] contents = new String[size];
    for (int i = 0; i < size; ++i) {
      contents[i] = reserveHanChar(authorPoetryList.get(i).getContent());
    }
    for (int i = 0; i < size - 1; ++i) {
      if (contents[i] == null) continue;
      for (int j = i + 1; j < size; ++j) {
        if (contents[j] == null || contents[i] == null) continue;
        // 如果两者相似，只保留内容较长的
        if (isSimilar(contents[i], contents[j])) {
          if (contents[i].length() > contents[j].length()) {
            contents[j] = null;
          } else {
            contents[i] = null;
          }
        }
      }
    }
    int distinctCount = 0;
    for (int i = 0; i < size; ++i) {
      if (contents[i] != null) {
        ++distinctCount;
        dump.add(authorPoetryList.get(i));
      }
    }
    log.info("[{}] distinct records found for author [{}]", distinctCount, author);
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
