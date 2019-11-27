package com.github.yuyu.source;

import com.github.yuyu.entity.ClassicPoetry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
public final class ChuCiSource extends FixedPatternSource {

  private static final String FILE_NAME = "楚辞.txt";

  private static final Pattern SENTENCE_PATTERN = Pattern.compile("^[\u4e00-\u9fa5，：]+[。？！；：]$");
  private static final Pattern TITLE_PATTERN =
      Pattern.compile("^[\u4e00-\u9fa5]+(\u3000[\u4e00-\u9fa5]+)?$");

  public ChuCiSource() {
    super(FILE_NAME);
  }

  @Override
  boolean startPattern(String currentLine, String nextLine) {
    return TITLE_PATTERN.matcher(currentLine).matches()
        && SENTENCE_PATTERN.matcher(nextLine).matches();
  }

  @Override
  boolean endPattern(String currentLine, String nextLine) {
    return SENTENCE_PATTERN.matcher(currentLine).matches()
        && (nextLine == null || TITLE_PATTERN.matcher(nextLine).matches());
  }

  @Override
  ClassicPoetry resolveLines(String[] lines) {

    return ClassicPoetry.builder()
        .title(lines[0])
        .author("屈原、宋玉等")
        .content(StringUtils.join(ArrayUtils.subarray(lines, 1, lines.length), LINE_BREAKER))
        .type("楚辞")
        .build();
  }
}
