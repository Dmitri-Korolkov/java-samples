package dev.local.java.samples.app.fabric;

import dev.local.java.samples.app.fabric.exceptions.AppFabricExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class AppProperties {

  private static final Logger log = LoggerFactory.getLogger(AppProperties.class);

  private static Properties properties;

  private AppProperties() {
  }

  public static String getProp(String key) throws AppFabricExceptions {

    if (properties == null) {
      try (InputStream input = AppProperties.class.getClassLoader().getResourceAsStream("application.properties")) {
        properties = new Properties();
        properties.load(input);
      } catch (Exception e) {
        log.error("read properties error: {}", e);
        throw new AppFabricExceptions("AppFabric can't find application.properties");
      }
      log.info("AppProperties init");
    }
    return properties.getProperty(key);
  }

}
