package com.github.poetry.pipeline;

import com.github.poetry.entity.GeneralChinesePoetry;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 */
public abstract class EndPipeline implements Pipeline {

  private final String name;

  public EndPipeline(String name) {
    this.name = name;
  }

  @Override
  public abstract void process(IndexContext ctx, Iterable<GeneralChinesePoetry> poetries);

  @Override
  public final void forward(IndexContext ctx, Iterable<GeneralChinesePoetry> poetries) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return name;
  }
}
