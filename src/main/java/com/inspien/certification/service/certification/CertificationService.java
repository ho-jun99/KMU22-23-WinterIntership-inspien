package com.inspien.certification.service.certification;

import com.inspien.certification.domain.certification.dto.CertificationCreateRequestDto;
import com.inspien.certification.domain.certification.dto.CertificationDto;
import com.inspien.certification.domain.certification.dto.CertificationPageDto;
import com.inspien.certification.domain.certification.dto.ResourceDto;
import com.inspien.certification.domain.manager.dto.ManagerDto;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.List;

public interface CertificationService {

  CertificationPageDto searchAll(Pageable pageable); // 모든 인증서 조회
  CertificationPageDto searchKeyword(String keyword, Pageable pageable); //별칭, 시리얼, 발급자ID
  Long deleteCertification(Long id); // 인증서 삭제
  CertificationDto detail(Long id); // 인증서 세부조회
  ResourceDto downloadFile(Long id) throws MalformedURLException; // 파일 다운로드
  Long createCertification(CertificationCreateRequestDto requestDto) throws IOException, CertificateException; // 인증서 등록
  List<ManagerDto> findByDateUntilDue(Long dateTime);
  Long update(Long certificationId, CertificationCreateRequestDto certificationCreateRequestDto) throws IOException, CertificateException;
}
