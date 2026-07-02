package com.swucjute.api.domain.home.controller;

import com.swucjute.api.domain.home.service.HomeService;
import com.swucjute.api.global.common.ApiPaths;
import com.swucjute.api.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Home", description = "홈 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.HOME)
public class HomeController {

  private final HomeService homeService;

  @Operation(summary = "홈 화면 통합 조회")
  @GetMapping
  public ApiResponse<Object> getHome(
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate date,
      @RequestParam(defaultValue = "5") int scheduleLimit,
      @RequestParam(defaultValue = "4") int worshipLimit) {
    return ApiResponse.success(homeService.getHome(date, scheduleLimit, worshipLimit));
  }

  @Operation(summary = "홈 공지 목록 조회")
  @GetMapping("/notices")
  public ApiResponse<Object> getNotices(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "true") boolean activeOnly) {
    return ApiResponse.success(homeService.getNotices(page, size, activeOnly));
  }

  @Operation(summary = "홈 공지 상세 조회")
  @GetMapping("/notices/{noticeId}")
  public ApiResponse<Object> getNotice(@PathVariable Long noticeId) {
    return ApiResponse.success(homeService.getNotice(noticeId));
  }

  @Operation(summary = "일정 목록 조회")
  @GetMapping("/schedules")
  public ApiResponse<Object> getSchedules(
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate from,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate to,
      @RequestParam(required = false) String type,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ApiResponse.success(homeService.getSchedules(from, to, type, page, size));
  }

  @Operation(summary = "일정 상세 조회")
  @GetMapping("/schedules/{scheduleId}")
  public ApiResponse<Object> getSchedule(@PathVariable Long scheduleId) {
    return ApiResponse.success(homeService.getSchedule(scheduleId));
  }

  @Operation(summary = "배너 목록 조회")
  @GetMapping("/banners")
  public ApiResponse<Object> getBanners(@RequestParam(defaultValue = "true") boolean activeOnly) {
    return ApiResponse.success(homeService.getBanners(activeOnly));
  }
}
