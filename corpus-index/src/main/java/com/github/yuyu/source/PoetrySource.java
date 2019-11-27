package com.github.yuyu.source;

import com.github.yuyu.entity.ClassicPoetry;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
public interface PoetrySource extends Supplier<List<ClassicPoetry>> {

  String LINE_BREAKER = "<br>";
}
