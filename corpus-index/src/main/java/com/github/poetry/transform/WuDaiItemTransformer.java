package com.github.poetry.transform;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.WuDaiItem;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public final class WuDaiItemTransformer implements PoetryTransformer<WuDaiItem> {

  @Override
  public GeneralChinesePoetry apply(WuDaiItem wudaiItem) {

    GeneralChinesePoetry.GeneralChinesePoetryBuilder builder = GeneralChinesePoetry.builder();
    String[] titles = TransformUtils.splitWithMidDot(wudaiItem.getTitle());
    if (titles.length == 1) {
      builder.title(titles[0]);
    } else if (titles.length == 2) {
      builder.title(titles[0]);
      builder.subtitle(titles[1]);
    }
    return builder
        .author(wudaiItem.getAuthor())
        .content(TransformUtils.joinParagraphs(wudaiItem.getParagraphs()))
        .dynasty("五代十国")
        .type("词")
        .build();
  }
}
