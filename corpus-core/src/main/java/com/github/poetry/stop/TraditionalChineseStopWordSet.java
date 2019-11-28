package com.github.poetry.stop;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
@RequiredArgsConstructor
public final class TraditionalChineseStopWordSet implements StopWordSet {

  private static final double DEFAULT_COVERAGE_THRESHOLD = 70;
  private static final String RESOURCE_NAME = "古籍字频统计.txt";

  private final double coverageThreshold;

  public TraditionalChineseStopWordSet() {
    this(DEFAULT_COVERAGE_THRESHOLD);
  }

  @Override
  public Set<String> get() {

    try (InputStream in = getClass().getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
      try (BufferedReader reader =
          new BufferedReader(
              new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8))) {
        Set<String> result = new HashSet<>();
        String line = reader.readLine();
        while (line != null) {
          WordFreqStat stat = WordFreqStat.parseLine(line);
          if (stat.coverage > coverageThreshold) {
            result.add(stat.hanzi);
          }
          line = reader.readLine();
        }
        return result;
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @RequiredArgsConstructor
  @ToString
  private static final class WordFreqStat {
    private final int num;
    private final int codePoint;
    private final String hanzi;
    private final int freq;
    private final double coverage;

    static WordFreqStat parseLine(String line) {
      String[] a = StringUtils.splitPreserveAllTokens(line, '\t');
      return new WordFreqStat(
          Integer.parseUnsignedInt(a[0]),
          Integer.parseUnsignedInt(a[1].substring(2), 16),
          a[3],
          Integer.parseInt(a[5]),
          Double.parseDouble(a[6]));
    }
  }
}
