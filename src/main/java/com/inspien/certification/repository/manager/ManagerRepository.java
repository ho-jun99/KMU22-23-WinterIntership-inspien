package com.inspien.certification.repository.manager;

import com.inspien.certification.domain.manager.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManagerRepository extends JpaRepository<Manager,Long> {
    // 이메일 중복 체크
    boolean existsByEmail(String email);

    //이름, 이메일, 전화번호로 조회
    List<Manager> findAllByNameOrEmailOrPhone(String name,String email, String phone);
}
