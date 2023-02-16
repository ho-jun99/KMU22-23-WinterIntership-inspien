package com.inspien.certification.service.scheduled;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SchedulerRuntimeServiceTest {

  @Autowired
  private SchedulerRuntimeService service;

  @Test
  public void 스케줄러_런타임_변경() throws InterruptedException {

    // 2초 주기로 시작
	service.start();
    Thread.sleep(10000L);
    System.out.println("주기변경 2 -> 5초");
    // 5초 주기로 변경
    service.changeCron("0/5 * * * * *");
    Thread.sleep(10000L);
  }



}
