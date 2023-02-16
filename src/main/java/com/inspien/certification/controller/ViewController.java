package com.inspien.certification.controller;

import com.inspien.certification.domain.certification.dto.CertificationCreateRequestDto;
import com.inspien.certification.domain.certification.dto.CertificationDto;
import com.inspien.certification.domain.certification.dto.CertificationPageDto;
import com.inspien.certification.domain.certification.dto.CertificationUpdateRequestDto;
import com.inspien.certification.domain.customer.dto.CustomerDto;
import com.inspien.certification.domain.customer.dto.CustomerPageDto;
import com.inspien.certification.domain.customer.dto.CustomerRequestDto;
import com.inspien.certification.domain.customer.dto.CustomerUpdateDto;
import com.inspien.certification.domain.manager.dto.ManagerDto;
import com.inspien.certification.domain.manager.dto.ManagerRegisterRequestDto;
import com.inspien.certification.domain.manager.dto.ManagerUpdateDto;
import com.inspien.certification.repository.certification.CertificationRepository;
import com.inspien.certification.service.certification.CertificationService;
import com.inspien.certification.service.customer.CustomerService;
import com.inspien.certification.service.manager.ManagerService;
import com.inspien.certification.service.scheduled.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view")
@Slf4j
public class ViewController {
  private final CertificationService certificationService;
  private final CustomerService customerService;
  private final ManagerService managerService;
  private final SchedulerService schedulerService;

  // <-- 테스트 ---> //

  /**
   * 페이징 처리 확인을 위해 CUSTOMER DUMMY를 생성하는 TEST API
   */
//  @GetMapping("/test/customers/dummy")
  @Transactional
  public String test() {
	for (int i = 50; i < 200; i++) {
	  customerService.createCustomer(
			  CustomerRequestDto.builder()
					  .id("TestCustomers" + i)
					  .name("TestCustomers" + i)
					  .memo("memo")
					  .build()
	  );
	}
	return "redirect:/view/customers";
  }

