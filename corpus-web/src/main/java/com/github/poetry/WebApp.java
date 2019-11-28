package com.github.poetry;

import com.github.poetry.handler.PoetryQueryHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
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
import org.apache.commons.lang3.math.NumberUtils;

@Slf4j
public final class WebApp {
  static {
    System.setProperty(
        LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory.class.getName());
  }

  private final String indexPath;
  private final int port;

  private WebApp(String indexPath, int port) {
    this.indexPath = indexPath;
    this.port = port;
  }

  public static void main(String[] args) {
    Options options = new Options();
    options.addOption(new Option("p", "port", true, "listening port"));
    options.addOption(new Option("h", "help", false, "print help"));
    options.addOption(new Option("i", "index", true, "the lucene index path"));

    CommandLineParser parser = new DefaultParser();
    CommandLine commandLine = null;
    try {
      commandLine = parser.parse(options, args);
    } catch (ParseException ignore) {
    }
    if (commandLine == null || commandLine.hasOption('h')) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("jar -jar <fileName>", options);
      return;
    }

    // String indexPath = "/Users/zhaoyuyu/IdeaProjects/corpus/temp_index_dir";
    String indexPath = commandLine.getOptionValue('i');
    if (indexPath == null) {
      throw new IllegalArgumentException("index path is required!");
    }
    int port = NumberUtils.toInt(commandLine.getOptionValue('p'), 8080);
    new WebApp(indexPath, port).start();
  }

  private void start() {

    Vertx vertx = Vertx.vertx();
    HttpServer httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);

    // BodyHandler 之前不能有任何BlockingHandler,且本身不能Blocking
    router.route("/api/*").handler(BodyHandler.create());
    router.route().blockingHandler(LoggerHandler.create());
    router
        .route("/api/query-poetry")
        .method(HttpMethod.POST)
        .blockingHandler(new PoetryQueryHandler(indexPath));
    router.route("/").handler(ctx -> ctx.response().sendFile("webroot/index.html").end());
    router
        .route("/static/*")
        .handler(StaticHandler.create("webroot/static").setDefaultContentEncoding("UTF-8"));

    Runtime.getRuntime().addShutdownHook(new Thread(vertx::close));

    log.info("starting listening on port: {}", port);
    httpServer.requestHandler(router).listen(port);
  }
}
