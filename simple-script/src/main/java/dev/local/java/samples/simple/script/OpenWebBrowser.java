package dev.local.java.samples.simple.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;

public class OpenWebBrowser {

  private static final Logger log = LoggerFactory.getLogger(OpenWebBrowser.class);

  public static void main(String[] args) {

    if (Desktop.isDesktopSupported()) {
      try {
        Desktop.getDesktop().browse(new URI("https://www.google.com"));
      } catch (Exception e) {
        log.error("{}", e);
      }
    }
  }

}
