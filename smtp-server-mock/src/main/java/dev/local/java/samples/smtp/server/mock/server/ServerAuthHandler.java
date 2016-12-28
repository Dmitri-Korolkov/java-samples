package dev.local.java.samples.smtp.server.mock.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.AuthenticationHandler;
import org.subethamail.smtp.RejectException;

public class ServerAuthHandler implements AuthenticationHandler {

  private static final Logger log = LoggerFactory.getLogger(ServerAuthHandler.class);

  @Override
  public String auth(String s) throws RejectException {
    log.info("auth: {}", s);
    return "";
  }

  @Override
  public Object getIdentity() {
    return "";
  }
}
