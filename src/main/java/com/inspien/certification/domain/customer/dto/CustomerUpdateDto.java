package com.inspien.certification.domain.customer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class CustomerUpdateDto {
  private String name;
  private String memo;
}
