package com.swucjute.api.domain.auth.oauth;

import com.swucjute.api.domain.auth.dto.response.AuthTokenResponse;
import com.swucjute.api.domain.auth.service.AuthService;
import com.swucjute.api.global.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/** 카카오 인증 성공 시 서비스 JWT(액세스/리프레시)를 발급해 프론트 콜백 페이지로 리다이렉트한다. */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private static final String MEMBER_ID_ATTRIBUTE = "memberId";

  private final AuthService authService;
  private final AppProperties appProperties;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {

    OAuth2User principal = (OAuth2User) authentication.getPrincipal();
    Long memberId = ((Number) principal.getAttributes().get(MEMBER_ID_ATTRIBUTE)).longValue();

    AuthTokenResponse tokens = authService.login(memberId);
    log.info("서비스 로그인 완료: memberId={}", memberId);

    String redirectUrl =
        UriComponentsBuilder.fromUriString(appProperties.frontendBaseUrl())
            .path("/auth/kakao/callback")
            .queryParam("accessToken", tokens.accessToken())
            .queryParam("refreshToken", tokens.refreshToken())
            .queryParam("expiresIn", tokens.expiresIn())
            .build()
            .toUriString();

    response.sendRedirect(redirectUrl);
  }
}
