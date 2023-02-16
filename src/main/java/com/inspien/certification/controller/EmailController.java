package com.inspien.certification.controller;

import com.inspien.certification.domain.email.dto.EmailDto;
import com.inspien.certification.domain.manager.dto.ManagerDto;
import com.inspien.certification.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
@Slf4j
public class EmailController {

}
