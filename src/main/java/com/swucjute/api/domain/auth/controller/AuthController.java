package com.swucjute.api.domain.auth.controller;

import com.swucjute.api.domain.auth.dto.TokenRefreshRequest;
import com.swucjute.api.domain.auth.service.AuthService;
import com.swucjute.api.global.common.ApiPaths;
import com.swucjute.api.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

  @Operation(summary = "액세스 토큰 재발급")
  @PostMapping("/refresh")
  public ApiResponse<Object> refresh(@Valid @RequestBody TokenRefreshRequest request) {
    return ApiResponse.success(authService.refresh(request));
  }

  @Operation(summary = "로그아웃")
  @PostMapping("/logout")
  public ApiResponse<Object> logout(@RequestBody(required = false) TokenRefreshRequest request) {
    return ApiResponse.success(authService.logout(request));
  }
}
