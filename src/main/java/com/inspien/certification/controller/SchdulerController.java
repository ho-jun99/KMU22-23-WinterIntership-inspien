package com.inspien.certification.controller;

import com.inspien.certification.service.scheduled.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/schduler")
@RequiredArgsConstructor
@Slf4j
public class SchdulerController {

  private final SchedulerService schedulerService;

  @PostMapping
  public String setSchdulerConfig(@RequestParam Long expireDue,
								  @RequestParam String cron) {

	schedulerService.changeConfig(expireDue, cron);
	log.info("==CRON 정상 변경완료== ");

	return "redirect:/view/email-setting";
  }
}
