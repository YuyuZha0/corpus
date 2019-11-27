package com.github.yuyu.source;

import com.github.yuyu.entity.ClassicPoetry;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
public final class SongCi300Source extends LookaheadFixedLineSource {

  private static final String FILE_NAME = "宋词三百首.txt";

  public SongCi300Source() {
    super(FILE_NAME, 3);
  }

  @Override
  boolean lookahead(String line) {
    return StringUtils.startsWith(line, "词牌");
  }

  @Override
  ClassicPoetry resolveLines(String[] lines) {
    checkPrefix(lines[1], "作者");
    checkPrefix(lines[2], "词文");
    return ClassicPoetry.builder()
        .title(lines[0].substring(3))
        .author(lines[1].substring(3))
        .content(StringUtils.replace(lines[2].substring(3), "○", LINE_BREAKER))
        .dynasty("宋")
        .type("宋词")
        .build();
  }
}
