package dev.local.java.samples.jdbc.example;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class CallProcedureFromPool {

  private static final Logger log = LoggerFactory.getLogger(CallProcedureFromPool.class);

  private static final String URL = "jdbc:postgresql://127.0.0.1:5432/dev";
  private static final String LOGIN = "postgres";
  private static final String PASS = "123";
  private static final String DRIVER = "org.postgresql.Driver";

  private static final String QUERY = "{? = call exampleProc(?,?,?,?)}";

  private BasicDataSource connectionPool;

  private CallProcedureFromPool() {
  }

  public static void main(String[] args) {

    CallProcedureFromPool pool = new CallProcedureFromPool();

    Connection connection = null;
    CallableStatement callableStatement = null;
    ResultSet resultSet = null;

    try {
      connection = pool.connection();
      callableStatement = connection.prepareCall(QUERY);

      //out
      callableStatement.registerOutParameter(1, Types.INTEGER);
      callableStatement.registerOutParameter(2, Types.REF_CURSOR);

      //in
      callableStatement.setInt(3, 1);
      callableStatement.setString(4, "aaa");
      callableStatement.setString(5, "bbb");

      callableStatement.executeQuery();
      resultSet = (ResultSet) callableStatement.getObject(2);

      while (resultSet.next()) {
        log.info("{}", resultSet.getString(1));
      }

    } catch (Exception e) {
      log.error("{}", e);
    } finally {
      closeSQL(resultSet, callableStatement, connection);
    }
  }

  private Connection connection() throws SQLException {

    if (connectionPool != null) {
      return connectionPool.getConnection();
    }

    connectionPool = new BasicDataSource();

    connectionPool.setUsername(LOGIN);
    connectionPool.setPassword(PASS);
    connectionPool.setDriverClassName(DRIVER);
    connectionPool.setValidationQuery("SELECT 1");
    connectionPool.setInitialSize(5);
    connectionPool.setMaxActive(10);
    connectionPool.setMaxIdle(10);
    connectionPool.setMinIdle(2);
    connectionPool.setUrl(URL);

    return connectionPool.getConnection();
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
