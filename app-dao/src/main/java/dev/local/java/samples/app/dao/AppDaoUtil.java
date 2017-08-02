package dev.local.java.samples.app.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AppDaoUtil {

  private static final Logger log = LoggerFactory.getLogger(AppDaoUtil.class);

  public static void columnNames(ResultSet resultSet) throws SQLException {
    for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
      log.debug("{}: {}", i + 1, resultSet.getMetaData().getColumnName(i + 1));
    }
  }

}