package com.github.poetry.source;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.ObjectMapperFactory;
import com.github.poetry.transform.PoetryTransformer;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
@Slf4j
public final class JsonFileSource<T> implements PoetrySource {

  private final ObjectMapper objectMapper = new ObjectMapperFactory().get();
  private final Path path;
  private final Class<? extends T> clazz;
  private final PoetryTransformer<? super T> transformer;

  public JsonFileSource(
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

    JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
    log.info("load json from file[{}]...", path);
    try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ)) {
      List<T> list = objectMapper.readValue(in, type);
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
