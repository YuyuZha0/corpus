package com.github.poetry.transform;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public final class TransformUtil {

  private static final String LINE_BREAKER = "<br>";

  private TransformUtil() {
    throw new UnsupportedOperationException();
  }

  public static String[] splitWithBlank(String s) {
    if (s == null || s.isEmpty()) return ArrayUtils.EMPTY_STRING_ARRAY;
    List<String> list = new ArrayList<>(2);
    Iterable<String> iterable =
        Splitter.on(CharMatcher.whitespace()).omitEmptyStrings().limit(2).split(s);
    for (String ss : iterable) {
      list.add(ss);
    }
    return list.toArray(new String[0]);
  }

  public static String[] splitWithMidDot(String s) {
    return StringUtils.split(s, "・·");
  }

  static String joinParagraphs(List<String> paragraphs) {
    StringBuilder builder = new StringBuilder(paragraphs.size() * 16 + 7);
    return Joiner.on(LINE_BREAKER).appendTo(builder, paragraphs).toString();
  }

  static String resolveDynastyFromFileName(Path path) {
    String[] a = StringUtils.splitPreserveAllTokens(path.getFileName().toString(), '.');
    if ("song".equals(a[1])) {
      return "宋";
    } else if ("tang".equals(a[1])) {
      return "唐";
    } else {
      return null;
    }
  }
}
