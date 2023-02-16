package com.inspien.certification.domain.customer.dto;


import com.inspien.certification.domain.certification.dto.AssociationCustomerDto;
import com.inspien.certification.domain.certification.dto.CertificationDto;
import com.inspien.certification.domain.certification.entity.AssociationCustomer;
import com.inspien.certification.domain.certification.entity.Certification;
import com.inspien.certification.domain.manager.dto.ManagerDto;
import com.inspien.certification.domain.manager.entity.Manager;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CustomerDto {

    private String id;
    private String name;
    private String memo;
    private List<ManagerDto> managerList = new ArrayList<>();
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
}
