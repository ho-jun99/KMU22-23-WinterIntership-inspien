package com.inspien.certification.domain.certification.dto;

import lombok.*;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDto {

  private Resource resource;
  private String encodedUploadFileName;
  private String contentDisposition;
}
