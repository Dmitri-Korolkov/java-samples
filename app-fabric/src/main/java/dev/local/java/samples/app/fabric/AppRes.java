package dev.local.java.samples.app.fabric;

import dev.local.java.samples.app.fabric.exceptions.AppFabricExceptions;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AppRes {

  private static final Logger log = LoggerFactory.getLogger(AppRes.class);

  public static final String SUFFIX = ".tmp";

  private AppRes() {
  }

  public static File fileFromRes(String name) throws AppFabricExceptions {

    FileOutputStream out = null;
    InputStream stream = null;

    try {
      File tempFile = File.createTempFile(name, SUFFIX);
      tempFile.deleteOnExit();
      out = new FileOutputStream(tempFile);
      ClassLoader classLoader = new AppRes().getClass().getClassLoader();

      if (classLoader == null) {
        stream = ClassLoader.getSystemResourceAsStream(name);
      }
      stream = classLoader.getResourceAsStream(name);
      IOUtils.copy(stream, out);

      return tempFile;
    } catch (Exception e) {
      throw new AppFabricExceptions(e);
    } finally {
      IOUtils.closeQuietly(out, stream);
    }

  }

}


