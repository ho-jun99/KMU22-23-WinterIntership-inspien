package com.inspien.certification.domain.customer.dto;


import com.inspien.certification.domain.customer.entity.Customer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerRequestDto {
    private String id;
    private String name;
    private String memo;


    public Customer toEntity() {
        Customer customer = new Customer();
        customer.setId(this.id);
        customer.setName(this.name);
        customer.setMemo(this.memo);

        return customer;
    }
}
