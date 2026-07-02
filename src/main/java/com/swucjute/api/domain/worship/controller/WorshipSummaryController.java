package com.swucjute.api.domain.worship.controller;

import com.swucjute.api.domain.worship.dto.WorshipSummaryTriggerRequest;
import com.swucjute.api.domain.worship.dto.WorshipSummaryUpdateRequest;
import com.swucjute.api.domain.worship.service.WorshipSummaryService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Worship Summary", description = "예배 AI 요약 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.WORSHIPS)
public class WorshipSummaryController {

  private final WorshipSummaryService worshipSummaryService;

  @Operation(summary = "예배 요약 생성/재처리 트리거")
  @PostMapping("/{worshipId}/summary")
  public ApiResponse<Object> trigger(
      @PathVariable Long worshipId,
      @RequestBody(required = false) WorshipSummaryTriggerRequest request) {
    return ApiResponse.success(worshipSummaryService.trigger(worshipId, request));
  }

  @Operation(summary = "예배 요약 결과 조회")
  @GetMapping("/{worshipId}/summary")
  public ApiResponse<Object> getSummary(@PathVariable Long worshipId) {
    return ApiResponse.success(worshipSummaryService.getSummary(worshipId));
  }

  @Operation(summary = "예배 요약 수동 수정")
  @PatchMapping("/{worshipId}/summary")
  public ApiResponse<Object> updateSummary(
      @PathVariable Long worshipId, @Valid @RequestBody WorshipSummaryUpdateRequest request) {
    return ApiResponse.success(worshipSummaryService.updateSummary(worshipId, request));
  }

  @Operation(summary = "예배 요약 삭제")
  @DeleteMapping("/{worshipId}/summary")
  public ApiResponse<Object> deleteSummary(@PathVariable Long worshipId) {
    return ApiResponse.success(worshipSummaryService.deleteSummary(worshipId));
  }
}
