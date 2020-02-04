package com.github.poetry.pipeline;

import com.github.poetry.json.GsonFactory;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 */
@Getter
public final class IndexContext {

  private final Gson gson = new GsonFactory().get();

  @Setter
  private int approxCount;
}
