package com.github.poetry.handler;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.query.LuceneFacade;
import com.github.poetry.text.TextUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/21
 */
@Slf4j
public final class PoetryQueryHandler implements Handler<RoutingContext> {

  private static final int DEFAULT_RESULT_SIZE = 10;

  private final LuceneFacade luceneFacade;

  public PoetryQueryHandler(String indexPath) {
    this.luceneFacade = new LuceneFacade(indexPath);
  }

  @Override
  public void handle(RoutingContext routingContext) {

    HttpServerResponse response = routingContext.response();
    response.putHeader(
        HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");

    MultiMap multiMap = routingContext.request().formAttributes();

    String query = multiMap.get(ParamEnum.QUERY.key);
    if (TextUtils.isBlank(query)) {
      response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
      response.end(Json.encode(new SearchResult("empty query!")));
      return;
    }

    try {
      List<GeneralChinesePoetry> poetryList =
          luceneFacade.search(
              query,
              multiMap.get(ParamEnum.PRE_TAG.key),
              multiMap.get(ParamEnum.POST_TAG.key),
              NumberUtils.toInt(multiMap.get(ParamEnum.MAX_SIZE.key), DEFAULT_RESULT_SIZE));

      if (!poetryList.isEmpty()) {
        log.info(
            "query [{}], [{}] result(s) found, first title is [{}]",
            query,
            poetryList.size(),
            poetryList.get(0).getTitle());
      }

      SearchResult entity = new SearchResult(0, "", poetryList);
      response.end(Json.encode(entity));
    } catch (Exception e) {
      String msg = e.getMessage();
      log.error("exception:", e);
      response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      response.end(Json.encode(new SearchResult(msg)));
    }
  }

  private enum ParamEnum {
    QUERY("query"),
    PRE_TAG("preTag"),
    POST_TAG("postTag"),
    MAX_SIZE("maxSize");

    final String key;

    ParamEnum(String key) {
      this.key = key;
    }
  }

  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @Getter
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
