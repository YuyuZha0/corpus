package com.github.poetry.transform;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.Qu;
import com.github.poetry.text.TextUtil;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public final class QuTransformer implements PoetryTransformer<Qu> {

  @Override
  public GeneralChinesePoetry apply(Qu qu) {

    GeneralChinesePoetry poetry = new GeneralChinesePoetry();
    String[] titles = TextUtil.splitWithMidDot(qu.getTitle());
    if (titles.length == 1) {
      poetry.setTitle(titles[0]);
    } else if (titles.length >= 2) {
      poetry.setTitle(titles[0]);
      poetry.setSubtitle(titles[1]);
    }
    poetry.setContent(TextUtil.joinParagraphs(qu.getParagraphs()));
    poetry.setType("曲");
    poetry.setDynasty("元");
    poetry.setAuthor(qu.getAuthor());
    return poetry;
  }
}
