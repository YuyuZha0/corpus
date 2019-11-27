package com.github.yuyu.source;

import com.github.yuyu.entity.ClassicPoetry;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
public final class TangShi300Source extends LookaheadFixedLineSource {

  private static final String FILE_NAME = "唐诗三百首.txt";
  private static final Pattern HEADER_PATTERN = Pattern.compile("^诗文:(\\(押[\u4e00-\u9fa5]韵\\))?");

  public TangShi300Source() {
    super(FILE_NAME, 4);
  }

  private static String removeContentHeader(String s) {
    return HEADER_PATTERN.matcher(s).replaceFirst("");
  }

  @Override
  boolean lookahead(String line) {
    return StringUtils.startsWith(line, "诗名");
  }

  @Override
  ClassicPoetry resolveLines(String[] lines) {
    checkPrefix(lines[1], "作者");
    checkPrefix(lines[2], "诗体");
    checkPrefix(lines[3], "诗文");
    return ClassicPoetry.builder()
        .title(lines[0].substring(3))
        .author(lines[1].substring(3))
        .content(removeContentHeader(lines[3]))
        .dynasty("唐")
        .type(lines[2].substring(3))
        .build();
  }
}
