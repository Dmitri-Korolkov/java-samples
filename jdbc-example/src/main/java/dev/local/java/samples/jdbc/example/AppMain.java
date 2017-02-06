package dev.local.java.samples.jdbc.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class AppMain {

  private static final Logger log = LoggerFactory.getLogger(AppMain.class);

  private static final String INSERT_USER = "INSERT INTO  user_role (name,role) VALUES (?,?);";
  private static final String URL = "jdbc:postgresql://127.0.0.1:5432/dev";
  private static final String LOGIN = "postgres";
  private static final String PASS = "123";

  private AppMain() {
  }

  public static void main(String[] args) {

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    try {
      Class.forName("org.postgresql.Driver");
      connection = DriverManager.getConnection(URL, LOGIN, PASS);
      connection.setAutoCommit(false);

      preparedStatement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, "test");
      preparedStatement.setString(2, "admin");

      int result = preparedStatement.executeUpdate();
      connection.commit();

      log.info("{}", result);
      resultSet = preparedStatement.getGeneratedKeys();

      for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
        log.info("{}", resultSet.getMetaData().getColumnName(i));
      }

      if (resultSet.next()) {
        log.info("result: {} : {}", result, resultSet.getObject("id"));
      }

    } catch (Exception e) {
      try {
        if (connection != null) {
          connection.rollback();
        }
      } catch (Exception ex) {
        log.error("{}", ex);
      }
      log.error("{}", e);
    } finally {
      closeSQL(resultSet, preparedStatement, connection);
    }
  }

  private static void closeSQL(AutoCloseable... closeables) {
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
