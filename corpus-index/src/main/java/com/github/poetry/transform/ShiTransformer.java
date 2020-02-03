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

    GeneralChinesePoetry poetry = new GeneralChinesePoetry();

    String[] titles = TransformUtils.splitWithBlank(shi.getTitle());
    if (titles.length == 1) {
      poetry.setTitle(titles[0]);
    } else if (titles.length >= 2) {
      poetry.setTitle(titles[0]);
      poetry.setSubtitle(titles[1]);
    }

    poetry.setContent(TransformUtils.joinParagraphs(shi.getParagraphs()));
    poetry.setAuthor(shi.getAuthor());
    poetry.setDynasty(dynasty);
    poetry.setType("诗");
    return poetry;
  }
}
