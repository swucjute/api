package com.swucjute.api.global.common;

import java.util.List;
import org.springframework.data.domain.Page;

/** 페이징 응답 공통 포맷. Spring Data {@link Page}의 직렬화 계약을 고정한다. */
public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last) {

  public static <T> PageResponse<T> of(Page<T> page) {
    return new PageResponse<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast());
  }
}
