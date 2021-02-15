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

    GeneralChinesePoetry poetry = new GeneralChinesePoetry();
    poetry.setTitle(shiJingItem.getTitle());
    poetry.setSubtitle(shiJingItem.getChapter() + "\u3000" + shiJingItem.getSection());
    poetry.setContent(TransformUtil.joinParagraphs(shiJingItem.getContent()));
    poetry.setDynasty("先秦");
    poetry.setType("诗经");
    poetry.setAuthor("诗经");
    return poetry;
  }
}
