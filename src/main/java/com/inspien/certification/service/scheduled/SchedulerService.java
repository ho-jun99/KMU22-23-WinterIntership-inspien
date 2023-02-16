package com.inspien.certification.service.scheduled;

import com.inspien.certification.domain.certification.dto.CertificationDto;
import com.inspien.certification.domain.manager.dto.ManagerDto;
import com.inspien.certification.service.certification.CertificationService;
import com.inspien.certification.service.email.EmailService;
import com.inspien.certification.service.manager.ManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {
  private final DynamicChangeSchduler dynamicChangeSchduler;
  private final CertificationService certificationService;
  private final EmailService emailService;
  @Value("${email.sender.due}")
  private Long expireDue;

  public Long currentExpireDue() {
	return expireDue;
  }

  @Transactional
  public void changeConfig(Long expireDue, String cron) {
	this.expireDue= expireDue;
	dynamicChangeSchduler.stopScheduler();
	log.info("new Cron >> " + cron);
	dynamicChangeSchduler.setJob(()->{

	  // 만료일 범위에 있는 매니저 불러오기
	  List<ManagerDto> beRecivedMangers = certificationService.findByDateUntilDue(expireDue);

	  // 매니저들에게 이메일 보내기
	  for (ManagerDto manger : beRecivedMangers) {
		try {
		  emailService.sendTemplateMail(manger);
		  log.info("이메일 송부 완료! : " + manger.getEmail().toString());
		} catch (MessagingException e) {

		  log.info("이메일 송부 실패! : " + manger.getEmail().toString());
		  throw new RuntimeException(e);
		}
	  }
	  log.info("매니저에게 모든 이메일 송부 완료");
	});

	dynamicChangeSchduler.changeCronSet(cron);
  }

  public String currentCron() {
	return dynamicChangeSchduler.getCron();
  }
}
