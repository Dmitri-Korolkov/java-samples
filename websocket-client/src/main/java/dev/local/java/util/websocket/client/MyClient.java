package dev.local.java.util.websocket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.util.*;

@ClientEndpoint
public class MyClient extends Endpoint {

  private static final Logger log = LoggerFactory.getLogger(MyClient.class);

  private ClientEndpointConfig cec;

  private Map<String, List<String>> reqHeaders;

  public MyClient() {
    reqHeaders = new HashMap<>();
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {
    log.info("Open session: {}", session.getBasicRemote());
  }

  public ClientEndpointConfig getCec() {
    return ClientEndpointConfig.Builder.create().configurator(
            new ClientEndpointConfig.Configurator() {
              @Override
              public void beforeRequest(Map<String, List<String>> headers) {
                headers.putAll(reqHeaders);
              }
            }).build();
  }

  public void addHeader(String key, String... values){
    reqHeaders.put(key, Arrays.asList(values));
  }

  public Map<String, List<String>> getReqHeaders() {
    return reqHeaders;
  }
}
