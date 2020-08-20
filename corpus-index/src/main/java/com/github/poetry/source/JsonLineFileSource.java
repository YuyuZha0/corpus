package com.github.poetry.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.ObjectMapperFactory;
import com.github.poetry.transform.PoetryTransformer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
@Slf4j
public final class JsonLineFileSource<T> implements PoetrySource {

  private final ObjectMapper objectMapper = new ObjectMapperFactory().get();
  private final File file;
  private final Class<? extends T> clazz;
  private final PoetryTransformer<? super T> transformer;

  public JsonLineFileSource(
      File file, Class<? extends T> clazz, PoetryTransformer<? super T> transformer) {
    if (!file.exists() || !file.isFile() || !file.canRead())
      throw new IllegalArgumentException(file.getAbsolutePath() + " is not a valid file path!");
    this.file = file;
    this.clazz = clazz;
    this.transformer = transformer;
  }

  @Override
  public List<GeneralChinesePoetry> get() {

    log.info("load json from file[{}]...", file.getName());
    try (InputStream in = new FileInputStream(file)) {
      List<GeneralChinesePoetry> result = new ArrayList<>();
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
        String line = reader.readLine();
        while (line != null) {
          T t = objectMapper.readValue(line, clazz);
          result.add(transformer.apply(t));
          line = reader.readLine();
        }
      }

      log.info("[{}] entity read.", result.size());
      return result;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
