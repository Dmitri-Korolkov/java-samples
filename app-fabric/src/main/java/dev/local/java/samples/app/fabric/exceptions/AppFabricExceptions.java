package dev.local.java.samples.app.fabric.exceptions;

public class AppFabricExceptions extends Exception {

  public AppFabricExceptions() {
  }

  public AppFabricExceptions(String message) {
    super(message);
  }

  public AppFabricExceptions(String message, Throwable cause) {
    super(message, cause);
  }

  public AppFabricExceptions(Throwable cause) {
    super(cause);
  }

  public AppFabricExceptions(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
