package com.swucjute.api.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swucjute.api.global.common.ApiResponse;
import com.swucjute.api.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/** 인증되지 않은 요청에 대해 통일된 401 JSON 응답을 반환한다. */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
    response.setStatus(errorCode.getStatus());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(
        response.getWriter(), ApiResponse.error(errorCode.getStatus(), errorCode.getMessage()));
  }
}
