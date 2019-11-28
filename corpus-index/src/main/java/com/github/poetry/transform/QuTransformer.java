package com.github.poetry.transform;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.Qu;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public final class QuTransformer implements PoetryTransformer<Qu> {

  @Override
  public GeneralChinesePoetry apply(Qu qu) {

    GeneralChinesePoetry.GeneralChinesePoetryBuilder builder = GeneralChinesePoetry.builder();
    String[] titles = TransformUtils.splitWithMidDot(qu.getTitle());
    if (titles.length == 1) {
      builder.title(titles[0]);
    } else if (titles.length == 2) {
      builder.title(titles[0]);
      builder.subtitle(titles[1]);
    }
    return builder
        .content(TransformUtils.joinParagraphs(qu.getParagraphs()))
        .type("曲")
        .dynasty("元")
        .author(qu.getAuthor())
        .build();
  }
}
