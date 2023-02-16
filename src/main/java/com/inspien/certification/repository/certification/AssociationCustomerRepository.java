package com.inspien.certification.repository.certification;

import com.inspien.certification.domain.certification.entity.AssociationCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssociationCustomerRepository extends JpaRepository<AssociationCustomer,Long> {
  List<AssociationCustomer> findAllByCertificationId(Long id);
  void deleteAllByCustomerId(String id);
  void deleteAllByCertificationId(Long id);
}
