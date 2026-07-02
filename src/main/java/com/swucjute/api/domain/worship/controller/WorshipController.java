package com.swucjute.api.domain.worship.controller;

import com.swucjute.api.domain.worship.dto.WorshipSaveRequest;
import com.swucjute.api.domain.worship.service.WorshipService;
import com.swucjute.api.global.common.ApiPaths;
import com.swucjute.api.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Worship", description = "예배 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.WORSHIPS)
public class WorshipController {

  private final WorshipService worshipService;

  @Operation(summary = "이번 주/최신 예배 조회")
  @GetMapping("/current")
  public ApiResponse<Object> getCurrent() {
    return ApiResponse.success(worshipService.getCurrent());
  }

  @Operation(summary = "예배 목록/다시보기 조회")
  @GetMapping
  public ApiResponse<Object> getWorships(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate from,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to,
      @RequestParam(required = false) String keyword) {
    return ApiResponse.success(worshipService.getWorships(page, size, from, to, keyword));
  }

  @Operation(summary = "예배 상세 조회")
  @GetMapping("/{worshipId}")
  public ApiResponse<Object> getWorship(@PathVariable Long worshipId) {
    return ApiResponse.success(worshipService.getWorship(worshipId));
  }

  @Operation(summary = "예배 등록")
  @PostMapping
  public ApiResponse<Object> create(@Valid @RequestBody WorshipSaveRequest request) {
    return ApiResponse.success(worshipService.create(request));
  }

  @Operation(summary = "예배 수정")
  @PutMapping("/{worshipId}")
  public ApiResponse<Object> update(
      @PathVariable Long worshipId, @Valid @RequestBody WorshipSaveRequest request) {
    return ApiResponse.success(worshipService.update(worshipId, request));
  }

  @Operation(summary = "예배 삭제")
  @DeleteMapping("/{worshipId}")
  public ApiResponse<Object> delete(@PathVariable Long worshipId) {
    return ApiResponse.success(worshipService.delete(worshipId));
  }
}
