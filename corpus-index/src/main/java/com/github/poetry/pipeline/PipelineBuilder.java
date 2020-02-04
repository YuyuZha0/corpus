package com.github.poetry.pipeline;

import java.util.function.Function;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 */
public final class PipelineBuilder<P extends Pipeline> {

  private final Function<Pipeline, P> factory;

  @SuppressWarnings("unchecked")
  public PipelineBuilder() {
    this((Pipeline next) -> (P) next);
  }

  private PipelineBuilder(Function<Pipeline, P> nextFactory) {
    this.factory = nextFactory;
  }

  public PipelineBuilder<P> add(Function<Pipeline, ? extends ForwardingPipeline> nextFactory) {
    return new PipelineBuilder<>(factory.compose(nextFactory));
  }

  public P end(EndPipeline endPipeline) {
    return factory.apply(endPipeline);
  }
}
