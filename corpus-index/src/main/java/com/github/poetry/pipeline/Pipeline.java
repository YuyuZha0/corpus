package com.github.poetry.pipeline;

import com.github.poetry.entity.GeneralChinesePoetry;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 */
public interface Pipeline {

  void process(IndexContext ctx, Iterable<GeneralChinesePoetry> poetries);

  void forward(IndexContext ctx, Iterable<GeneralChinesePoetry> poetries);
}