  // <-- CERTIFICATIONS 인증서 관련-- >
  @GetMapping("/certis")
  public String home(Model model,
					 @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort = "createdDate") Pageable pageable,
					 @RequestParam(required = false) String keyword) {
	if (keyword != null) {
	  CertificationPageDto certificationPageDto = certificationService.searchKeyword(keyword, pageable);
	  model.addAttribute("certifications", certificationPageDto);
	} else {
	  CertificationPageDto certificationPageDto = certificationService.searchAll(pageable);
	  model.addAttribute("certifications", certificationPageDto);
	}
	return "index";
  }


  @GetMapping("/certis/reg")
  public String certificationRegister(Model model) {
	List<CustomerDto> customerDtos = customerService.searchAllCustomer();
	CertificationCreateRequestDto createRequestDto = new CertificationCreateRequestDto();
	model.addAttribute("customerDtos", customerDtos);
	model.addAttribute("certificationCreateRequest", createRequestDto);

	return "certificationRegister";
  }

  @PostMapping("/certis/reg")
  public String createCertification(@ModelAttribute CertificationCreateRequestDto requestDto,
									RedirectAttributes redirectAttributes) throws CertificateException, IOException {
	log.info(requestDto.toString());
	Long certification = certificationService.createCertification(requestDto);
	redirectAttributes.addAttribute("id", certification);
	return "redirect:/view/certis/{id}";
  }


  @GetMapping("/certis/{id}")
  public String certificationDetail(Model model,
									@PathVariable Long id) {
	CertificationDto detail = certificationService.detail(id);
	model.addAttribute("certification", detail);
	log.info("연관고객사 == \n" + detail.getRelatedCustomers().toString());
	return "certificationDetail";
  }

  @GetMapping("/certis/delete/{id}")
  public String certificationDelte(@PathVariable Long id) {
	certificationService.deleteCertification(id);
	return "redirect:/view/certis";
  }

  @GetMapping("/certis/update/{certiId}")
  public String certificationUpdate(@PathVariable Long certiId,
									Model model){

	CertificationDto certificationDetail = certificationService.detail(certiId);
	List<CustomerDto> customerDtos = customerService.searchAllCustomer();
//	CertificationUpdateRequestDto updateRequestDto = new CertificationUpdateRequestDto();
	CertificationCreateRequestDto createRequestDto = new CertificationCreateRequestDto();


//	log.info("==========\n" + certificationDetail.toString());
//	log.info("relatedCustomerIdList" + certificationDetail.relatedCustomerId().toString());

	model.addAttribute("certificationDetail",certificationDetail);
	model.addAttribute("relatedCustomerIdList",certificationDetail.relatedCustomerId());
	model.addAttribute("customerDtos",customerDtos);
	model.addAttribute("certificationCreateRequest", createRequestDto);

	return "certificationUpdate";
  }

  @PostMapping ("/certis/update/{certificationId}")
  public String updateCertification(@ModelAttribute CertificationCreateRequestDto certificationCreateRequestDto,
									@PathVariable Long certificationId,
									RedirectAttributes redirectAttributes
									) throws CertificateException, IOException {
	log.info("here! " + certificationCreateRequestDto);

	certificationService.update(certificationId,certificationCreateRequestDto);

	redirectAttributes.addAttribute("certificationId",certificationId);
	return "redirect:/view/certis/{certificationId}";
  }



  // <-- CUSTOMERS 고객 관련-- >
  @GetMapping("/customers")
  public String customer(Model model,
						 @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
						 @RequestParam(required = false) String keyword) {
	if (keyword != null) {
	  CustomerPageDto customerPageDto = customerService.serachCustomer(keyword, pageable);
	  model.addAttribute("customsers", customerPageDto);
	} else {
//	  List<CustomerDto> customerDtos = customerService.searchAllCustomer();
	  CustomerPageDto customerPageDto = customerService.customersWithPage(pageable);
	  model.addAttribute("customsers", customerPageDto);
	}
	return "customer";
  }

  @PostMapping("/customers")
  public String customerJoin(CustomerRequestDto customerRequestDto) {
	customerService.createCustomer(customerRequestDto);
	log.info("customer join success, " + customerRequestDto.toString());
	return "redirect:/view/customers";
  }


  @GetMapping("/customers/{id}")
  public String customerDetail(Model model, @PathVariable String id) {
	CustomerDto detail = customerService.detail(id);
	model.addAttribute("customer", detail);
	return "customerDetail";
  }

  @GetMapping("/customers/delete/{customerId}")
  public String customerDelete(@PathVariable String customerId) {
	customerService.deleteCustomer(customerId);
	return "redirect:/view/customers";
  }

  @GetMapping("/customers-reg")
  public String emailReg() {
	return "customerRegister";
  }

  @GetMapping("/customers/update/{customerId}")
  public String customerUpdate(Model model,
							   @PathVariable String customerId){
	CustomerDto customer = customerService.detail(customerId);
	model.addAttribute("customer",customer);
	return "customerUpdate";
  }

  @PostMapping("customers/update/{customerId}")
  public String customerUpdate(@ModelAttribute CustomerUpdateDto customerUpdateDto,
							   @PathVariable String customerId,
							   RedirectAttributes redirectAttributes){
	customerService.customerUpdate(customerId,customerUpdateDto);
	redirectAttributes.addAttribute("customerId",customerId);
	return "redirect:/view/customers/{customerId}";
  }


  // <-- EMAIL 관련 --> //
  @GetMapping("/email-setting")
  public String emailSetting(Model model) {
	String cron = schedulerService.currentCron();
	Long expire = schedulerService.currentExpireDue();
	log.info("ViewController Cron : " + cron);
	model.addAttribute("cron", cron);
	model.addAttribute("expire", expire);
	return "emailSetting";
  }


  // <-- Manager 관련 --> //
  @GetMapping("/managers/reg/{customerId}")
  public String managerReg(
		  Model model,
		  @PathVariable String customerId
  ) {
	model.addAttribute("customerId", customerId);
	return "managerReg";
  }

  @GetMapping("/managers/delete/{id}")
  public String managerDelete(@PathVariable Long id,
							  @RequestParam String customerId,
							  RedirectAttributes redirectAttributes) {
	redirectAttributes.addAttribute("customerId", customerId);
	managerService.deleteManager(id);
	return "redirect:/view/customers/{customerId}";
  }

  @GetMapping("/managers/update/{managerId}")
  public String managerUpdate(@PathVariable Long managerId,
							  @RequestParam String customerId,
							  Model model) {
	ManagerDto findManager = managerService.findMangerById(managerId);
	model.addAttribute("manager", findManager);
	model.addAttribute("customerId",customerId);
	return "managerUpdate";
  }

  @PostMapping("/managers/update/{managerId}")
  public String managerUpdate(@PathVariable Long managerId,
							  @ModelAttribute ManagerUpdateDto managerUpdateDto,
							  @RequestParam String customerId,
							  RedirectAttributes redirectAttributes) {
	managerService.managerUpdate(managerId,managerUpdateDto);
	redirectAttributes.addAttribute("customerId",customerId);

	return "redirect:/view/customers/{customerId}";
  }

  @PostMapping("/managers/reg/{custoerId}")
  public String managerJoin(@PathVariable String custoerId,
							ManagerRegisterRequestDto requestDto,
							RedirectAttributes redirectAttributes) {
	requestDto.setCustomerId(custoerId);
	log.info("customerId : ", requestDto.getCustomerId());
	managerService.createManager(requestDto);
	redirectAttributes.addAttribute("customerId", requestDto.getCustomerId());
	return "redirect:/view/customers/{customerId}";
  }
}
