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
   * ????????? ????????? ?????? ????????? ???????????????.
   */
  @Override
  @Transactional
  public Long deleteCertification(Long id) {
	// ?????? fullpath??? ????????? ???
	String path = certificationRepository.findById(id).get().getPath();
	// filestore????????? ????????? ??????
	fileStore.deleteFile(path);
	// ?????????????????????????????? ????????????
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
		?????? ??????
	 */
	MultipartFile multipartFile = requestDto.getMultipartFile();
	String storeFileName = fileStore.storeFile(multipartFile);

	// == ??????????????? ?????? == //
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

	log.info("????????? : " + cert.getSerialNumber());
	log.info("????????? : " + cert.getNotAfter());

	LocalDateTime localDateTime = new java.sql.Timestamp(cert.getNotAfter().getTime()) .toLocalDateTime();
//	localDateTime = LocalDateTime.parse(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	SerialExpire serialExpire = new SerialExpire();
	serialExpire.setSerial(cert.getSerialNumber().toString());
	serialExpire.setExpire(localDateTime);
	return serialExpire;
  }




  /**
   * ?????????????????? ????????? ????????????, ??? ?????? ?????? ?????? ????????? ????????? ???????????? ??????
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

	// publisher(customer)??? ?????? manager ????????????
	certifications.forEach(certification -> {
	  Customer publisher = certification.getPublisher();
	  beRecivedMangers.addAll(publisher.getManagerList().stream()
			  .map(manager -> manager.toDto())
			  .collect(Collectors.toList()));
	});

	// ????????? customer??? ?????? ????????? ????????????
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
	// ????????? ????????? ??????
	List<ManagerDto> deduplicationMangerDtos = beRecivedMangers.stream()
			.distinct()
			.collect(Collectors.toList());


	return deduplicationMangerDtos;

	/*
		??????????????????????????? ????????? ??????????????? ?????? ??? ??? ??????.
	 */
  }

  @Override
  @Transactional
  public Long update(Long certificationId, CertificationCreateRequestDto certificationCreateRequestDto) throws IOException, CertificateException {
	Certification certification = certificationRepository.findById(certificationId).get();

	// <== ?????? ?????? ???????????? ==>
	certification.setMemo(certificationCreateRequestDto.getMemo());
	certification.setAlias(certificationCreateRequestDto.getAlias());
	certification.setPublisher(
			customerRepository.findById(certificationCreateRequestDto.getPublisherCustomerId()).get()
	);
	// <== ?????? ????????? ?????? ????????? ==>
	associationCustomerRepository.deleteAllByCertificationId(certificationId);
	// <== ?????? ????????? ?????? ==>
	certificationCreateRequestDto.getRelatedCustomerId().stream().forEach(customer -> {
	  associationCustomerRepository.save(
			  AssociationCustomer.builder()
					  .customer(customerRepository.findById(customer).get())
					  .certification(certification)
					  .build()
	  );
	});


	// ????????????( ????????? ??????????????? )
	if (certificationCreateRequestDto.getMultipartFile().isEmpty()) {
	  log.info("multipartFile is null"); // ?????? ????????? ?????? ??? ?????? X

	}else {
	  // <== ?????? ?????? ???????????? ????????? ==>
	  fileStore.deleteFile(certification.getPath());
	  log.info("?????? ?????? ?????? ??????");

	  // <== ?????? ????????? ??? ?????? == >
	  MultipartFile multipartFile = certificationCreateRequestDto.getMultipartFile();
	  String storeFileName = fileStore.storeFile(multipartFile);

	  // == ??????????????? ?????? == //
	  String fullPath = fileStore.getFullPath(storeFileName);
	  FileInputStream fileInputStream = new FileInputStream(fullPath);
	  SerialExpire serialAndExpire = getSerialAndExpire(fileInputStream);

	  certification.setSerial(serialAndExpire.getSerial());
	  certification.setExpireDateTime(serialAndExpire.getExpire());
	  certification.setPath(storeFileName);
	  certification.setOrginalFileName(multipartFile.getOriginalFilename());
	  log.info("???????????? ?????? ?????? ???????????? ??????");

	}

	return certification.getId();
  }
}
