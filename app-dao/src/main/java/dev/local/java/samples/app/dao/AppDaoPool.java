package dev.local.java.samples.app.dao;

import dev.local.java.samples.app.fabric.AppProperties;
import dev.local.java.samples.app.fabric.exceptions.AppFabricExceptions;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class AppDaoPool {

  private static final Logger log = LoggerFactory.getLogger(AppDaoPool.class);

  private static AppDaoPool instance;

  private BasicDataSource connectionPool;

  private AppDaoPool() {

    try {
      connectionPool = new BasicDataSource();
      connectionPool.setUsername(strFromProp("db.pool.login", ""));
      connectionPool.setPassword(strFromProp("db.pool.pass", ""));
      connectionPool.setDriverClassName(strFromProp("db.pool.driver", ""));
      connectionPool.setUrl(strFromProp("db.pool.url", ""));
      connectionPool.setValidationQuery(strFromProp("db.pool.validate.query", "SELECT 1"));
      connectionPool.setInitialSize(intFromProp("db.pool.initialSize", 1));
      connectionPool.setMaxActive(intFromProp("db.pool.maxActive", 1));
      connectionPool.setMaxIdle(intFromProp("db.pool.maxIdle", 1));
      connectionPool.setMinIdle(intFromProp("db.pool.minIdle", 1));
    } catch (Exception e) {
      log.error("init pool error: {}", e);
    }
  }

  public static Connection connection() throws SQLException {

    if (instance == null) {
      instance = new AppDaoPool();
    }
    return instance.connectionPool.getConnection();
  }

  private String strFromProp(String key, String defaultValue) throws AppFabricExceptions {
    String value = AppProperties.getProp(key);
    if (value != null) {
      return value;
    }
    return defaultValue;
  }

  private int intFromProp(String key, int defaultValue) throws AppFabricExceptions {
    String value = AppProperties.getProp(key);
    if (value != null) {
      return Integer.valueOf(value);
    }
    return defaultValue;
  }

}


