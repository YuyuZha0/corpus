package com.github.poetry;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import io.vertx.core.Vertx;
import lombok.NonNull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.util.Iterator;
import java.util.Properties;

/**
 * @author fishzhao
 * @since 2020-08-20
 */
final class CommandLineModule extends AbstractModule {

  private final CommandLine commandLine;

  public CommandLineModule(@NonNull CommandLine commandLine) {
    this.commandLine = commandLine;
  }

  @Override
  protected void configure() {
    super.configure();
    Properties properties = new Properties();
    properties.putAll(System.getenv());
    properties.putAll(System.getProperties());
    Iterator<Option> iterator = commandLine.iterator();
    while (iterator.hasNext()) {
      Option option = iterator.next();
      if (option.hasArg()) {
        properties.put("cli." + option.getLongOpt(), option.getValue());
      }
    }
    Names.bindProperties(binder(), properties);
  }

  @Provides
  @Singleton
  Vertx provideVertx() {
    return Vertx.vertx();
  }
}
