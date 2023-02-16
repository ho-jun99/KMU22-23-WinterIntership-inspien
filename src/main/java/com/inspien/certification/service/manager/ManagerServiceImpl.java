package com.inspien.certification.service.manager;

import com.inspien.certification.domain.customer.entity.Customer;
import com.inspien.certification.domain.manager.dto.ManagerDto;
import com.inspien.certification.domain.manager.dto.ManagerRegisterRequestDto;
import com.inspien.certification.domain.manager.dto.ManagerUpdateDto;
import com.inspien.certification.domain.manager.entity.Manager;
import com.inspien.certification.repository.customer.CustomerRepository;
import com.inspien.certification.repository.manager.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService{

    private final ManagerRepository managerRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public ManagerDto createManager(ManagerRegisterRequestDto requestDto) {
        Customer customer = customerRepository.findById(requestDto.getCustomerId()).orElseThrow(IllegalAccessError::new);
        Manager m = Manager.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .phone(requestDto.getPhone())
                .build();
        m.addCustomer(customer);
        return managerRepository.save(m).toDto();
    }

    @Override
    public Long deleteManager(Long id) {
        managerRepository.deleteById(id);
        return id;
    }

    @Override
    public List<ManagerDto> searchAllManager() {
        return managerRepository.findAll().stream()
                .map(manager -> manager.toDto())
                .collect(Collectors.toList());
    }

    @Override
    public List<ManagerDto> searchManager(String keyword) {
        return managerRepository.findAllByNameOrEmailOrPhone(keyword,keyword,keyword).stream()
                .map(manager -> manager.toDto())
                .collect(Collectors.toList());
    }


    @Override
    public Boolean checkEmail(String email) {
        //중복되지 않으면 true 리턴
        return !managerRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public Long managerUpdate(Long id, ManagerUpdateDto managerUpdateDto) {
        Manager manager = managerRepository.findById(id).get();
        manager.setName(managerUpdateDto.getName());
        manager.setEmail(managerUpdateDto.getEmail());
        manager.setPhone(managerUpdateDto.getPhone());

        Manager updatedManager = managerRepository.save(manager);

        return updatedManager.getId();
    }

    @Override
    public ManagerDto findMangerById(Long id) {
        Manager manager = managerRepository.findById(id).get();
        return manager.toDto();
    }
}
