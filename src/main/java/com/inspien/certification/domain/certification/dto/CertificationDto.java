package com.inspien.certification.domain.certification.dto;

import com.inspien.certification.domain.certification.entity.AssociationCustomer;
import com.inspien.certification.domain.customer.dto.CustomerDto;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class CertificationDto {

    private Long id;
    private String alias;
    private String serial;
    private String path;
    private String orginalFileName;
    private String memo;
    private CustomerDto publisher;
    private List<CustomerDto> relatedCustomers = new ArrayList<>();
    public void addRelatedCustomerDto(CustomerDto customerDto) {
        relatedCustomers.add(customerDto);
    }
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireDateTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    public List<String> relatedCustomerId(){
        List<String> customerIds = new ArrayList<>();
        relatedCustomers.forEach(customerDto -> customerIds.add(customerDto.getId()));
        return customerIds;
    }
}
