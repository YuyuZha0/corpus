package com.github.poetry.pipeline;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.poetry.json.ObjectMapperFactory;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 */
@Getter
public final class IndexContext {

  private final ObjectMapper objectMapper = new ObjectMapperFactory().get();

  @Setter private int approxCount;
}
