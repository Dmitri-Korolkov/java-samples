package dev.local.java.samples.smtp.server.mock.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerMessageListener implements SimpleMessageListener {

  private static final Logger log = LoggerFactory.getLogger(ServerMessageListener.class);

  @Override
  public boolean accept(String s, String s1) {
    return true;
  }

  @Override
  public void deliver(String from, String to, InputStream inputStream) throws TooMuchDataException, IOException {
    log.info("from: {}, to: {}, msg: {}", from, to, convertStreamToString(inputStream));
  }

  private String convertStreamToString(InputStream is) {

    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();

    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }
    } catch (IOException e) {
     log.error("{}",e);
    }
    return sb.toString();
  }

}
