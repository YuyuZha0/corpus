package com.github.poetry.pipeline;

import com.github.poetry.entity.GeneralChinesePoetry;
import lombok.NonNull;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 */
public abstract class ForwardingPipeline implements Pipeline {

  private final String name;

  private final Pipeline next;

  public ForwardingPipeline(@NonNull String name, @NonNull Pipeline next) {
    this.name = name;
    this.next = next;
  }

  @Override
  public abstract void process(IndexContext ctx, Iterable<GeneralChinesePoetry> poetries);

  @Override
  public void forward(IndexContext ctx, Iterable<GeneralChinesePoetry> poetries) {
    next.process(ctx, poetries);
  }

  @Override
  public String toString() {
    return name + "->" + next.toString();
  }
}
