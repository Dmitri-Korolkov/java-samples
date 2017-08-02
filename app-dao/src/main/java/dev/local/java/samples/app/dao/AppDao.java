package dev.local.java.samples.app.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class AppDao implements AutoCloseable {

  private static final Logger log = LoggerFactory.getLogger(AppDao.class);

  private Connection connection;

  private PreparedStatement preparedStatement;

  private ResultSet resultSet;

  private Boolean committed = false;

  public AppDao query(String query) throws SQLException {
    connection = AppDaoPool.connection();
    connection.setAutoCommit(false);
    preparedStatement = connection.prepareStatement(query);
    return this;
  }

  public AppDao query(String query, int key) throws SQLException {
    connection = AppDaoPool.connection();
    connection.setAutoCommit(false);
    preparedStatement = connection.prepareStatement(query, key);
    return this;
  }

  public AppDao setParam(int index, Object param) throws SQLException {
    if (param != null) {
      preparedStatement.setObject(index, param);
    } else {
      preparedStatement.setNull(index, Types.NULL);
    }
    return this;
  }

  public AppDao setParams(Object... params) throws SQLException {
    for (int i = 0; i < params.length; i++) {
      if (params[i] != null) {
        preparedStatement.setObject(i + 1, params[i]);
      } else {
        preparedStatement.setNull(i + 1, Types.NULL);
      }
    }
    return this;
  }

  public boolean execute() throws SQLException {
    return preparedStatement.execute();
  }

  public ResultSet executeQuery() throws SQLException {
    resultSet = preparedStatement.executeQuery();
    return resultSet;
  }

  public int executeUpdate() throws SQLException {
    return preparedStatement.executeUpdate();
  }

  public void commit() throws SQLException {
    if (connection != null) {
      connection.commit();
    }
    committed = true;
  }

  public void rollback() {
    try {
      if (connection != null) {
        connection.rollback();
      }
    } catch (Exception e) {
      log.error("rollback exception: {}", e);
    }
  }

  @Override
  public void close() {
    if (!committed) {
      rollback();
    }
    closeSQL(resultSet, preparedStatement, connection);
  }

  /* GETTERS */
  public Connection getConnection() {
    return connection;
  }

  public PreparedStatement getPreparedStatement() {
    return preparedStatement;
  }

  public ResultSet getResultSet() {
    return resultSet;
  }

  /* Class util methods */
  private void closeSQL(AutoCloseable... closeables) {
    for (AutoCloseable closeableTmp : closeables) {
      try {
        if (closeableTmp != null) {
          closeableTmp.close();
        }
      } catch (Exception e) {
        log.error("{}", e);
      }
    }
  }

}