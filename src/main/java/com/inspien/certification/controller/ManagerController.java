package com.inspien.certification.controller;

import com.inspien.certification.domain.manager.dto.ManagerDto;
import com.inspien.certification.domain.manager.dto.ManagerRegisterRequestDto;
import com.inspien.certification.service.manager.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/managers")
@RequiredArgsConstructor
public class ManagerController {
    private final ManagerService managerService;

    @GetMapping
    public List<ManagerDto> searchAllManager() {
        return managerService.searchAllManager();
    }

    @GetMapping("/list")
    public List<ManagerDto> searchByKeywordManager(@RequestParam String keyword){
        return managerService.searchManager(keyword);
    }

    @PostMapping
    public ManagerDto createManager(@RequestBody ManagerRegisterRequestDto requestDto) {
        return managerService.createManager(requestDto);
    }

    @DeleteMapping
    public Long deleteManager(@RequestParam Long id) {
        return managerService.deleteManager(id);
    }

    @GetMapping("/check")
    public boolean checkEmail(@RequestParam String email){
        return managerService.checkEmail(email);
    }
}
