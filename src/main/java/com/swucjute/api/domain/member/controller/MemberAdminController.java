package com.swucjute.api.domain.member.controller;

import com.swucjute.api.domain.member.dto.request.MemberDepartmentUpdateRequest;
import com.swucjute.api.domain.member.dto.request.MemberStatusUpdateRequest;
import com.swucjute.api.domain.member.service.MemberAdminService;
import com.swucjute.api.global.common.ApiPaths;
import com.swucjute.api.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member Admin", description = "회원 관리자 API (ADMIN 전용)")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.MEMBERS)
@PreAuthorize("hasRole('ADMIN')")
public class MemberAdminController {

  private final MemberAdminService memberAdminService;

  @Operation(summary = "회원 목록 조회")
  @GetMapping
  public ApiResponse<Object> getMembers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String department,
      @RequestParam(required = false) String keyword) {
    return ApiResponse.success(
        memberAdminService.getMembers(page, size, status, department, keyword));
  }

  @Operation(summary = "회원 단건 조회")
  @GetMapping("/{memberId}")
  public ApiResponse<Object> getMember(@PathVariable Long memberId) {
    return ApiResponse.success(memberAdminService.getMember(memberId));
  }

  @Operation(summary = "회원 소속/직분 수정")
  @PatchMapping("/{memberId}/department")
  public ApiResponse<Object> updateDepartment(
      @PathVariable Long memberId, @Valid @RequestBody MemberDepartmentUpdateRequest request) {
    return ApiResponse.success(memberAdminService.updateDepartment(memberId, request));
  }

  @Operation(summary = "회원 상태 변경")
  @PatchMapping("/{memberId}/status")
  public ApiResponse<Object> updateStatus(
      @PathVariable Long memberId, @Valid @RequestBody MemberStatusUpdateRequest request) {
    return ApiResponse.success(memberAdminService.updateStatus(memberId, request));
  }
}
