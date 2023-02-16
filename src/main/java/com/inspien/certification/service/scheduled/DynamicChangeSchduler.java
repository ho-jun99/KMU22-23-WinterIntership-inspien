package com.inspien.certification.service.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Slf4j
public class DynamicChangeSchduler {
  private ThreadPoolTaskScheduler scheduler;

  @Value("${email.sender.cron}")
  private String cron;

  public String getCron() {
	return cron;
  }

  private Runnable job;

  // 초기화 블럭
  {
	job= () -> {
	  log.info("초기화 수행 됨");
	};
  }

  public void startScheduler() {
	log.info("startScheduler Called");
	scheduler = new ThreadPoolTaskScheduler();
	scheduler.initialize();
	// scheduler setting
	scheduler.schedule(getRunnable(), getTrigger());
  }

  public void changeCronSet(String cron) {
	log.info("changeCronSet Called");
	this.cron = cron;
	startScheduler();
  }

  public void stopScheduler() {
	scheduler.shutdown();
	log.info("Stop Scheduler Success!");
  }

  private Runnable getRunnable() {
	if (this.job == null) {
	  return () -> {
		log.info("Job is null!!");
	  };
	}
	return this.job;
  }

  public void setJob(Runnable job) {
	this.job = job;
  }

  private Trigger getTrigger() {
	// cronSetting
	return new CronTrigger(cron);
  }

  @PostConstruct
  public void init() {
	log.info("Schduler init!");
	startScheduler();
  }

  @PreDestroy
  public void destroy() {
	log.info("Schduler destroy!");
	stopScheduler();
  }
}
