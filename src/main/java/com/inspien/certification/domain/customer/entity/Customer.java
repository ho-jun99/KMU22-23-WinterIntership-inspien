package com.inspien.certification.domain.customer.entity;

import com.inspien.certification.domain.BaseTimeEntity;
import com.inspien.certification.domain.certification.dto.AssociationCustomerDto;
import com.inspien.certification.domain.certification.dto.CertificationDto;
import com.inspien.certification.domain.certification.entity.AssociationCustomer;
import com.inspien.certification.domain.certification.entity.Certification;
import com.inspien.certification.domain.customer.dto.CustomerDto;
import com.inspien.certification.domain.manager.dto.ManagerDto;
import com.inspien.certification.domain.manager.entity.Manager;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString
public class Customer extends BaseTimeEntity {

    @Id
    private String id;
    private String name;
    private String memo;

    /**
     * 부모엔티티인 customer, 자식 엔티티 manager
     * 부모엔티티 삭제시 cascade옵션으로 영속성이 전이 돼 안전하게 삭제 가능
     * orphanRemoval을 통해서 연결이 끊기면 알아서 삭제 되도록 함
     * 두 옵션을 동시에 줌으로써, 부모에서 자식의 삭제 등록을 제어 할 수 있다( JPA book p.313 )
     */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Manager> managerList = new ArrayList<>();
    /*
        연관관계 편의 메소드 작성
     */
    public void addManager(Manager manager) {
        managerList.add(manager);
        manager.setCustomer(this);
    }

    public CustomerDto toDto() {
        return CustomerDto.builder()
                .id(id)
                .name(name)
                .memo(memo)
                .managerList(managerList.stream()
                        .map(manager -> manager.toDto())
                        .collect(Collectors.toList())
                )
                .createdDate(getCreatedDate())
                .build();
    }
}

/**
    --기록--
 0. 인증서와 출판한 관리사의 관계는 N:1의 관계여야함.
 1. certification.toDto() 사용시, NPE문제가 발생 할 수 있다. 이에대한 처리를 해줘야함

 */