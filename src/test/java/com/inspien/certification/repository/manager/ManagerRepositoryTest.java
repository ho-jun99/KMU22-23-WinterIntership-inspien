package com.inspien.certification.repository.manager;

import com.inspien.certification.domain.certification.entity.AssociationCustomer;
import com.inspien.certification.domain.certification.entity.Certification;
import com.inspien.certification.domain.customer.entity.Customer;
import com.inspien.certification.domain.manager.entity.Manager;
import com.inspien.certification.repository.customer.CustomerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class ManagerRepositoryTest {

    @Autowired ManagerRepository managerRepository;
    @Autowired CustomerRepository customerRepository;

    @AfterEach
    void after_each() {
        managerRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @Transactional
    void 매니저에서_등록고객조회_테스트() {
        Customer customer = new Customer();
        customer.setId("customer-Id");
        customer.setName("customer-name");
        customer.setMemo("memo");
        Customer saveCustomer = customerRepository.save(customer);

        Manager manager = new Manager();
        manager.setEmail("test@teset.com");
        manager.setPhone("010-1234-1234");
        manager.setName("hojun");
        manager.addCustomer(saveCustomer);
        Manager savedManager = managerRepository.save(manager);

        assertThat(savedManager.getCustomer().getId()).isEqualTo("customer-Id");
        assertThat(saveCustomer.getManagerList().size()).isEqualTo(1);

    }

    @Test
    void 매니저등록_테스트() {

        // given
        Customer customer = new Customer();
        customer.setId("kakao123");
        customer.setName("카카오");
        customer.setMemo("메모를 위한 공간입니다.");
        Customer savedCustomer = customerRepository.save(customer);

        Manager manager = new Manager();
        manager.setName("김호준");
        manager.setEmail("test@test.com");
        manager.setPhone("010-2851-6322");
        manager.setCustomer(savedCustomer);

        // when
        Manager savedManager = managerRepository.save(manager);

        // then
        assertThat(savedManager.getName()).isEqualTo(manager.getName());
        assertThat(savedManager.getCustomer().getId()).isEqualTo(savedCustomer.getId());

    }

    /*
        매니저만 지워져야 함
        다른 연관관계 테이블에는 변화가 없어야함
     */
    @Test
    void 매니저만_삭제_테스트() {
        Customer customer = new Customer();
        customer.setId("customer-Id");
        customer.setName("customer-name");
        customer.setMemo("memo");
        Customer saveCustomer = customerRepository.save(customer);

        Manager manager = new Manager();
        manager.setEmail("test@teset.com");
        manager.setPhone("010-1234-1234");
        manager.setName("hojun");
        manager.addCustomer(saveCustomer);
        Manager savedManager = managerRepository.save(manager);

        managerRepository.delete(savedManager);
        assertThat(managerRepository.findAll().size()).isEqualTo(0);
    }


}