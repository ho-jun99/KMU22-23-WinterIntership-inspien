package com.inspien.certification.domain.customer.dto;

import com.inspien.certification.domain.base.dto.PageDto;
import com.inspien.certification.domain.customer.entity.Customer;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.bytebuddy.implementation.bind.annotation.Super;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 롬복 Builder를 사용하기 때문에 상속관계에 있어서 문제가 발생
 * 해결 => SuperBuilder
 * https://velog.io/@mihyun/Lombok-Builder-%EC%83%81%EC%86%8D
 */
@Getter @SuperBuilder
public class CustomerPageDto extends PageDto {
	List<CustomerDto> customerDto;


	public static CustomerPageDto toDto(Page<Customer> page) {

	  return CustomerPageDto.builder()
			  .customerDto(page.getContent().stream().map(customer -> customer.toDto()).collect(Collectors.toList()))
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
