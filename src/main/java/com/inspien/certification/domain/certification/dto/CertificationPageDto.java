package com.inspien.certification.domain.certification.dto;

import com.inspien.certification.domain.base.dto.PageDto;
import com.inspien.certification.domain.certification.entity.Certification;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Getter @SuperBuilder
public class CertificationPageDto extends PageDto {
  List<CertificationDto> certificationDtos;

  public static CertificationPageDto toDto(Page<Certification> page) {
	return CertificationPageDto.builder()
			.certificationDtos(page.getContent().stream().map(certification -> certification.toDto()).collect(Collectors.toList()))
			.totalPage( page.getTotalPages() <= 1 ? 0 : page.getTotalPages()-1) // index가 0부터 시작하기 떄문에 숫자가 다르다
			.hasNextPage(page.hasNext())
			.hasPrevious(page.hasPrevious())
			.curPage(page.getNumber())
			.size (page.getSize())
			.isFirst(page.isFirst())
			.isLast(page.isLast())
			.build();
  }
}
