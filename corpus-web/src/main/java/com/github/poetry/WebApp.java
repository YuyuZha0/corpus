package com.github.poetry;

import com.github.poetry.handler.PoetryQueryHandler;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.concurrent.Executors;

@Slf4j
@Singleton
public final class WebApp implements AutoCloseable {
  static {
    System.setProperty(
        LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory.class.getName());
  }

  private final PoetryQueryHandler poetryQueryHandler;
  private final Vertx vertx;
  private final HttpServer httpServer;
  private final int port;

  @Inject
  private WebApp(PoetryQueryHandler poetryQueryHandler, Vertx vertx, @Named("cli.port") int port) {
    this.poetryQueryHandler = poetryQueryHandler;
    this.vertx = vertx;
    this.httpServer = vertx.createHttpServer();
    this.port = port;
  }

  public static void main(String[] args) {
    Options options = new Options();
    options.addOption(new Option("p", "port", true, "listening port"));
    options.addOption(new Option("h", "help", false, "print help"));
    options.addOption(new Option("i", "index", true, "the lucene index path"));

    CommandLineParser parser = new DefaultParser();
    CommandLine commandLine;
    try {
      commandLine = parser.parse(options, args);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    if (commandLine.hasOption('h')) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("jar -jar <fileName>", options);
      return;
    }

    Injector injector =
        Guice.createInjector(new CommandLineModule(commandLine), new LuceneModule());
    WebApp webApp = injector.getInstance(WebApp.class);
    Runtime.getRuntime().addShutdownHook(new Thread(webApp::close));

    webApp.start();
  }

  private void start() {

    Router router = Router.router(vertx);

    // BodyHandler 之前不能有任何BlockingHandler,且本身不能Blocking
    router.route("/api/*").handler(BodyHandler.create(false));
    router.route().blockingHandler(LoggerHandler.create());
    router.post("/api/query-poetry").handler(poetryQueryHandler);
    router.route("/").handler(ctx -> ctx.response().sendFile("webroot/index.html"));
    router
        .route("/static/*")
        .handler(StaticHandler.create("webroot/static").setDefaultContentEncoding("utf-8"));

    Runtime.getRuntime().addShutdownHook(Executors.defaultThreadFactory().newThread(this::close));

    httpServer
        .requestHandler(router)
        .listen(port)
        .onSuccess(s -> log.info("WebApp started successfully on port : {}", port));
  }

  @Override
  public void close() {
    httpServer.close(
        result -> {
          if (result.succeeded()) {
            log.info("HttpServer shutdown successfully!");
          }
          vertx.close();
        });
  }
}
