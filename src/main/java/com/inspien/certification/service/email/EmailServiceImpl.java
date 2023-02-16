package com.inspien.certification.service.email;

import com.inspien.certification.domain.email.dto.EmailDto;
import com.inspien.certification.domain.manager.dto.ManagerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{
  private final JavaMailSender javaMailSender;
  private final SpringTemplateEngine springTemplateEngine;
  @Value("${spring.mail.username}")
  String senderEmailAdderess;

  @Value("${email.sender.title}")
  String emailTitle;

  @Override
  public void sendSimpleMail(EmailDto emailDto) {
	SimpleMailMessage message = new SimpleMailMessage();
	message.setFrom(senderEmailAdderess);
	message.setTo(emailDto.getAddress());
	message.setSubject(emailDto.getTitle());
	message.setText(emailDto.getContent());
	javaMailSender.send(message);
  }

  @Override
  public void sendTemplateMail(ManagerDto managerDto) throws MessagingException {
	MimeMessage mimeMessage = javaMailSender.createMimeMessage();
	MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

	//메일 제목 설정
	helper.setSubject(emailTitle);

	//수신자 설정
	helper.setTo(managerDto.getEmail());

	//발신자 설정
	helper.setFrom(senderEmailAdderess);

	//메일 템플릿
	Context context = new Context(); // 타임리프 컨텍스트
	context.setVariable("managerName",managerDto.getName());


	String html = springTemplateEngine.process("mail",context);
	helper.setText(html,true);

	// 템플릿 이미지 cid로 삽입
	helper.addInline("image1", new ClassPathResource("static/images/image-1.png"));
	helper.addInline("image2", new ClassPathResource("static/images/image-2.png"));
	helper.addInline("image3", new ClassPathResource("static/images/image-3.png"));
	helper.addInline("image4", new ClassPathResource("static/images/image-4.png"));
	helper.addInline("image5", new ClassPathResource("static/images/image-5.png"));

	javaMailSender.send(mimeMessage);

  }
}
