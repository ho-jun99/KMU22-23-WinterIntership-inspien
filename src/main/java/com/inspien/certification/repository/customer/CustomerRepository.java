package com.inspien.certification.repository.customer;

import com.inspien.certification.domain.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer,String> {

    // 해당 Id가 존재할경우 true, 아닐경우 false
    boolean existsById(String id);
    Page<Customer> findAllByIdContainingOrNameContainingOrderByCreatedDateDesc(String id, String name,Pageable pageable);

    // Page 처리 전체 조회
    Page<Customer> findAll(Pageable pageable);

}
