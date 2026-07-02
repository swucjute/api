package com.swucjute.api.domain.platform.controller;

import com.swucjute.api.domain.platform.dto.PlatformMemberStatusUpdateRequest;
import com.swucjute.api.domain.platform.dto.PlatformSaveRequest;
import com.swucjute.api.domain.platform.service.PlatformService;
import com.swucjute.api.global.common.ApiPaths;
import com.swucjute.api.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
  public ApiResponse<Object> getPlatforms(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String approvalStatus,
      @RequestParam(required = false) String operatingStatus,
      @RequestParam(required = false) String keyword) {
    return ApiResponse.success(
        platformService.getPlatforms(page, size, approvalStatus, operatingStatus, keyword));
  }

  @Operation(summary = "플랫폼 상세 조회")
  @GetMapping("/{platformId}")
  public ApiResponse<Object> getPlatform(@PathVariable Long platformId) {
    return ApiResponse.success(platformService.getPlatform(platformId));
  }

  @Operation(summary = "플랫폼 생성/제안")
  @PostMapping
  public ApiResponse<Object> create(@Valid @RequestBody PlatformSaveRequest request) {
    return ApiResponse.success(platformService.create(request));
  }

  @Operation(summary = "플랫폼 수정")
  @PutMapping("/{platformId}")
  public ApiResponse<Object> update(
      @PathVariable Long platformId, @Valid @RequestBody PlatformSaveRequest request) {
    return ApiResponse.success(platformService.update(platformId, request));
  }

  @Operation(summary = "플랫폼 삭제")
  @DeleteMapping("/{platformId}")
  public ApiResponse<Object> delete(@PathVariable Long platformId) {
    return ApiResponse.success(platformService.delete(platformId));
  }

  @Operation(summary = "플랫폼 가입 신청")
  @PostMapping("/{platformId}/members")
  public ApiResponse<Object> join(@PathVariable Long platformId) {
    return ApiResponse.success(platformService.join(platformId));
  }

  @Operation(summary = "플랫폼 멤버 목록 조회")
  @GetMapping("/{platformId}/members")
  public ApiResponse<Object> getMembers(
      @PathVariable Long platformId,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ApiResponse.success(platformService.getMembers(platformId, status, page, size));
  }

  @Operation(summary = "플랫폼 멤버 상태 변경")
  @PatchMapping("/{platformId}/members/{memberId}/status")
  public ApiResponse<Object> updateMemberStatus(
      @PathVariable Long platformId,
      @PathVariable Long memberId,
      @Valid @RequestBody PlatformMemberStatusUpdateRequest request) {
    return ApiResponse.success(platformService.updateMemberStatus(platformId, memberId, request));
  }

  @Operation(summary = "플랫폼 탈퇴")
  @DeleteMapping("/{platformId}/members/me")
  public ApiResponse<Object> leave(@PathVariable Long platformId) {
    return ApiResponse.success(platformService.leave(platformId));
  }
}
