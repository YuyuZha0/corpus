package com.github.poetry.transform;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.Shi;
import lombok.NonNull;

import java.io.File;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public final class ShiTransformer implements PoetryTransformer<Shi> {

  private final String dynasty;

  public ShiTransformer(@NonNull File file) {
    this.dynasty = TransformUtils.resolveDynastyFromFileName(file);
  }

  @Override
  public GeneralChinesePoetry apply(@NonNull Shi shi) {

    GeneralChinesePoetry.GeneralChinesePoetryBuilder builder = GeneralChinesePoetry.builder();
    String[] titles = TransformUtils.splitWithBlank(shi.getTitle());
    if (titles.length == 1) {
      builder.title(titles[0]);
    } else if (titles.length == 2) {
      builder.title(titles[0]);
      builder.subtitle(titles[1]);
    }
    return builder
        .content(TransformUtils.joinParagraphs(shi.getParagraphs()))
        .author(shi.getAuthor())
        .dynasty(dynasty)
        .type("诗")
        .build();
  }
}
