package com.github.poetry.transform;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.Ci;
import com.github.poetry.text.TextUtil;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public final class CiTransformer implements PoetryTransformer<Ci> {

  @Override
  public GeneralChinesePoetry apply(Ci ci) {

    GeneralChinesePoetry poetry = new GeneralChinesePoetry();
    String[] titles = TextUtil.splitWithMidDot(ci.getRhythmic());
    if (titles.length == 1) {
      poetry.setTitle(titles[0]);
    } else if (titles.length >= 2) {
      poetry.setTitle(titles[0]);
      poetry.setSubtitle(titles[1]);
    }
    poetry.setContent(TextUtil.joinParagraphs(ci.getParagraphs()));
    poetry.setDynasty("宋");
    poetry.setType("词");
    poetry.setAuthor(ci.getAuthor());
    return poetry;
  }
}
