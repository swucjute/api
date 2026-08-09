package com.swucjute.api.domain.auth.oauth;

import com.swucjute.api.global.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/** 카카오 인증 실패 시 프론트 로그인 페이지로 에러와 함께 리다이렉트한다. */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

  private final AppProperties appProperties;

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {

    log.warn("카카오 로그인 실패: {}", exception.getMessage());

    String redirectUrl =
        UriComponentsBuilder.fromUriString(appProperties.frontendBaseUrl())
            .path("/login")
            .queryParam("error", "kakao_login_failed")
            .build()
            .toUriString();

    response.sendRedirect(redirectUrl);
  }
}
