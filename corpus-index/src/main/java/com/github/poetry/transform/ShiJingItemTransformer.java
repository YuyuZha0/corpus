package com.github.poetry.transform;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.ShiJingItem;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public final class ShiJingItemTransformer implements PoetryTransformer<ShiJingItem> {

  @Override
  public GeneralChinesePoetry apply(ShiJingItem shiJingItem) {
    return GeneralChinesePoetry.builder()
        .title(shiJingItem.getTitle())
        .subtitle(shiJingItem.getChapter() + "\u3000" + shiJingItem.getSection())
        .content(TransformUtils.joinParagraphs(shiJingItem.getContent()))
        .dynasty("先秦")
        .type("诗经")
        .build();
  }
}
