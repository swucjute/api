package com.swucjute.api.domain.member.controller;

import com.swucjute.api.domain.member.dto.request.MemberDepartmentUpdateRequest;
import com.swucjute.api.domain.member.dto.request.MemberProfileRegisterRequest;
import com.swucjute.api.domain.member.dto.request.MemberStatusUpdateRequest;
import com.swucjute.api.domain.member.dto.request.MemberUpdateRequest;
import com.swucjute.api.domain.member.service.MemberService;
import com.swucjute.api.global.common.ApiPaths;
import com.swucjute.api.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.MEMBERS)
public class MemberController {

  private final MemberService memberService;

  @Operation(
      summary = "초기 프로필 등록",
      description = "카카오 로그인 후 실명/성별/생년월일/연락처/소속 등을 직접 입력해 프로필을 생성한다.")
  @PostMapping("/me/profile")
  public ApiResponse<Object> registerProfile(
      @Valid @RequestBody MemberProfileRegisterRequest request) {
    return ApiResponse.success(memberService.registerProfile(request));
  }

  @Operation(summary = "내 정보 전체 조회")
  @GetMapping("/me")
  public ApiResponse<Object> getMyProfile() {
    return ApiResponse.success(memberService.getMyProfile());
  }

  @Operation(summary = "내 정보 요약 조회")
  @GetMapping("/me/summary")
  public ApiResponse<Object> getMySummary() {
    return ApiResponse.success(memberService.getMySummary());
  }

  @Operation(summary = "내 정보 수정")
  @PutMapping("/me")
  public ApiResponse<Object> updateMyProfile(@Valid @RequestBody MemberUpdateRequest request) {
    return ApiResponse.success(memberService.updateMyProfile(request));
  }

  @Operation(summary = "회원 탈퇴")
  @DeleteMapping("/me")
  public ApiResponse<Object> withdrawMe() {
    return ApiResponse.success(memberService.withdrawMe());
  }

  @Operation(summary = "회원 목록 조회", description = "관리자 전용")
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ApiResponse<Object> getMembers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String department,
      @RequestParam(required = false) String keyword) {
    return ApiResponse.success(memberService.getMembers(page, size, status, department, keyword));
  }

  @Operation(summary = "회원 단건 조회", description = "관리자 전용")
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{memberId}")
  public ApiResponse<Object> getMember(@PathVariable Long memberId) {
    return ApiResponse.success(memberService.getMember(memberId));
  }

  @Operation(summary = "회원 소속/직분 수정", description = "관리자 전용")
  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{memberId}/department")
  public ApiResponse<Object> updateDepartment(
      @PathVariable Long memberId, @Valid @RequestBody MemberDepartmentUpdateRequest request) {
    return ApiResponse.success(memberService.updateDepartment(memberId, request));
  }

  @Operation(summary = "회원 상태 변경", description = "관리자 전용")
  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{memberId}/status")
  public ApiResponse<Object> updateStatus(
      @PathVariable Long memberId, @Valid @RequestBody MemberStatusUpdateRequest request) {
    return ApiResponse.success(memberService.updateStatus(memberId, request));
  }
}
