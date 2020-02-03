package com.github.poetry.text;

import com.google.common.base.CharMatcher;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import java.lang.Character.UnicodeScript;

/**
 * @author zhaoyuyu
 * @since 2020/2/3
 */
public final class TextUtils {

  private TextUtils() {
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
      if (TextUtils.isChineseCharacter(content, i)) {
        hasher.putChar(content.charAt(i));
      }
    }
    return hasher.hash().asLong();
  }
}
