package com.inspien.certification.domain.manager.dto;

import lombok.Data;

@Data
public class ManagerRegisterRequestDto {

    private String name;
    private String email;
    private String phone;

    private String customerId;


}
