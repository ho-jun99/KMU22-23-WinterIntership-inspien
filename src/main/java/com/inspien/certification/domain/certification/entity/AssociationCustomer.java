package com.inspien.certification.domain.certification.entity;

import com.inspien.certification.domain.certification.dto.AssociationCustomerDto;
import com.inspien.certification.domain.customer.entity.Customer;
import lombok.*;

import javax.persistence.*;


@Data
@Entity
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class AssociationCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "certification_id")
    @ToString.Exclude
    private Certification certification;

    @ManyToOne()
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    private Customer customer;

    public AssociationCustomerDto toDto() {
        return AssociationCustomerDto.builder()
                .id(id)
                .certification(certification.toDto())
                .customer(customer.toDto())
                .build();

    }
}
