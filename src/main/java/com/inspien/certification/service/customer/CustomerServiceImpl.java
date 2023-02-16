package com.inspien.certification.service.customer;

import com.inspien.certification.domain.customer.dto.CustomerDto;
import com.inspien.certification.domain.customer.dto.CustomerPageDto;
import com.inspien.certification.domain.customer.dto.CustomerRequestDto;
import com.inspien.certification.domain.customer.dto.CustomerUpdateDto;
import com.inspien.certification.domain.customer.entity.Customer;
import com.inspien.certification.repository.certification.AssociationCustomerRepository;
import com.inspien.certification.repository.certification.CertificationRepository;
import com.inspien.certification.repository.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;
    private final AssociationCustomerRepository associationCustomerRepository;
    private final CertificationRepository certificationRepository;

    // 시간순 내림차순 전체 정렬 조회
    @Override
    public List<CustomerDto> searchAllCustomer() {
        List<CustomerDto> customerDtos = new ArrayList<>();
        customerRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate")).stream().forEach(customer -> customerDtos.add(customer.toDto()));
        return customerDtos;
    }

    @Override
    public CustomerPageDto serachCustomer(String keyword,Pageable pageable) {
        Page<Customer> results = customerRepository.findAllByIdContainingOrNameContainingOrderByCreatedDateDesc(keyword, keyword, pageable);
        return CustomerPageDto.toDto(results);
    }

    @Override
    public CustomerDto createCustomer(CustomerRequestDto requestDto) {
        return customerRepository.save(requestDto.toEntity()).toDto();
    }


    /**
     * DB무결성 유지 때문에, 그냥 커스터만 지울 수는 없다.
     * 연관된 것들 지워주고
     * 해당 커스터머가 출판한 인증서도 지워주자
     */
    @Override
    @Transactional
    public String deleteCustomer(String id) {
        associationCustomerRepository.deleteAllByCustomerId(id); // 연관된커스터머들을 지워준다.
        certificationRepository.deleteAllByPublisherId(id); // 해당 고객이 출판한 인증서를 지워준다.
        customerRepository.deleteById(id);
        return id;
    }

    @Override
    public boolean idCheck(String id) {
        // id를 사용할수 있으면 true반환, 그렇지 않으면 false반화
        return !customerRepository.existsById(id);
    }

    // 디테일 페이지 이동을 위해서
    @Override
    public CustomerDto detail(String id) {
        return customerRepository.findById(id).get().toDto();
    }

    @Override
    public CustomerPageDto customersWithPage(Pageable pageable) {
        /**
         *  int page -> 현재 페이지?
         *  int size -> 한 페이지에 보여질 아이템
         *  Sort sort -> 정렬 방법
         */
//        PageRequest pageRequest = PageRequest.of(0,10, Sort.by(Sort.Direction.DESC,"createdDate"));
        Sort sort = pageable.getSort();
        log.info("Sort toString :\n : " + sort.toString());
        Page<Customer> result = customerRepository.findAll(pageable);
        log.info("getTotalPage : " + result.getTotalPages());
        log.info("curPageNum : " + result.getNumber());
        log.info("isLast : " + result.isLast());
        log.info("isFirst : " + result.isFirst());
        return CustomerPageDto.toDto(result);
    }


    /**
     * save를 통한 업데이트시 모든 인자값을 넣어주지 않고, 바뀐 인자값만을 업데이트 하면 어떻게 될까?
     */
    @Override
    @Transactional
    public String customerUpdate(String customerId, CustomerUpdateDto customerUpdateDto) {
        Customer customer = customerRepository.findById(customerId).get();
        customer.setName(customerUpdateDto.getName());
        customer.setMemo(customerUpdateDto.getMemo());

        customerRepository.save(customer);
        return customerId;
    }
}
