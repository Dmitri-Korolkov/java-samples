package dev.local.java.util.websocket.client;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.container.grizzly.GrizzlyClientSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AppMain {

  private static final Logger log = LoggerFactory.getLogger(AppMain.class);

  final static CountDownLatch messageLatch = new CountDownLatch(1);

  private static Properties properties;

  public static void main(String[] args) {

    try (InputStream input = AppMain.class.getClassLoader().getResourceAsStream("application.properties")) {
      properties = new Properties();
      properties.load(input);
    } catch (Exception e) {
      log.error("read properties error: {}", e);
      System.exit(-1);
    }

    try {
      ClientManager clientManager = ClientManager.createClient();
      if (properties.getProperty("proxy.host") != null)
        clientManager.getProperties().put(GrizzlyClientSocket.PROXY_URI, properties.getProperty("proxy.host"));

      MyClient myClient = new MyClient();
      myClient.addHeader("test", "ok");

      Session session = clientManager.connectToServer(
              myClient, myClient.getCec(), URI.create(properties.getProperty("ws.host"))
      );

      session.addMessageHandler(new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String message) {
          log.info("message: {}", message);
        }
      });

      messageLatch.await(100, TimeUnit.SECONDS);
    } catch (Exception e) {
      log.error("ws error: {}", e);
    }
  }

}
