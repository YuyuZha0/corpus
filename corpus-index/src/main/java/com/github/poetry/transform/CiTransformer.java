package com.github.poetry.transform;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.Ci;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public final class CiTransformer implements PoetryTransformer<Ci> {

  @Override
  public GeneralChinesePoetry apply(Ci ci) {

    GeneralChinesePoetry.GeneralChinesePoetryBuilder builder = GeneralChinesePoetry.builder();
    String[] titles = TransformUtils.splitWithMidDot(ci.getRhythmic());
    if (titles.length == 1) {
      builder.title(titles[0]);
    } else if (titles.length == 2) {
      builder.title(titles[0]);
      builder.subtitle(titles[1]);
    }
    return builder
        .content(TransformUtils.joinParagraphs(ci.getParagraphs()))
        .type("词")
        .dynasty("宋")
        .build();
  }
}
