package dev.local.java.samples.app.dao;

public class AppDaoException extends Exception {

  public AppDaoException() {
    super();
  }

  public AppDaoException(String message) {
    super(message);
  }

  public AppDaoException(String message, Throwable cause) {
    super(message, cause);
  }

  public AppDaoException(Throwable cause) {
    super(cause);
  }

  protected AppDaoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
