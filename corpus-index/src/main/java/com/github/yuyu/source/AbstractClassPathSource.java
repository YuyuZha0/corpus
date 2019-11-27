package com.github.yuyu.source;

import com.github.yuyu.entity.ClassicPoetry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
@Slf4j
abstract class AbstractClassPathSource implements PoetrySource {

  private final String fileName;
  private final Charset charset;

  AbstractClassPathSource(String fileName) {
    this(fileName, StandardCharsets.UTF_8);
  }

  private AbstractClassPathSource(@NonNull String fileName, Charset charset) {
    this.fileName = fileName;
    this.charset = charset;
  }

  static void checkPrefix(String s, String prefix) {
    if (s == null || !s.startsWith(prefix)) {
      String msg = String.format("line \"%s\" is assert to starts with [%s]", s, prefix);
      throw new IllegalArgumentException(msg);
    }
  }

  @Override
  public final List<ClassicPoetry> get() {
    log.info("load input stream from: [{}]", fileName);
    try (InputStream in = getClass().getClassLoader().getResourceAsStream(fileName)) {
      try (BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(Objects.requireNonNull(in), charset))) {
        return read(bufferedReader);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  abstract List<ClassicPoetry> read(BufferedReader reader) throws IOException;
}
