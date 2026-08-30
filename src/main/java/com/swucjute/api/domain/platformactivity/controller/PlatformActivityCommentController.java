package com.swucjute.api.domain.platformactivity.controller;

import com.swucjute.api.domain.platformactivity.dto.request.PlatformActivityCommentRequest;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Platform Activity Comment", description = "플랫폼 활동기록 댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.PLATFORM_ACTIVITY_COMMENTS)
public class PlatformActivityCommentController {

  private final PlatformActivityService platformActivityService;

  @Operation(summary = "댓글 수정")
  @PatchMapping("/{commentId}")
  public ApiResponse<Object> updateComment(
      @PathVariable Long commentId,
      @AuthenticationPrincipal MemberPrincipal principal,
      @Valid @RequestBody PlatformActivityCommentRequest request) {
    return ApiResponse.success(
        platformActivityService.updateComment(commentId, principal.memberId(), request));
  }

  @Operation(summary = "댓글 삭제")
  @DeleteMapping("/{commentId}")
  public ApiResponse<Object> deleteComment(
      @PathVariable Long commentId, @AuthenticationPrincipal MemberPrincipal principal) {
    return ApiResponse.success(
        platformActivityService.deleteComment(commentId, principal.memberId()));
  }
}
