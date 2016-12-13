package dev.local.java.util.websocket.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.*;

public class AppMain extends WebSocketServer {

  private static final Logger log = LoggerFactory.getLogger(AppMain.class);

  private static Properties properties;

  private Set<WebSocket> connections;

  public AppMain(int port) {
    super(new InetSocketAddress(port));
    connections = new HashSet<>();
  }

  public static void main(String[] args) {

    try (InputStream input = AppMain.class.getClassLoader().getResourceAsStream("application.properties")) {
      properties = new Properties();
      properties.load(input);
    } catch (Exception e) {
      log.error("read properties error: {}", e);
      System.exit(-1);
    }

    try {
      AppMain main = new AppMain(Integer.valueOf(properties.getProperty("ws.port")));
      main.start();
    } catch (Exception e) {
      log.error("ws error: {}", e);
    }
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    connections.add(conn);
    log.info("New connection from: {}", conn.getRemoteSocketAddress().getAddress().getHostAddress());
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    connections.remove(conn);
    log.info("Closed connection to {}", conn.getRemoteSocketAddress().getAddress().getHostAddress());
  }

  @Override
  public void onMessage(WebSocket conn, String message) {

  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    log.error("ERROR from: {}", conn.getRemoteSocketAddress().getAddress().getHostAddress());
  }

}
