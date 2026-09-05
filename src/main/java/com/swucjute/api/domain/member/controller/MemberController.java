package com.swucjute.api.domain.member.controller;

import com.swucjute.api.domain.member.dto.request.MemberProfileRegisterRequest;
import com.swucjute.api.domain.member.dto.request.MemberUpdateRequest;
import com.swucjute.api.domain.member.dto.response.ChurchMemberResponse;
import com.swucjute.api.domain.member.dto.response.MemberMeResponse;
import com.swucjute.api.domain.member.dto.response.MemberProfileResponse;
import com.swucjute.api.domain.member.dto.response.MemberSummaryResponse;
import com.swucjute.api.domain.member.service.MemberService;
import com.swucjute.api.global.common.ApiPaths;
import com.swucjute.api.global.common.ApiResponse;
import com.swucjute.api.global.common.RegexPatterns;
import com.swucjute.api.global.security.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 API (본인)")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.MEMBERS)
@Validated
public class MemberController {

  private final MemberService memberService;

  @Operation(
      summary = "초기 프로필 등록",
      description =
          "카카오 로그인 후 실명/성별/생년월일/연락처/소속 등을 입력해 프로필을 생성한다. 소속이 청년(YOUTH)이면 교적부(ChurchMember) 매칭이 필수다.")
  @PostMapping("/me/profile")
  public ApiResponse<MemberMeResponse> registerProfile(
      @AuthenticationPrincipal MemberPrincipal principal,
      @Valid @RequestBody MemberProfileRegisterRequest request) {
    return ApiResponse.success(memberService.registerProfile(principal.memberId(), request));
  }

  @Operation(summary = "내 정보 전체 조회")
  @GetMapping("/me")
  public ApiResponse<MemberMeResponse> getMyProfile(
      @AuthenticationPrincipal MemberPrincipal principal) {
    return ApiResponse.success(memberService.getMyProfile(principal.memberId()));
  }

  @Operation(summary = "내 정보 요약 조회")
  @GetMapping("/me/summary")
  public ApiResponse<MemberSummaryResponse> getMySummary(
      @AuthenticationPrincipal MemberPrincipal principal) {
    return ApiResponse.success(memberService.getMySummary(principal.memberId()));
  }

  @Operation(summary = "내 정보 수정")
  @PutMapping("/me")
  public ApiResponse<MemberProfileResponse> updateMyProfile(
      @AuthenticationPrincipal MemberPrincipal principal,
      @Valid @RequestBody MemberUpdateRequest request) {
    return ApiResponse.success(memberService.updateMyProfile(principal.memberId(), request));
  }

  @Operation(summary = "회원 탈퇴")
  @DeleteMapping("/me")
  public ApiResponse<Void> withdrawMe(@AuthenticationPrincipal MemberPrincipal principal) {
    memberService.withdrawMe(principal.memberId());
    return ApiResponse.success();
  }

  @Operation(
      summary = "청년부 등록 조회",
      description =
          "이름/생년월일/전화번호로 교적부의 기본정보(id/이름/성별/생년월일/전화번호)를 조회한다. 교적부에 없으면 404(CHURCH_MEMBER_NOT_FOUND).")
  @GetMapping("/church-lookup")
  public ApiResponse<ChurchMemberResponse> lookupChurchMember(
      @RequestParam String name,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate birthDate,
      @Pattern(regexp = RegexPatterns.PHONE_NUMBER, message = RegexPatterns.PHONE_NUMBER_MESSAGE)
          @RequestParam
          String phoneNumber) {
    return ApiResponse.success(memberService.lookupChurchMember(name, birthDate, phoneNumber));
  }
}
