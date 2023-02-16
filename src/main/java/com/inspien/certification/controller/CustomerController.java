package com.inspien.certification.controller;

import com.inspien.certification.domain.customer.dto.CustomerDto;
import com.inspien.certification.domain.customer.dto.CustomerPageDto;
import com.inspien.certification.domain.customer.dto.CustomerRequestDto;
import com.inspien.certification.domain.customer.entity.Customer;
import com.inspien.certification.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<CustomerDto> searchAllCustomers() {
        return customerService.searchAllCustomer();
    }

    @GetMapping("/{id}")
    public CustomerDto detailCustomer(@PathVariable String id){
        return customerService.detail(id);
    }

    @GetMapping("/list")
    public CustomerPageDto searchKeyword(@RequestParam String keyword,
                                           @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC, sort= "d") Pageable pageable){
        return customerService.serachCustomer(keyword,pageable);
    }

    @PostMapping
    public CustomerDto createCustomer(@RequestBody CustomerRequestDto requestDto){
        return customerService.createCustomer(requestDto);
    }

    @DeleteMapping
    public String deleteCustomer(@RequestParam String id) {
        return customerService.deleteCustomer(id);
    }

    @GetMapping("/check")
    public boolean idValidCheck(@RequestParam String id){
        return customerService.idCheck(id);
    }

    @GetMapping("/page/customers")
    public CustomerPageDto pageAll(@PageableDefault(page = 0, size=10, sort="createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        CustomerPageDto customerPageDto = customerService.customersWithPage(pageable);
        return customerPageDto;
    }
}
