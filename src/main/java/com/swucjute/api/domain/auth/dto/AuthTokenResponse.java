package com.swucjute.api.domain.auth.dto;

/** 로그인/토큰 재발급 시 클라이언트에 전달하는 토큰 묶음. */
public record AuthTokenResponse(
    String accessToken, String refreshToken, String tokenType, long expiresIn) {

  public static AuthTokenResponse of(String accessToken, String refreshToken, long expiresIn) {
    return new AuthTokenResponse(accessToken, refreshToken, "Bearer", expiresIn);
  }
}
