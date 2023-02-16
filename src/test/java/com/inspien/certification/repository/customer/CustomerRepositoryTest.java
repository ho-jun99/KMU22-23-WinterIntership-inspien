package com.inspien.certification.repository.customer;

import com.inspien.certification.domain.certification.entity.AssociationCustomer;
import com.inspien.certification.domain.certification.entity.Certification;
import com.inspien.certification.domain.customer.entity.Customer;
import com.inspien.certification.domain.manager.entity.Manager;
import com.inspien.certification.repository.certification.AssociationCustomerRepository;
import com.inspien.certification.repository.certification.CertificationRepository;
import com.inspien.certification.repository.manager.ManagerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class CustomerRepositoryTest {
}
