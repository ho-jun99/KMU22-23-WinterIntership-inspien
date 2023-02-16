package com.inspien.certification.service.email;

import com.inspien.certification.domain.email.dto.EmailDto;
import com.inspien.certification.domain.manager.dto.ManagerDto;

import javax.mail.MessagingException;

public interface EmailService {
  void sendSimpleMail(EmailDto emailDto);
  void sendTemplateMail(ManagerDto managerDto) throws MessagingException;
}
