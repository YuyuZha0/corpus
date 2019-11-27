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
abstract class FixedPatternSource extends AbstractClassPathSource {

  FixedPatternSource(String fileName) {
    super(fileName);
  }

  abstract boolean startPattern(String currentLine, String nextLine);

  abstract boolean endPattern(String currentLine, String nextLine);

  abstract ClassicPoetry resolveLines(String[] lines);

  @Override
  final List<ClassicPoetry> read(BufferedReader reader) throws IOException {
    List<ClassicPoetry> result = new ArrayList<>(300);

    String currentLine = reader.readLine(), nextLine = reader.readLine();
    List<String> buffer = new ArrayList<>();
    while (nextLine != null) {
      if (startPattern(currentLine, nextLine)) {
        while (!endPattern(currentLine, nextLine)) {
          buffer.add(currentLine);
          currentLine = nextLine;
          nextLine = reader.readLine();
        }
        buffer.add(currentLine);
        ClassicPoetry classicPoetry = resolveLines(buffer.toArray(new String[0]));
        if (classicPoetry != null) {
          result.add(classicPoetry);
        }
        buffer.clear();
      }

      currentLine = nextLine;
      nextLine = reader.readLine();
    }
    return result;
  }
}
