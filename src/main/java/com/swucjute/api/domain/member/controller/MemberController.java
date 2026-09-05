package com.swucjute.api.domain.member.controller;

import com.swucjute.api.domain.member.dto.request.MemberProfileRegisterRequest;
import com.swucjute.api.domain.member.dto.request.MemberUpdateRequest;
import com.swucjute.api.domain.member.service.MemberService;
import com.swucjute.api.global.common.ApiPaths;
import com.swucjute.api.global.common.ApiResponse;
import com.swucjute.api.global.security.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 API (본인)")
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
      @AuthenticationPrincipal MemberPrincipal principal,
      @Valid @RequestBody MemberProfileRegisterRequest request) {
    return ApiResponse.success(memberService.registerProfile(principal.memberId(), request));
  }

  @Operation(summary = "내 정보 전체 조회")
  @GetMapping("/me")
  public ApiResponse<Object> getMyProfile(@AuthenticationPrincipal MemberPrincipal principal) {
    return ApiResponse.success(memberService.getMyProfile(principal.memberId()));
  }

  @Operation(summary = "내 정보 요약 조회")
  @GetMapping("/me/summary")
  public ApiResponse<Object> getMySummary(@AuthenticationPrincipal MemberPrincipal principal) {
    return ApiResponse.success(memberService.getMySummary(principal.memberId()));
  }

  @Operation(summary = "내 정보 수정")
  @PutMapping("/me")
  public ApiResponse<Object> updateMyProfile(
      @AuthenticationPrincipal MemberPrincipal principal,
      @Valid @RequestBody MemberUpdateRequest request) {
    return ApiResponse.success(memberService.updateMyProfile(principal.memberId(), request));
  }

  @Operation(summary = "회원 탈퇴")
  @DeleteMapping("/me")
  public ApiResponse<Object> withdrawMe(@AuthenticationPrincipal MemberPrincipal principal) {
    return ApiResponse.success(memberService.withdrawMe(principal.memberId()));
  }
}
