package dev.local.java.samples.smtp.server.mock.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.AuthenticationHandler;
import org.subethamail.smtp.AuthenticationHandlerFactory;

import java.util.ArrayList;
import java.util.List;

public class ServerAuthFactory implements AuthenticationHandlerFactory {

  private static final Logger log = LoggerFactory.getLogger(ServerAuthFactory.class);

  @Override
  public List<String> getAuthenticationMechanisms() {
    return new ArrayList<String>() {{
      add("LOGIN");
    }};
  }

  @Override
  public AuthenticationHandler create() {
    return new ServerAuthHandler();
  }
}
