package com.github.poetry.rank;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.poetry.json.ObjectMapperFactory;
import com.google.common.math.IntMath;
import com.google.common.math.Stats;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class RankingScoreManager implements ToDoubleBiFunction<String, String> {

  private final TObjectDoubleMap<RankingKey> keyMap;

  private final TObjectDoubleMap<String> authorMap;

  public static RankingScoreManager create(@NonNull Path root) {
    final RankingStat[] stats;
    try {
      stats = loadFrom(root, new ObjectMapperFactory().get()).toArray(new RankingStat[0]);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    setLevel(stats);

    TObjectDoubleMap<RankingKey> keyMap = new TObjectDoubleHashMap<>(stats.length);
    Map<String, TDoubleList> authorScoreMap = new HashMap<>();
    for (RankingStat stat : stats) {
      double score = stat.calcScore();
      if (Double.isNaN(score) || Double.isInfinite(score) || score <= 0D) {
        log.warn("invalid score: {} -> {}", stat, score);
        continue;
      }
      keyMap.put(RankingKey.of(stat.getTitle(), stat.getAuthor()), score);
      authorScoreMap
          .compute(stat.getAuthor(), (k, v) -> v == null ? new TDoubleArrayList() : v)
          .add(score);
    }
    TObjectDoubleMap<String> authorMap = new TObjectDoubleHashMap<>(authorScoreMap.size());
    for (Entry<String, TDoubleList> entry : authorScoreMap.entrySet()) {
      double[] scores = entry.getValue().toArray();
      //noinspection UnstableApiUsage
      authorMap.put(entry.getKey(), Stats.meanOf(scores));
    }

    return new RankingScoreManager(keyMap, authorMap);
  }

  private static List<RankingStat> loadFrom(Path rootPath, ObjectMapper objectMapper)
      throws IOException {
    List<Path> files = new ArrayList<>();
    addJsonFile(Paths.get(rootPath.toString(), "rank", "ci"), files);
    addJsonFile(Paths.get(rootPath.toString(), "rank", "poet"), files);

    JavaType type =
        objectMapper.getTypeFactory().constructCollectionType(List.class, RankingStat.class);
    List<List<RankingStat>> listList = new ArrayList<>(files.size());

    for (Path file : files) {
      try (InputStream in = Files.newInputStream(file, StandardOpenOption.READ)) {
        List<RankingStat> list = objectMapper.readValue(in, type);
        listList.add(list);
      }
    }
    int totalLen = listList.stream().mapToInt(List::size).sum();
    log.info("[{}] records loaded.", totalLen);
    List<RankingStat> result = new ArrayList<>(totalLen);
    for (List<RankingStat> list : listList) {
      result.addAll(list);
    }
    return result;
  }

  private static void addJsonFile(Path root, List<Path> files) throws IOException {
    try (Stream<Path> pathStream = Files.walk(root)) {
      pathStream
          .filter(p -> p.toString().endsWith("json"))
          .filter(Files::isReadable)
          .filter(Files::isRegularFile)
          .forEach(files::add);
    }
  }

  private static void setLevel(RankingStat[] stats) {
    setLevel0(stats, RankingStat::getBaidu, RankingStat::setBaiduLevel);
    setLevel0(stats, RankingStat::getBing, RankingStat::setBingLevel);
    setLevel0(stats, RankingStat::getBingEn, RankingStat::setBingEnLevel);
    setLevel0(stats, RankingStat::getSo360, RankingStat::setSo360Level);
    setLevel0(stats, RankingStat::getGoogle, RankingStat::setGoogleLevel);
  }

  private static void setLevel0(
      RankingStat[] stats,
      ToLongFunction<? super RankingStat> key,
      BiConsumer<? super RankingStat, Integer> setter) {
    Arrays.sort(stats, Comparator.comparingLong(key));
    for (int i = 0; i < stats.length; ++i) {
      RankingStat stat = stats[i];
      int level = IntMath.divide((i + 1) * 5, stats.length, RoundingMode.HALF_UP) + 1;
      setter.accept(stat, level);
    }
  }

  public double getRankingScore(String title, String author) {
    if ("句".equals(title)) {
      return 0.9D;
    }
    double score = keyMap.get(RankingKey.of(title, author));
    if (score > 0) {
      return score;
    }
    score = authorMap.get(author);
    if (score > 0) {
      return score;
    }
    return 1D;
  }

  @Override
  public double applyAsDouble(String s, String s2) {
    return getRankingScore(s, s2);
  }
}
