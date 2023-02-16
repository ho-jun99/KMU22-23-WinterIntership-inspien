package com.inspien.certification.controller;

import com.inspien.certification.domain.certification.dto.CertificationCreateRequestDto;
import com.inspien.certification.domain.certification.dto.CertificationDto;
import com.inspien.certification.domain.certification.dto.CertificationPageDto;
import com.inspien.certification.domain.certification.dto.ResourceDto;
import com.inspien.certification.service.certification.CertificationService;
import com.inspien.certification.service.certification.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.cert.CertificateException;
import java.util.List;


@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
public class CertificationController {
  private final CertificationService certificationService;

  @GetMapping
  public CertificationPageDto searchAll(Pageable pageable) {
	return certificationService.searchAll(pageable);
  }

  @GetMapping("/search")
  public CertificationPageDto searchKeyword(@RequestParam String keyword, Pageable pageable) {
	return certificationService.searchKeyword(keyword,pageable);
  }

  @GetMapping("/{id}")
  public CertificationDto detail(@PathVariable Long id) {
	return certificationService.detail(id);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Long createCertification(@ModelAttribute CertificationCreateRequestDto requestDto) throws IOException, CertificateException {
	return certificationService.createCertification(requestDto);
  }

  @GetMapping("/download/{id}")
  public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws MalformedURLException {
	ResourceDto resourceDto = certificationService.downloadFile(id);
	return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION,resourceDto.getContentDisposition())
			.body(resourceDto.getResource());
  }

@DeleteMapping("/{id}")
  public Long deleteCertification(@PathVariable Long id) {
	return certificationService.deleteCertification(id);
}
}
