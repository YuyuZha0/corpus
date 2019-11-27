package com.github.yuyu.source;

import com.github.yuyu.entity.ClassicPoetry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
public final class SongTangWudaiCiSource extends FixedPatternSource {

  private static final String FILE_NAME = "全宋词_全唐五代词.txt";
  private static final String SEP = "---";

  public SongTangWudaiCiSource() {
    super(FILE_NAME);
  }

  @Override
  boolean startPattern(String currentLine, String nextLine) {
    return SEP.equals(currentLine) && nextLine != null;
  }

  @Override
  boolean endPattern(String currentLine, String nextLine) {
    return nextLine == null || SEP.equals(nextLine);
  }

  @Override
  ClassicPoetry resolveLines(String[] lines) {

    Map<String, String> map = new HashMap<>(5);
    for (int i = 2; i < lines.length; ++i) {
      int split = lines[i].indexOf('：');
      if (split < 0) {
        throw new IllegalArgumentException("'：' is required!");
      }
      map.put(lines[i].substring(0, split), lines[i].substring(split + 1));
    }

    return ClassicPoetry.builder()
        .title(map.get("词牌"))
        .subtitle(map.get("词题"))
        .author(map.get("作者"))
        .content(map.get("词文"))
        .dynasty(map.get("朝代"))
        .type("词")
        .build();
  }
}
