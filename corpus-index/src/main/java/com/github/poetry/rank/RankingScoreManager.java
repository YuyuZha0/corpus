package com.github.poetry.rank;

import com.github.poetry.source.PoetrySource;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class RankingScoreManager {

  private final double averageScore;

  private final Map<RankingKey, Double> keyMap;

  private final Map<String, Double> authorMap;

  public static RankingScoreManager create(String root) {
    final List<RankingStat> statList;
    try {
      statList = loadFrom(root);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    double totalScore = 0D;
    Map<RankingKey, Double> keyMap = new HashMap<>(statList.size());
    Map<String, List<Double>> authorScoreMap = new HashMap<>();
    for (RankingStat stat : statList) {
      double score = stat.calcScore();
      if (Double.isNaN(score) || Double.isInfinite(score) || score <= 0D) {
        log.warn("invalid score: {}", stat);
        continue;
      }
      totalScore += score;
      keyMap.put(new RankingKey(stat.getAuthor(), stat.getTitle()), score);
      authorScoreMap.compute(
          stat.getAuthor(),
          (k, v) -> {
            if (v == null) {
              v = new ArrayList<>();
            }
            v.add(score);
            return v;
          });
    }
    Map<String, Double> authorMap = new HashMap<>(authorScoreMap.size());
    for (Entry<String, List<Double>> entry : authorScoreMap.entrySet()) {
      double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0D);
      authorMap.put(entry.getKey(), avg);
    }

    return new RankingScoreManager(totalScore / keyMap.size(), keyMap, authorMap);
  }

  private static List<RankingStat> loadFrom(String inputRoot) throws IOException {
    List<File> files = new ArrayList<>();
    addJsonFile(inputRoot + "/rank/ci", files);
    addJsonFile(inputRoot + "/rank/poet", files);

    TypeToken<?> typeToken = TypeToken.getParameterized(List.class, RankingStat.class);

    List<List<RankingStat>> listList = new ArrayList<>(files.size());

    int totalLen = 0;
    for (File file : files) {
      try (InputStream in = new FileInputStream(file)) {
        List<RankingStat> list =
            PoetrySource.GSON.fromJson(
                new InputStreamReader(in, StandardCharsets.UTF_8), typeToken.getType());
        totalLen += list.size();
        listList.add(list);
      }
    }
    log.warn("[{}] records loaded.", totalLen);
    List<RankingStat> result = new ArrayList<>(totalLen);
    for (List<RankingStat> list : listList) {
      result.addAll(list);
    }
    return result;
  }

  private static void addJsonFile(String root, List<File> files) {
    File[] files1 = new File(root).listFiles((f, n) -> n.endsWith("json"));
    if (files1 != null && files1.length > 0) {
      Collections.addAll(files, files1);
    } else {
      log.warn("no files found at [{}]", root);
    }
  }

  public double getRankingScore(String title, String author) {
    Double score = keyMap.get(new RankingKey(title, author));
    if (score != null) {
      return score / averageScore;
    }
    score = authorMap.get(author);
    if (score != null) {
      return score / averageScore;
    }
    return 1D;
  }
}
