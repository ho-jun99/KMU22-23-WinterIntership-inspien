package com.inspien.certification.domain.base.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PageDto {
  int totalPage;
  boolean hasNextPage;
  boolean hasPrevious;
  int curPage;
  int size;
  boolean isFirst;
  boolean isLast;
}
