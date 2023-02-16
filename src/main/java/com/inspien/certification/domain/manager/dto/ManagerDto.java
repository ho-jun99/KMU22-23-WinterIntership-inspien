package com.inspien.certification.domain.manager.dto;

import com.inspien.certification.domain.customer.dto.CustomerDto;
import com.inspien.certification.domain.customer.entity.Customer;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Builder
public class ManagerDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
}
