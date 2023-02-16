package com.inspien.certification.service.customer;

import com.inspien.certification.domain.customer.dto.CustomerDto;
import com.inspien.certification.domain.customer.dto.CustomerPageDto;
import com.inspien.certification.domain.customer.dto.CustomerRequestDto;
import com.inspien.certification.domain.customer.dto.CustomerUpdateDto;
import com.inspien.certification.domain.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    // 모든 고객 조회
    List<CustomerDto> searchAllCustomer();

    // 고객 조회( 고객 ID or 고객명)
    CustomerPageDto serachCustomer(String keyword,Pageable pageable);

    // CUSTOMER 등록
    CustomerDto createCustomer(CustomerRequestDto requestDto);

    // CUSTOMER 삭제 (id명으로 삭제)
    String deleteCustomer(String id);

    // CUSTOMER ID 중복확인
    boolean idCheck(String id);

    // 디테일 정보
    CustomerDto detail(String id);

    // Page
    CustomerPageDto customersWithPage(Pageable pageable);

    // 업데이트
    String customerUpdate(String customerId,CustomerUpdateDto customerUpdateDto);


}
