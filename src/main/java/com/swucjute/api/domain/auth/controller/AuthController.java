package com.swucjute.api.domain.auth.controller;

import com.swucjute.api.domain.auth.dto.request.TokenRefreshRequest;
import com.swucjute.api.domain.auth.dto.response.AuthTokenResponse;
import com.swucjute.api.domain.auth.service.AuthService;
import com.swucjute.api.global.common.ApiPaths;
import com.swucjute.api.global.common.ApiResponse;
import com.swucjute.api.global.security.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.AUTH)
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "액세스 토큰 재발급", description = "리프레시 토큰으로 액세스/리프레시 토큰을 재발급한다 (rotation).")
  @PostMapping("/refresh")
  public ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
    return ApiResponse.success(authService.refresh(request));
  }

  @Operation(summary = "로그아웃", description = "현재 회원의 리프레시 토큰을 폐기한다.")
  @PostMapping("/logout")
  public ApiResponse<Void> logout(
      @AuthenticationPrincipal MemberPrincipal principal,
      @RequestBody(required = false) TokenRefreshRequest request) {
    authService.logout(principal.memberId(), request);
    return ApiResponse.success();
  }
}
