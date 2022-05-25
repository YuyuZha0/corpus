package com.github.poetry.transform;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.json.Shi;
import com.github.poetry.text.TextUtil;
import lombok.NonNull;

import java.nio.file.Path;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public final class ShiTransformer implements PoetryTransformer<Shi> {

  private final String dynasty;

  public ShiTransformer(@NonNull Path path) {
    this.dynasty = TextUtil.resolveDynastyFromFileName(path);
  }

  @Override
  public GeneralChinesePoetry apply(@NonNull Shi shi) {

    GeneralChinesePoetry poetry = new GeneralChinesePoetry();

    String[] titles = TextUtil.splitWithBlank(shi.getTitle());
    if (titles.length == 1) {
      poetry.setTitle(titles[0]);
    } else if (titles.length >= 2) {
      poetry.setTitle(titles[0]);
      poetry.setSubtitle(titles[1]);
    }

    poetry.setContent(TextUtil.joinParagraphs(shi.getParagraphs()));
    poetry.setAuthor(shi.getAuthor());
    poetry.setDynasty(dynasty);
    poetry.setType("诗");
    return poetry;
  }
}
