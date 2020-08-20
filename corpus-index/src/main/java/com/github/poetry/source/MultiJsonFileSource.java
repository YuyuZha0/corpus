package com.github.poetry.source;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.transform.PoetryTransformer;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author zhaoyuyu
 * @since 2019/11/28
 */
@Slf4j
public final class MultiJsonFileSource<T> implements PoetrySource {

  private final File root;
  private final Class<? extends T> clazz;
  private final Function<? super File, PoetryTransformer<? super T>> transformerFactory;
  private final Predicate<? super String> filter;

  public MultiJsonFileSource(
      @NonNull File root,
      @NonNull Class<? extends T> clazz,
      @NonNull Function<? super File, PoetryTransformer<? super T>> transformerFactory,
      @NonNull Predicate<? super String> filter) {
    if (!root.exists() || !root.isDirectory())
      throw new IllegalArgumentException("invalid root:" + root.getAbsolutePath());
    this.root = root;
    this.clazz = clazz;
    this.transformerFactory = transformerFactory;
    this.filter = filter;
  }

  @Override
  @SuppressWarnings("UnstableApiUsage")
  public List<GeneralChinesePoetry> get() {
    Iterable<File> files = Files.fileTreeTraverser().breadthFirstTraversal(root);
    List<List<GeneralChinesePoetry>> temp = new ArrayList<>();
    int totalCount = 0;
    for (File file : files) {
      if (!filter.test(file.getName())) continue;
      JsonFileSource<T> jsonFileSource =
          new JsonFileSource<>(file, clazz, transformerFactory.apply(file));
      List<GeneralChinesePoetry> poetryList = jsonFileSource.get();
      temp.add(poetryList);
      totalCount += poetryList.size();
    }
    log.info("read [{}] on directory [{}].", totalCount, root.getAbsolutePath());
    List<GeneralChinesePoetry> result = new ArrayList<>(totalCount);
    for (List<GeneralChinesePoetry> list : temp) {
      result.addAll(list);
    }
    return result;
  }
}
