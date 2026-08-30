package com.swucjute.api.domain.platformactivity.controller;

import com.swucjute.api.domain.platformactivity.dto.request.PlatformActivityCommentRequest;
import com.swucjute.api.domain.platformactivity.dto.request.PlatformActivityCreateRequest;
import com.swucjute.api.domain.platformactivity.dto.request.PlatformActivityUpdateRequest;
import com.swucjute.api.domain.platformactivity.service.PlatformActivityService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Platform Activity", description = "플랫폼 활동기록 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.PLATFORM_ACTIVITIES)
public class PlatformActivityController {

  private final PlatformActivityService platformActivityService;

  @Operation(summary = "활동 게시글 목록 조회")
  @GetMapping
  public ApiResponse<Object> getActivities(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ApiResponse.success(platformActivityService.getActivities(page, size));
  }

  @Operation(summary = "활동 게시글 상세 조회")
  @GetMapping("/{activityId}")
  public ApiResponse<Object> getActivity(@PathVariable Long activityId) {
    return ApiResponse.success(platformActivityService.getActivity(activityId));
  }

  @Operation(summary = "활동 게시글 작성")
  @PostMapping
  public ApiResponse<Object> create(
      @AuthenticationPrincipal MemberPrincipal principal,
      @Valid @RequestBody PlatformActivityCreateRequest request) {
    return ApiResponse.success(platformActivityService.create(principal.memberId(), request));
  }

  @Operation(summary = "활동 게시글 수정")
  @PatchMapping("/{activityId}")
  public ApiResponse<Object> update(
      @PathVariable Long activityId,
      @AuthenticationPrincipal MemberPrincipal principal,
      @Valid @RequestBody PlatformActivityUpdateRequest request) {
    return ApiResponse.success(
        platformActivityService.update(activityId, principal.memberId(), request));
  }

  @Operation(summary = "활동 게시글 삭제")
  @DeleteMapping("/{activityId}")
  public ApiResponse<Object> delete(
      @PathVariable Long activityId, @AuthenticationPrincipal MemberPrincipal principal) {
    return ApiResponse.success(platformActivityService.delete(activityId, principal.memberId()));
  }

  @Operation(summary = "좋아요 등록")
  @PostMapping("/{activityId}/likes")
  public ApiResponse<Object> addLike(
      @PathVariable Long activityId, @AuthenticationPrincipal MemberPrincipal principal) {
    return ApiResponse.success(platformActivityService.addLike(activityId, principal.memberId()));
  }

  @Operation(summary = "좋아요 취소")
  @DeleteMapping("/{activityId}/likes")
  public ApiResponse<Object> removeLike(
      @PathVariable Long activityId, @AuthenticationPrincipal MemberPrincipal principal) {
    return ApiResponse.success(
        platformActivityService.removeLike(activityId, principal.memberId()));
  }

  @Operation(summary = "좋아요 사용자 목록")
  @GetMapping("/{activityId}/likes")
  public ApiResponse<Object> getLikes(@PathVariable Long activityId) {
    return ApiResponse.success(platformActivityService.getLikes(activityId));
  }

  @Operation(summary = "댓글 작성")
  @PostMapping("/{activityId}/comments")
  public ApiResponse<Object> createComment(
      @PathVariable Long activityId,
      @AuthenticationPrincipal MemberPrincipal principal,
      @Valid @RequestBody PlatformActivityCommentRequest request) {
    return ApiResponse.success(
        platformActivityService.createComment(activityId, principal.memberId(), request));
  }
}
