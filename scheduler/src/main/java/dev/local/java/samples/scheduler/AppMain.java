package dev.local.java.samples.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AppMain {

  private static final Logger log = LoggerFactory.getLogger(AppMain.class);

  private static final CountDownLatch messageLatch = new CountDownLatch(1);

  private AppMain(){}

  public static void main(String[] args) {

    try {
      Timer time = new Timer();
      SchedulerTask st = new SchedulerTask();
      time.schedule(st, 0, 1000);//1 second

      if (messageLatch.await(10, TimeUnit.SECONDS)){
       log.info("await");
      }
    } catch (Exception e) {
      log.error("{}", e);
    }
  }

}
