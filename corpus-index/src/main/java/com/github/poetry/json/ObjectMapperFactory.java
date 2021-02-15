package com.github.poetry.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.function.Supplier;

/**
 * @author fishzhao
 * @since 2020-08-20
 */
public final class ObjectMapperFactory implements Supplier<ObjectMapper> {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Override
  public ObjectMapper get() {
    return OBJECT_MAPPER;
  }
}
