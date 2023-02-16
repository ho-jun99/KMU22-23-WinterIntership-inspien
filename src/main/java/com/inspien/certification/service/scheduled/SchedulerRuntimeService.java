package com.inspien.certification.service.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;

/*
	STUDY에 사용된 클래스
 */

public class SchedulerRuntimeService {
//  private final TaskScheduler scheduler;
  private String cron = "0/1 * * * * *";
  private ScheduledFuture<?> future;

  public String getCron() {
	return cron;
  }

  public void start() {

//	ScheduledFuture<?> future = this.scheduler.schedule(() -> {
//			  log.info("run at >>>>>" + "${certification.checking.due}");
//			},
//			new CronTrigger(cron));
//
//	this.future = future;
  }

  public void changeCron(String cron) {
	if (future != null) future.cancel(true);
	this.future = null;
	this.cron = cron;
	this.start();
  }
}
