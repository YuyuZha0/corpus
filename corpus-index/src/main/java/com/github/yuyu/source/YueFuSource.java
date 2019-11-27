package com.github.yuyu.source;

import com.github.yuyu.entity.ClassicPoetry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
public final class YueFuSource extends FixedPatternSource {

  private static final String FILE_NAME = "乐府诗集.txt";

  private static final String SEP = "---";

  public YueFuSource() {
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
    for (String line : lines) {
      int split = line.indexOf('：');
      if (split < 0) {
        continue;
      }
      map.put(line.substring(0, split), line.substring(split + 1));
    }

    return ClassicPoetry.builder()
        .title(map.get("篇目"))
        .subtitle(map.get("诗题"))
        .author(map.get("作者"))
        .content(map.get("诗文"))
        .dynasty(map.get("朝代"))
        .type("乐府")
        .build();
  }
}
