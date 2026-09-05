package com.swucjute.api.domain.platform.controller;

import com.swucjute.api.domain.platform.dto.PlatformApprovalStatusUpdateRequest;
import com.swucjute.api.domain.platform.dto.PlatformDetailResponse;
import com.swucjute.api.domain.platform.dto.PlatformListItemResponse;
import com.swucjute.api.domain.platform.dto.PlatformMemberResponse;
import com.swucjute.api.domain.platform.dto.PlatformMemberStatusUpdateRequest;
import com.swucjute.api.domain.platform.dto.PlatformOperatingStatusUpdateRequest;
import com.swucjute.api.domain.platform.dto.PlatformSaveRequest;
import com.swucjute.api.domain.platform.service.PlatformService;
import com.swucjute.api.global.common.ApiPaths;
import com.swucjute.api.global.common.ApiResponse;
import com.swucjute.api.global.common.PageResponse;
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

@Tag(name = "Platform", description = "플랫폼 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.PLATFORMS)
public class PlatformController {

  private final PlatformService platformService;

  @Operation(summary = "플랫폼 목록 조회")
  @GetMapping
  public ApiResponse<PageResponse<PlatformListItemResponse>> getPlatforms(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String approvalStatus,
      @RequestParam(required = false) Boolean recruiting,
      @RequestParam(required = false) Boolean operating,
      @RequestParam(required = false) String closedStatus,
      @RequestParam(required = false) String keyword) {
    return ApiResponse.success(
        platformService.getPlatforms(
            page, size, approvalStatus, recruiting, operating, closedStatus, keyword));
  }

  @Operation(summary = "플랫폼 상세 조회")
  @GetMapping("/{platformId}")
  public ApiResponse<PlatformDetailResponse> getPlatform(@PathVariable Long platformId) {
    return ApiResponse.success(platformService.getPlatform(platformId));
  }

  @Operation(summary = "플랫폼 생성/제안")
  @PostMapping
  public ApiResponse<PlatformDetailResponse> create(
      @Valid @RequestBody PlatformSaveRequest request) {
    return ApiResponse.success(platformService.create(request));
  }

  @Operation(summary = "플랫폼 수정")
  @PutMapping("/{platformId}")
  public ApiResponse<PlatformDetailResponse> update(
      @PathVariable Long platformId, @Valid @RequestBody PlatformSaveRequest request) {
    return ApiResponse.success(platformService.update(platformId, request));
  }

  @Operation(summary = "플랫폼 삭제")
  @DeleteMapping("/{platformId}")
  public ApiResponse<Void> delete(@PathVariable Long platformId) {
    return ApiResponse.success(platformService.delete(platformId));
  }

  @Operation(summary = "플랫폼 승인 상태 변경 (관리자 전용)")
  @PatchMapping("/{platformId}/approval-status")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<PlatformDetailResponse> updateApprovalStatus(
      @PathVariable Long platformId,
      @Valid @RequestBody PlatformApprovalStatusUpdateRequest request) {
    return ApiResponse.success(platformService.changeApprovalStatus(platformId, request));
  }

  @Operation(summary = "플랫폼 운영상태 변경 (모집/운영 토글, 종료·취소)")
  @PatchMapping("/{platformId}/operating-status")
  public ApiResponse<PlatformDetailResponse> updateOperatingStatus(
      @PathVariable Long platformId,
      @Valid @RequestBody PlatformOperatingStatusUpdateRequest request) {
    return ApiResponse.success(platformService.changeOperatingStatus(platformId, request));
  }

  @Operation(summary = "플랫폼 가입 신청")
  @PostMapping("/{platformId}/members")
  public ApiResponse<PlatformMemberResponse> join(@PathVariable Long platformId) {
    return ApiResponse.success(platformService.join(platformId));
  }

  @Operation(summary = "내 플랫폼 멤버십 상태 조회")
  @GetMapping("/{platformId}/members/me")
  public ApiResponse<PlatformMemberResponse> getMyMembership(@PathVariable Long platformId) {
    return ApiResponse.success(platformService.getMyMembership(platformId));
  }

  @Operation(summary = "플랫폼 멤버 목록 조회")
  @GetMapping("/{platformId}/members")
  public ApiResponse<PageResponse<PlatformMemberResponse>> getMembers(
      @PathVariable Long platformId,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ApiResponse.success(platformService.getMembers(platformId, status, page, size));
  }

  @Operation(summary = "플랫폼 멤버 상태 변경")
  @PatchMapping("/{platformId}/members/{memberId}/status")
  public ApiResponse<PlatformMemberResponse> updateMemberStatus(
      @PathVariable Long platformId,
      @PathVariable Long memberId,
      @Valid @RequestBody PlatformMemberStatusUpdateRequest request) {
    return ApiResponse.success(platformService.updateMemberStatus(platformId, memberId, request));
  }

  @Operation(summary = "플랫폼 탈퇴")
  @DeleteMapping("/{platformId}/members/me")
  public ApiResponse<Void> leave(@PathVariable Long platformId) {
    return ApiResponse.success(platformService.leave(platformId));
  }
}
