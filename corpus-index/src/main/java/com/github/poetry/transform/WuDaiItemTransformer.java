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

    GeneralChinesePoetry poetry = new GeneralChinesePoetry();

    String[] titles = TransformUtil.splitWithMidDot(wudaiItem.getTitle());
    if (titles.length == 1) {
      poetry.setTitle(titles[0]);
    } else if (titles.length >= 2) {
      poetry.setTitle(titles[0]);
      poetry.setSubtitle(titles[1]);
    }

    poetry.setAuthor(wudaiItem.getAuthor());
    poetry.setContent(TransformUtil.joinParagraphs(wudaiItem.getParagraphs()));
    poetry.setDynasty("五代十国");
    poetry.setType("词");
    return poetry;
  }
}
