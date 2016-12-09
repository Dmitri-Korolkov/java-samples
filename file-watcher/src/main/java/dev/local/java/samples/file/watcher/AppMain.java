package dev.local.java.samples.file.watcher;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class AppMain {

  private static final Logger log = LoggerFactory.getLogger(AppMain.class);

  public static void main(String[] args) {

    File input = new File("input");
    File output = new File("output");

    try {

      if (!input.exists()) input.mkdir();
      if (!output.exists()) output.mkdir();

      WatchService watcher = FileSystems.getDefault().newWatchService();
      input.toPath().register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

      while (true) {

        WatchKey key = watcher.take();

        for (WatchEvent<?> event : key.pollEvents()) {

          WatchEvent.Kind<?> kind = event.kind();

          @SuppressWarnings("unchecked")
          WatchEvent<Path> ev = (WatchEvent<Path>) event;

          log.info("{}: {}", kind.name(), ev.context());

          FileUtils.copyFile(
                  new File(input.getPath() + File.separator + ev.context()), new File(output.getAbsolutePath()
                          + File.separator + String.valueOf(System.currentTimeMillis() + "-" + ev.context())));
        }

        if (!key.reset()) break;
      }
    } catch (Exception e) {
      log.error("{}", e);
    }
  }

}
