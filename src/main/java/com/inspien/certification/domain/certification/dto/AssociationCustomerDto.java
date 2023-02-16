package com.inspien.certification.domain.certification.dto;

import com.inspien.certification.domain.customer.dto.CustomerDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
public class AssociationCustomerDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    private Long id;

    private CertificationDto certification;

    private CustomerDto customer;

}
