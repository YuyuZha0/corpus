package com.github.yuyu.text;

import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
final class StopWordUtils {

  private static final double COVERAGE_THRESHOLD = 70;

  private static final Set<String> TRADITIONAL_CHINESE_STOP_WORDS_SET =
      createTraditionalChineseStopWordSet();

  private static final Set<String> FULL_WIDTH_PUNCTUATION_SET = createFullWidthPunctuationSet();

  private StopWordUtils() {
    throw new IllegalStateException();
  }

  private static WordFreqStat parseLine(String line) {
    String[] a = StringUtils.splitPreserveAllTokens(line, '\t');
    return new WordFreqStat(
        Integer.parseUnsignedInt(a[0]),
        Integer.parseUnsignedInt(a[1].substring(2), 16),
        a[3],
        Integer.parseInt(a[5]),
        Double.parseDouble(a[6]));
  }

  private static Set<String> createTraditionalChineseStopWordSet() {

    ClassPathText classPathText = new ClassPathText("古籍字频统计.txt");
    try {
      List<WordFreqStat> list =
          classPathText.getLines(StopWordUtils::parseLine, x -> x.coverage > COVERAGE_THRESHOLD);
      List<String> strings = new ArrayList<>(list.size());
      for (WordFreqStat stat : list) {
        strings.add(stat.hanzi);
      }
      return ImmutableSet.copyOf(strings);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Set<String> createFullWidthPunctuationSet() {
    return ImmutableSet.copyOf(
        Arrays.asList(
            "\uff0c", "\uff01", "\uff1f", "\uff1b", "\uff1a", "\uff08", "\uff09", "\uff3b",
            "\uff3d", "\u3010", "\u3011", "\u3002", "\ufe12", "\u300e", "\u300f", "\ufe41",
            "\ufe42", "\u300c", "\u300d", "\u3001", "\u3000"));
  }

  static Set<String> getTraditionalChineseStopWordsSet() {
    return TRADITIONAL_CHINESE_STOP_WORDS_SET;
  }

  static Set<String> getFullWidthPunctuationSet() {
    return FULL_WIDTH_PUNCTUATION_SET;
  }

  @RequiredArgsConstructor
  @ToString
  private static final class WordFreqStat {
    private final int num;
    private final int codePoint;
    private final String hanzi;
    private final int freq;
    private final double coverage;
  }
}
