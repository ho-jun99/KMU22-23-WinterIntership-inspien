package com.inspien.certification.domain.manager.entity;

import com.inspien.certification.domain.customer.entity.Customer;
import com.inspien.certification.domain.manager.dto.ManagerDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String name;
    @Column(nullable = false) private String email;
    @Column(nullable = false) private String phone;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    private Customer customer;

    public void addCustomer(Customer customer) {
        customer.addManager(this);
    }

    public ManagerDto toDto() {
        return ManagerDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .phone(phone)
                .build();
    }

}
