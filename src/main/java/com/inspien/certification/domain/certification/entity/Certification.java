package com.inspien.certification.domain.certification.entity;

import com.inspien.certification.domain.BaseTimeEntity;
import com.inspien.certification.domain.certification.dto.AssociationCustomerDto;
import com.inspien.certification.domain.certification.dto.CertificationDto;
import com.inspien.certification.domain.customer.entity.Customer;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Certification extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // xToONE은 모두 LAZY로
    @JoinColumn(name = "publisher_id")
    @ToString.Exclude
    private Customer publisher;
    private String alias;
    private String serial;
    private String path;
    private String memo;
    private String orginalFileName;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireDateTime;

    public CertificationDto toDto() {
        return CertificationDto.builder()
                .id(id)
                .publisher(publisher.toDto())
                .alias(alias)
                .serial(serial)
                .path(path)
                .orginalFileName(orginalFileName)
                .memo(memo)
                .relatedCustomers(new ArrayList<>())
                .createdDate(getCreatedDate())
                .expireDateTime(expireDateTime)
                .build();
    }
}
