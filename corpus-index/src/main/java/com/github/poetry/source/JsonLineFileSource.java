package com.github.poetry.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.ObjectMapperFactory;
import com.github.poetry.transform.PoetryTransformer;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
@Slf4j
public final class JsonLineFileSource<T> implements PoetrySource {

  private final ObjectMapper objectMapper = new ObjectMapperFactory().get();
  private final Path path;
  private final Class<? extends T> clazz;
  private final PoetryTransformer<? super T> transformer;

  public JsonLineFileSource(
      @NonNull Path path,
      @NonNull Class<? extends T> clazz,
      @NonNull PoetryTransformer<? super T> transformer) {
    Preconditions.checkArgument(
        Files.isRegularFile(path) && Files.isReadable(path),
        "`%s is not a valid file path!`",
        path.toString());
    this.path = path;
    this.clazz = clazz;
    this.transformer = transformer;
  }

  @Override
  public List<GeneralChinesePoetry> get() {

    log.info("load json from file[{}]...", path);
    try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      List<GeneralChinesePoetry> result = new ArrayList<>();
      String line;
      while ((line = reader.readLine()) != null) {
        T t = objectMapper.readValue(line, clazz);
        result.add(transformer.apply(t));
      }
      log.info("[{}] entity read.", result.size());
      return result;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
