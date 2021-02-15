package com.github.poetry.source;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.transform.PoetryTransformer;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhaoyuyu
 * @since 2019/11/28
 */
@Slf4j
public final class MultiJsonFileSource<T> implements PoetrySource {

  private final Path root;
  private final Class<? extends T> clazz;
  private final Function<? super Path, PoetryTransformer<? super T>> transformerFactory;
  private final Predicate<? super String> filter;

  public MultiJsonFileSource(
      @NonNull Path root,
      @NonNull Class<? extends T> clazz,
      @NonNull Function<? super Path, PoetryTransformer<? super T>> transformerFactory,
      @NonNull Predicate<? super String> filter) {
    Preconditions.checkArgument(
        Files.isDirectory(root) && Files.isReadable(root), "invalid root: `%s`", root.toString());
    this.root = root;
    this.clazz = clazz;
    this.transformerFactory = transformerFactory;
    this.filter = filter;
  }

  @Override
  public List<GeneralChinesePoetry> get() {
    List<Path> pathList;
    try {
      try (Stream<Path> pathStream = Files.walk(root)) {
        pathList =
            pathStream
                .filter(path -> filter.test(path.getFileName().toString()))
                .filter(Files::isRegularFile)
                .filter(Files::isReadable)
                .collect(Collectors.toList());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    log.info("[{}] file(s) found in directory: {}", pathList.size(), root);
    List<List<GeneralChinesePoetry>> temp = new ArrayList<>(pathList.size());
    for (Path path : pathList) {
      JsonFileSource<T> jsonFileSource =
          new JsonFileSource<>(path, clazz, transformerFactory.apply(path));
      List<GeneralChinesePoetry> poetryList = jsonFileSource.get();
      temp.add(poetryList);
    }
    int totalCount = temp.stream().mapToInt(List::size).sum();
    log.info("read [{}] on directory [{}].", totalCount, root);
    List<GeneralChinesePoetry> result = new ArrayList<>(totalCount);
    for (List<GeneralChinesePoetry> list : temp) {
      result.addAll(list);
    }
    return result;
  }
}
