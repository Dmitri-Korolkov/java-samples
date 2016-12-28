package dev.local.java.samples.smtp.server.mock;

import dev.local.java.samples.smtp.server.mock.server.ServerAuthFactory;
import dev.local.java.samples.smtp.server.mock.server.ServerMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

public class AppMain {

  private static final Logger log = LoggerFactory.getLogger(AppMain.class);

  public static void main(String[] args) {
    SMTPServer smtpServer = new SMTPServer(
            new SimpleMessageListenerAdapter(new ServerMessageListener()),
            new ServerAuthFactory()
    );

    smtpServer.setHostName("localhost");
    smtpServer.setPort(25);

    smtpServer.setEnableTLS(true);
    smtpServer.start();
  }

}
