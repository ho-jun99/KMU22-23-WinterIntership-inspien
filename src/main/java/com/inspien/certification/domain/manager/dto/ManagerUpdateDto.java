package com.inspien.certification.domain.manager.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ManagerUpdateDto {
  private String name;
  private String email;
  private String phone;
}
