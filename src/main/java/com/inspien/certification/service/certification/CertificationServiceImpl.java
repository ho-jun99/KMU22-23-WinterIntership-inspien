package com.inspien.certification.service.certification;

import com.inspien.certification.domain.certification.dto.CertificationCreateRequestDto;
import com.inspien.certification.domain.certification.dto.CertificationDto;
import com.inspien.certification.domain.certification.dto.CertificationPageDto;
import com.inspien.certification.domain.certification.dto.ResourceDto;
import com.inspien.certification.domain.certification.entity.AssociationCustomer;
import com.inspien.certification.domain.certification.entity.Certification;
import com.inspien.certification.domain.customer.dto.CustomerDto;
import com.inspien.certification.domain.customer.entity.Customer;
import com.inspien.certification.domain.manager.dto.ManagerDto;
import com.inspien.certification.repository.certification.AssociationCustomerRepository;
import com.inspien.certification.repository.certification.CertificationRepository;
import com.inspien.certification.repository.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

  private final CertificationRepository certificationRepository;
  private final CustomerRepository customerRepository;
  private final AssociationCustomerRepository associationCustomerRepository;
  private final FileStore fileStore;

  @Override
  @Transactional
  public CertificationPageDto searchAll(Pageable pageable) {
	Page<Certification> results = certificationRepository.findAll(pageable);

	CertificationPageDto certificationPageDto = CertificationPageDto.toDto(results);
	certificationPageDto.getCertificationDtos().forEach(certificationDto -> {
	  List<AssociationCustomer> allByCertificationIds = associationCustomerRepository.findAllByCertificationId(certificationDto.getId());
	  for (AssociationCustomer allByCertificationId : allByCertificationIds) {
		certificationDto.addRelatedCustomerDto(allByCertificationId.getCustomer().toDto());
	  }
	});
	return certificationPageDto;
  }

  @Override
  @Transactional
  public CertificationPageDto searchKeyword(String keyword, Pageable pageable) {
	Page<Certification> results = certificationRepository.findAllByAliasContainingOrSerialContainingOrPublisherId(keyword, keyword, keyword, pageable);
	return CertificationPageDto.toDto(results);
  }

  /**
   * 삭제시 로컬에 있는 파일을 삭제해야함.
   */
  @Override
  @Transactional
  public Long deleteCertification(Long id) {
	// 파일 fullpath을 알아낸 후
	String path = certificationRepository.findById(id).get().getPath();
	// filestore어에게 삭제를 위임
	fileStore.deleteFile(path);
	// 연관고객테이블에서도 정리해줌
	associationCustomerRepository.deleteAllByCertificationId(id);

	certificationRepository.deleteById(id);
	return id;
  }

  @Override
  public CertificationDto detail(Long id) {
	CertificationDto certificationDto = certificationRepository.findById(id).get().toDto();
	List<AssociationCustomer> associationCustomers = associationCustomerRepository.findAllByCertificationId(certificationDto.getId());
	for (AssociationCustomer associationCustomer : associationCustomers) {
	  certificationDto.addRelatedCustomerDto(associationCustomer.getCustomer().toDto());
	}
	return certificationDto;
  }

  @Override
  @Transactional
  public ResourceDto downloadFile(Long id) throws MalformedURLException {
	Certification certification = certificationRepository.findById(id).get();
	String path = certification.getPath();
	String orginalFileName = certification.getOrginalFileName();
	String fullPath = fileStore.getFullPath(path);

	UrlResource urlResource = new UrlResource("file:" + fullPath);
	String encodedUploadFileName = UriUtils.encode(orginalFileName,
			StandardCharsets.UTF_8);
	String contentDisposition = "attachment; filename=\"" +
			encodedUploadFileName + "\"";

	return ResourceDto.builder()
			.resource(urlResource)
			.encodedUploadFileName(encodedUploadFileName)
			.contentDisposition(contentDisposition)
			.build();
  }

  @Override
  @Transactional
  public Long createCertification(CertificationCreateRequestDto requestDto) throws IOException, CertificateException {
	/*
		파일 저장
	 */
	MultipartFile multipartFile = requestDto.getMultipartFile();
	String storeFileName = fileStore.storeFile(multipartFile);

	// == 파일시리얼 추출 == //
	String fullPath = fileStore.getFullPath(storeFileName);
	FileInputStream fileInputStream = new FileInputStream(fullPath);
	SerialExpire serialAndExpire = getSerialAndExpire(fileInputStream);
	requestDto.setSerial(serialAndExpire.getSerial());
	requestDto.setExpireDateTime(serialAndExpire.getExpire());
	// ================ //

	Customer publisherCustomer = customerRepository.findById(requestDto.getPublisherCustomerId()).get();

	Certification certification = Certification.builder()
			.publisher(publisherCustomer)
			.alias(requestDto.getAlias())
			.serial(requestDto.getSerial())
			.memo(requestDto.getMemo())
			.path(storeFileName)
			.orginalFileName(multipartFile.getOriginalFilename())
			.expireDateTime(requestDto.getExpireDateTime())
			.build();

	Certification savedCertification = certificationRepository.save(certification);

	List<String> relatedCustomerIds = requestDto.getRelatedCustomerId();
	for (String relatedCustomerId : relatedCustomerIds) {
	  Customer findCustomer = customerRepository.findById(relatedCustomerId).get();

	  associationCustomerRepository.save(
			  AssociationCustomer.builder()
					  .certification(certification)
					  .customer(findCustomer)
					  .build());
	}

	return savedCertification.getId();
  }

  private LocalDateTime convertStringToLocalDateTime(String date) {
	return null;
  }

  private SerialExpire getSerialAndExpire(InputStream inputStream) throws CertificateException {
	CertificateFactory fac = CertificateFactory.getInstance("X509");
	X509Certificate cert = (X509Certificate) fac.generateCertificate(inputStream);

	log.info("시리얼 : " + cert.getSerialNumber());
	log.info("만료일 : " + cert.getNotAfter());

	LocalDateTime localDateTime = new java.sql.Timestamp(cert.getNotAfter().getTime()) .toLocalDateTime();
//	localDateTime = LocalDateTime.parse(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	SerialExpire serialExpire = new SerialExpire();
	serialExpire.setSerial(cert.getSerialNumber().toString());
	serialExpire.setExpire(localDateTime);
	return serialExpire;
  }




  /**
   * 만료범위일을 인자로 넘겨주면, 그 범위 안에 드는 매니저 명단을 반환하는 함수
   * @return MangerDto
   */
  @Override
  @Transactional
  public List<ManagerDto> findByDateUntilDue(Long expireDueDay) {
	LocalDateTime now = LocalDateTime.now();
	LocalDateTime start = now.minusDays(expireDueDay);

	List<ManagerDto> beRecivedMangers = new ArrayList<ManagerDto>();

	log.info("now.minusDays : " + start);
	log.info("now.now : " + now);
	List<Certification> certifications = certificationRepository.findAllByExpireDateTimeBetweenOrderByExpireDateTime(start, now);

	// publisher(customer)에 있는 manager 추가하기
	certifications.forEach(certification -> {
	  Customer publisher = certification.getPublisher();
	  beRecivedMangers.addAll(publisher.getManagerList().stream()
			  .map(manager -> manager.toDto())
			  .collect(Collectors.toList()));
	});

	// 연관된 customer에 있는 매니저 추가하기
	certifications.forEach(certification -> {
	  Long id = certification.getId();
	  List<AssociationCustomer> associationCustomers = associationCustomerRepository.findAllByCertificationId(id);
	  associationCustomers.forEach(associationCustomer -> {

		beRecivedMangers.addAll(associationCustomer.getCustomer()
				.getManagerList().stream()
				.map(manager -> manager.toDto())
				.collect(Collectors.toList()));
	  });
	});
	// 중복된 이메일 제거
	List<ManagerDto> deduplicationMangerDtos = beRecivedMangers.stream()
			.distinct()
			.collect(Collectors.toList());


	return deduplicationMangerDtos;

	/*
		서비스레이어끼리의 참조는 순환참조를 유발 할 수 있다.
	 */
  }

  @Override
  @Transactional
  public Long update(Long certificationId, CertificationCreateRequestDto certificationCreateRequestDto) throws IOException, CertificateException {
	Certification certification = certificationRepository.findById(certificationId).get();

	// <== 공통 정보 업데이트 ==>
	certification.setMemo(certificationCreateRequestDto.getMemo());
	certification.setAlias(certificationCreateRequestDto.getAlias());
	certification.setPublisher(
			customerRepository.findById(certificationCreateRequestDto.getPublisherCustomerId()).get()
	);
	// <== 연관 고객사 목록 지우기 ==>
	associationCustomerRepository.deleteAllByCertificationId(certificationId);
	// <== 연관 고객사 등록 ==>
	certificationCreateRequestDto.getRelatedCustomerId().stream().forEach(customer -> {
	  associationCustomerRepository.save(
			  AssociationCustomer.builder()
					  .customer(customerRepository.findById(customer).get())
					  .certification(certification)
					  .build()
	  );
	});


	// 업데이트( 파일을 확인해야함 )
	if (certificationCreateRequestDto.getMultipartFile().isEmpty()) {
	  log.info("multipartFile is null"); // 파일 처리를 따로 할 필요 X

	}else {
	  // <== 기존 파일 삭제처리 해주기 ==>
	  fileStore.deleteFile(certification.getPath());
	  log.info("기존 파일 삭제 완료");

	  // <== 파일 업로드 및 갱신 == >
	  MultipartFile multipartFile = certificationCreateRequestDto.getMultipartFile();
	  String storeFileName = fileStore.storeFile(multipartFile);

	  // == 파일시리얼 추출 == //
	  String fullPath = fileStore.getFullPath(storeFileName);
	  FileInputStream fileInputStream = new FileInputStream(fullPath);
	  SerialExpire serialAndExpire = getSerialAndExpire(fileInputStream);

	  certification.setSerial(serialAndExpire.getSerial());
	  certification.setExpireDateTime(serialAndExpire.getExpire());
	  certification.setPath(storeFileName);
	  certification.setOrginalFileName(multipartFile.getOriginalFilename());
	  log.info("업데이트 파일 정보 업데이트 완료");

	}

	return certification.getId();
  }
}
