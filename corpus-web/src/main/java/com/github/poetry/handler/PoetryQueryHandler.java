package com.github.poetry.handler;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.lucene.LuceneFacade;
import com.github.poetry.lucene.QueryParams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/21
 */
@Slf4j
@Singleton
public final class PoetryQueryHandler implements Handler<RoutingContext> {

  private static final int DEFAULT_RESULT_SIZE = 10;

  private final LuceneFacade luceneFacade;

  @Inject
  public PoetryQueryHandler(LuceneFacade luceneFacade) {
    this.luceneFacade = luceneFacade;
  }

  private static void endReq(HttpServerResponse response, SearchResult searchResult) {
    response
        .putHeader("content-type", "application/json;charset=utf-8")
        .setStatusCode(200)
        .end(Json.encodeToBuffer(searchResult));
  }

  @Override
  public void handle(RoutingContext routingContext) {

    HttpServerResponse response = routingContext.response();
    MultiMap multiMap = routingContext.request().formAttributes();

    String query = multiMap.get("query");
    if (StringUtils.isBlank(query)) {
      endReq(response, new SearchResult("empty query!"));
      return;
    }

    QueryParams queryParams =
        new QueryParams(
            query,
            multiMap.get("preTag"),
            multiMap.get("postTag"),
            NumberUtils.toInt(multiMap.get("maxSize"), DEFAULT_RESULT_SIZE));

    try {
      List<GeneralChinesePoetry> poetryList = luceneFacade.apply(queryParams);
      if (!poetryList.isEmpty()) {
        log.info(
            "query [{}], [{}] result(s) found, first title is [{}]",
            query,
            poetryList.size(),
            poetryList.get(0).getTitle());
      }
      endReq(response, new SearchResult(0, "", poetryList));
    } catch (Exception e) {
      String msg = e.getMessage();
      log.error("exception:", e);
      endReq(response, new SearchResult(msg));
    }
  }

  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @Getter
  @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
  private static final class SearchResult implements Serializable {
    private static final long serialVersionUID = -7036094043351059691L;
    private final int status;
    private final String msg;
    private final List<GeneralChinesePoetry> documents;

    private SearchResult(String msg) {
      this.status = -1;
      this.msg = msg;
      this.documents = Collections.emptyList();
    }
  }
}
