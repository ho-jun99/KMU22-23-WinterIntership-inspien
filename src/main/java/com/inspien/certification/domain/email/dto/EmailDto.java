package com.inspien.certification.domain.email.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailDto {
  private String address;
  private String title;
  private String content;
  private String template = "index";

}
