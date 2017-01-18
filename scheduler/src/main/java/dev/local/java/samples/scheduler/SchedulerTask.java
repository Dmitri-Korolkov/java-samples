package dev.local.java.samples.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.TimerTask;

public class SchedulerTask extends TimerTask {

  private static final Logger log = LoggerFactory.getLogger(SchedulerTask.class);

  @Override
  public void run() {
    log.info("scheduler run: {}", new Date());
  }
}
