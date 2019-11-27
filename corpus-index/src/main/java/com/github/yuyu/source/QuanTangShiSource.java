package com.github.yuyu.source;

import com.github.yuyu.entity.ClassicPoetry;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author zhaoyuyu
 * @since 2019/11/22
 */
@Slf4j
public final class QuanTangShiSource implements PoetrySource {

  private static final Pattern FILE_NAME_PATTERN = Pattern.compile("^全唐诗/卷\\d+[^.]+\\.txt$");

  private final String[] fileNames;

  public QuanTangShiSource() {

    List<String> list = new ArrayList<>();
    try {
      CodeSource src = getClass().getProtectionDomain().getCodeSource();
      URL jar = src.getLocation();
      try (ZipInputStream in = new ZipInputStream(jar.openStream())) {
        ZipEntry entry = in.getNextEntry();
        while (entry != null) {
          String name = entry.getName();
          if (FILE_NAME_PATTERN.matcher(name).matches()) {
            list.add(name);
          }
          entry = in.getNextEntry();
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    //    try (InputStream in = getClass().getClassLoader().getResourceAsStream(DIR)) {
    //      try (BufferedReader reader =
    //          new BufferedReader(
    //              new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8))) {
    //        String line = reader.readLine();
    //        while (line != null) {
    //          if (FILE_NAME_PATTERN.matcher(line).matches()) {
    //            list.add(line);
    //          }
    //          line = reader.readLine();
    //        }
    //      }
    //    } catch (IOException e) {
    //      throw new RuntimeException(e);
    //    }

    this.fileNames = list.toArray(new String[0]);
  }

  public List<String> getFileNames() {
    return ImmutableList.copyOf(fileNames);
  }

  @Override
  public List<ClassicPoetry> get() {
    log.info("[{}]全唐诗 found", fileNames.length);
    List<List<ClassicPoetry>> temp = new ArrayList<>(fileNames.length);
    int len = 0;
    for (String fileName : fileNames) {
      EmbeddedSource embeddedSource = new EmbeddedSource(fileName);
      List<ClassicPoetry> list = embeddedSource.get();
      temp.add(list);
      len += list.size();
    }
    List<ClassicPoetry> total = new ArrayList<>(len);
    for (List<ClassicPoetry> list : temp) {
      total.addAll(list);
    }
    return total;
  }

  public static final class EmbeddedSource extends FixedPatternSource {

    private static final Pattern HEADER_PATTERN = Pattern.compile("^第\\d+笔$");
    private static final Pattern TAIL_PATTERN = Pattern.compile("^\\[页]卷,册....\\[\\d+]\\d+,\\d+$");
    private static final Pattern REMOVE_PATTERN = Pattern.compile("\\[\\d+[]}]|^\u3000+|\u3000+$");

    private final List<String> buffer = new ArrayList<>();
    private final String name;

    public EmbeddedSource(String fileName) {
      super(fileName);
      this.name = fileName.substring(fileName.indexOf(' '), fileName.lastIndexOf('.'));
    }

    @SuppressWarnings("UnstableApiUsage")
    private static List<String> splitWithBlank(String s) {
      return Splitter.on(CharMatcher.whitespace()).omitEmptyStrings().splitToList(s);
    }

    @Override
    boolean startPattern(String currentLine, String nextLine) {
      return HEADER_PATTERN.matcher(currentLine).matches();
    }

    @Override
    boolean endPattern(String currentLine, String nextLine) {
      return nextLine == null
          || TAIL_PATTERN.matcher(nextLine).matches()
          || HEADER_PATTERN.matcher(nextLine).matches();
    }

    @Override
    ClassicPoetry resolveLines(String[] lines) {
      buffer.clear();
      for (int i = 1; i < lines.length; ++i) {
        if (CharMatcher.whitespace().matchesAllOf(lines[i])) continue;
        String line = REMOVE_PATTERN.matcher(lines[i]).replaceAll("");
        buffer.add(line);
      }
      if (buffer.size() < 2) return null;
      List<String> headerList = splitWithBlank(buffer.get(0));
      int hl = headerList.size();
      String title = null, subtitle = null, author = null;
      if (hl == 1) {
        title = headerList.get(0);
        author = name;
      } else if (hl == 2) {
        title = headerList.get(0);
        author = headerList.get(1);
      } else {
        title = headerList.get(0);
        subtitle = headerList.get(1);
        author = headerList.get(2);
      }
      return ClassicPoetry.builder()
          .title(title)
          .subtitle(subtitle)
          .author(author)
          .content(Joiner.on(LINE_BREAKER).join(buffer.subList(1, buffer.size())))
          .type("唐诗")
          .dynasty("唐")
          .build();
    }
  }
}
