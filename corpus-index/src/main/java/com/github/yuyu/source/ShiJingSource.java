package com.github.yuyu.source;

import com.github.yuyu.entity.ClassicPoetry;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
public final class ShiJingSource extends FixedPatternSource {

  private static final String FILE_NAME = "诗经.txt";

  private static final Pattern TITLE_PATTERN =
      Pattern.compile("《[\u4e00-\u9fa5]+・?[\u4e00-\u9fa5]+》");

  public ShiJingSource() {
    super(FILE_NAME);
  }

  @Override
  boolean startPattern(String currentLine, String nextLine) {
    return TITLE_PATTERN.matcher(currentLine).find() && StringUtils.isNotBlank(nextLine);
  }

  @Override
  boolean endPattern(String currentLine, String nextLine) {
    return currentLine != null && StringUtils.isBlank(nextLine);
  }

  @Override
  ClassicPoetry resolveLines(String[] lines) {

    String titleLine = lines[0].trim();
    int split = titleLine.indexOf('・');
    String title = null, subtitle = null;
    if (split < 0) {
      title = titleLine.substring(1, titleLine.length() - 1);
    } else {
      title = titleLine.substring(1, split);
      subtitle = titleLine.substring(split + 1, titleLine.length() - 1);
    }
    StringBuilder contentBuilder = new StringBuilder(100);
    for (int i = 1; i < lines.length; ++i) {
      if (i > 1) {
        contentBuilder.append(LINE_BREAKER);
      }
      contentBuilder.append(lines[i]);
    }
    return ClassicPoetry.builder()
        .dynasty("先秦")
        .content(contentBuilder.toString())
        .title(title)
        .subtitle(subtitle)
        .type("诗经")
        .build();
  }
}
