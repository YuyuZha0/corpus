package com.github.poetry.text;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.Character.UnicodeScript;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
public final class TextUtil {

  private static final String LINE_BREAKER = "<br>";

  private TextUtil() {
    throw new IllegalStateException();
  }

  public static boolean isBlank(String s) {
    return s == null || s.isEmpty() || CharMatcher.whitespace().matchesAllOf(s);
  }

  public static boolean isChineseCharacter(String s, int index) {
    int codePoint = s.codePointAt(index);
    return Character.UnicodeScript.of(codePoint) == UnicodeScript.HAN;
  }

  @SuppressWarnings("UnstableApiUsage")
  public static long contentHash(String content) {
    if (content == null || content.isEmpty()) return 0L;
    int len = Math.min(content.length(), 128);
    Hasher hasher = Hashing.murmur3_128().newHasher(len);
    for (int i = 0; i < len; ++i) {
      if (isChineseCharacter(content, i)) {
        hasher.putChar(content.charAt(i));
      }
    }
    return hasher.hash().asLong();
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

  public static String joinParagraphs(List<String> paragraphs) {
    StringBuilder builder = new StringBuilder(paragraphs.size() * 16 + 7);
    return Joiner.on(LINE_BREAKER).appendTo(builder, paragraphs).toString();
  }

  public static String resolveDynastyFromFileName(Path path) {
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
