package com.inspien.certification.repository.certification;

import com.inspien.certification.domain.certification.entity.Certification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CertificationRepository extends JpaRepository<Certification,Long> {
    List<Certification> findByAlias(String alias);
    List<Certification> findAllByOrderByExpireDateTimeDesc();
    List<Certification> findAllByAliasContainingOrSerialContainingOrPublisherId(String alias,String serial,String id);
    List<Certification> findAllByExpireDateTimeBetweenOrderByExpireDateTime(LocalDateTime start, LocalDateTime end);
    void deleteAllByPublisherId(String id);

    /**
     * 페이징 지원 레포지토리
     */
    Page<Certification> findAll(Pageable pageable);
    Page<Certification> findAllByAliasContainingOrSerialContainingOrPublisherId(String alias,String serial,String id,Pageable pageable);


}
