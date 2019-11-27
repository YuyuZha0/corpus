package com.github.yuyu.source;

import com.github.yuyu.entity.ClassicPoetry;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
abstract class LookaheadFixedLineSource extends AbstractClassPathSource {

  private final int lineCount;

  LookaheadFixedLineSource(String fileName, int lineCount) {
    super(fileName);
    if (lineCount < 0) throw new IllegalArgumentException("illegal line count:" + lineCount);
    this.lineCount = lineCount;
  }

  abstract boolean lookahead(String line);

  abstract ClassicPoetry resolveLines(String[] lines);

  @Override
  final List<ClassicPoetry> read(BufferedReader reader) throws IOException {

    List<ClassicPoetry> result = new ArrayList<>(300);

    String line = reader.readLine();
    int lineNumber = 0;
    while (line != null) {
      ++lineNumber;
      if (lookahead(line)) {
        String[] lines = new String[lineCount];
        lines[0] = line;
        for (int i = 1; i < lines.length; ++i) {
          line = reader.readLine();
          if (line == null) {
            throw new NullPointerException(
                "no more lines found while resolving, current line number:" + lineNumber);
          }
          lines[i] = line;
        }
        ClassicPoetry classicPoetry = resolveLines(lines);
        if (classicPoetry != null) {
          result.add(classicPoetry);
        }
      }

      line = reader.readLine();
    }
    return result;
  }
}
