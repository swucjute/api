package com.swucjute.api.domain.auth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swucjute.api.global.common.ApiResponse;
import com.swucjute.api.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/** 카카오 인증 실패 시 통일된 JSON 오류 응답을 반환한다. */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {

    log.warn("카카오 로그인 실패: {}", exception.getMessage());

    ErrorCode errorCode = ErrorCode.OAUTH_PROVIDER_ERROR;
    response.setStatus(errorCode.getStatus());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(
        response.getWriter(), ApiResponse.error(errorCode.getStatus(), errorCode.getMessage()));
  }
}
