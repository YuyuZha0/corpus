package com.github.yuyu.text;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
@RequiredArgsConstructor
public final class ClassPathText implements Supplier<List<String>> {

  private final String fileName;

  @Override
  public List<String> get() {
    try {
      return getLines(Function.identity(), s -> true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> List<T> getLines(
      @NonNull Function<? super String, ? extends T> function,
      @NonNull Predicate<? super T> predicate)
      throws IOException {
    List<T> result = new ArrayList<>();
    try (InputStream in = getClass().getClassLoader().getResourceAsStream(fileName)) {
      try (BufferedReader reader =
          new BufferedReader(
              new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8))) {
        String line = reader.readLine();
        while (line != null) {
          T t = function.apply(line);
          if (predicate.test(t)) {
            result.add(t);
          }
          line = reader.readLine();
        }
      }
    }
    return result;
  }
}
