package com.inspien.certification.domain.certification.dto;

import com.inspien.certification.domain.customer.dto.CustomerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationUpdateRequestDto {
  private String alias; // 별칭
  private String serial; // 시리얼번호
  private String memo; //메모
  private MultipartFile multipartFile;
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime expireDateTime; // 만료일
  private CustomerDto publisherCustomer; // 발급고객
  private List<CustomerDto> relatedCustomer; // 연관고객
}
