package com.inspien.certification.service.manager;


import com.inspien.certification.domain.manager.dto.ManagerDto;
import com.inspien.certification.domain.manager.dto.ManagerRegisterRequestDto;
import com.inspien.certification.domain.manager.dto.ManagerUpdateDto;
import com.inspien.certification.domain.manager.entity.Manager;

import java.util.List;
import java.util.Map;

public interface ManagerService {
    // 매니저 등록
    ManagerDto createManager(ManagerRegisterRequestDto requestDto);

    // 매니저 삭제
    Long deleteManager(Long id);

    // 매니저 전체 조회
    List<ManagerDto> searchAllManager();

    // 키워드로 매니저 조회
    List<ManagerDto> searchManager(String keyword);

    // 매니저 이메일 중복 검사
    Boolean checkEmail(String email);

    //매니저 정보 업데이트 -> customerID반환하게 해주자
    Long managerUpdate(Long id, ManagerUpdateDto managerUpdateDto);

    //매니저 ID단건 조회
    ManagerDto findMangerById(Long id);
}
