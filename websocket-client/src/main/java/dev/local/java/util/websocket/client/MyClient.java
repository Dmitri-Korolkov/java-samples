package dev.local.java.util.websocket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;

@ClientEndpoint
public class MyClient {

  private static final Logger log = LoggerFactory.getLogger(MyClient.class);

  @OnOpen
  public void onOpen(Session session) {
    log.info("Connected to endpoint: {}", session.getBasicRemote());
  }

//  @OnMessage
//  public void processMessage(String message) {
//    log.info("Received message in client: {}", message);
//  }

  @OnError
  public void processError(Throwable t) {
    log.error("{}", t);
  }


}
