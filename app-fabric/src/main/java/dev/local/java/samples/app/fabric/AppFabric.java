package dev.local.java.samples.app.fabric;

import dev.local.java.samples.app.fabric.exceptions.AppFabricExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class AppFabric {

  private static final Logger log = LoggerFactory.getLogger(AppFabric.class);

  private static AppFabric instance;

  private Map<String, Object> beans;

  private AppFabric() {
    beans = new HashMap<>();
  }

  private void init() {

  }

  /**
   * return bean
   *
   * @param name
   * @return
   * @throws AppFabricExceptions
   */
  public static Object getBean(String name) throws AppFabricExceptions {
    if (instance == null) {
      instance = new AppFabric();
      instance.init();
    }

    if (instance.beans.containsKey(name)) {
      return instance.beans.get(name);
    }
    throw new AppFabricExceptions("AppFabric not contain bean whith name " + name);
  }
}
