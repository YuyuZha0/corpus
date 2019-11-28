package com.github.poetry.source;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.transform.PoetryTransformer;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
@Slf4j
public final class JsonFileSource<T> implements PoetrySource {

  private final File file;
  private final Class<? extends T> clazz;
  private final PoetryTransformer<? super T> transformer;

  public JsonFileSource(
      File file, Class<? extends T> clazz, PoetryTransformer<? super T> transformer) {
    if (!file.exists() || !file.isFile() || !file.canRead())
      throw new IllegalArgumentException(file.getAbsolutePath() + " is not a valid file path!");
    this.file = file;
    this.clazz = clazz;
    this.transformer = transformer;
  }

  @Override
  public List<GeneralChinesePoetry> get() {

    TypeToken<?> typeToken = TypeToken.getParameterized(List.class, clazz);
    log.info("load json from file[{}]...", file.getName());
    try (InputStream in = new FileInputStream(file)) {
      List<T> list =
          GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), typeToken.getType());
      List<GeneralChinesePoetry> dump = new ArrayList<>(list.size());
      for (T t : list) {
        dump.add(transformer.apply(t));
      }
      log.info("[{}] entity read.", dump.size());
      return dump;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
