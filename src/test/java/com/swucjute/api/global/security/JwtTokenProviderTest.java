package com.swucjute.api.global.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.swucjute.api.domain.member.entity.MemberRole;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

  private static final String SECRET = "test-secret-key-that-is-at-least-32-bytes-long!!";

  private JwtTokenProvider provider(long accessTtl, long refreshTtl) {
    return new JwtTokenProvider(new JwtProperties(SECRET, accessTtl, refreshTtl));
  }

  @Test
  @DisplayName("액세스 토큰에서 회원 ID와 역할을 복원한다")
  void accessTokenRoundTrip() {
    JwtTokenProvider provider = provider(3600, 100000);

    String token = provider.createAccessToken(42L, MemberRole.USER);

    assertThat(provider.validateToken(token)).isTrue();
    assertThat(provider.getMemberId(token)).isEqualTo(42L);
    assertThat(provider.getRole(token)).isEqualTo(MemberRole.USER);
  }

  @Test
  @DisplayName("리프레시 토큰에는 역할 클레임이 없다")
  void refreshTokenHasNoRole() {
    JwtTokenProvider provider = provider(3600, 100000);

    String token = provider.createRefreshToken(7L);

    assertThat(provider.getMemberId(token)).isEqualTo(7L);
    assertThat(provider.getRole(token)).isNull();
  }

  @Test
  @DisplayName("만료된 토큰은 validateToken=false, validateOrThrow=EXPIRED_TOKEN")
  void expiredToken() {
    JwtTokenProvider provider = provider(-1, -1);

    String token = provider.createAccessToken(1L, MemberRole.USER);

    assertThat(provider.validateToken(token)).isFalse();
    assertThatThrownBy(() -> provider.validateOrThrow(token))
        .isInstanceOf(CustomException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.EXPIRED_TOKEN);
  }

  @Test
  @DisplayName("위조/손상된 토큰은 INVALID_TOKEN")
  void tamperedToken() {
    JwtTokenProvider provider = provider(3600, 100000);

    assertThat(provider.validateToken("garbage.token.value")).isFalse();
    assertThatThrownBy(() -> provider.validateOrThrow("garbage.token.value"))
        .isInstanceOf(CustomException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.INVALID_TOKEN);
  }

  @Test
  @DisplayName("다른 시크릿으로 서명한 토큰은 검증에 실패한다")
  void differentSecretFails() {
    JwtTokenProvider issuer = provider(3600, 100000);
    JwtTokenProvider verifier =
        new JwtTokenProvider(
            new JwtProperties("another-secret-key-also-32-bytes-long-xx!!", 3600, 100000));

    String token = issuer.createAccessToken(5L, MemberRole.ADMIN);

    assertThat(verifier.validateToken(token)).isFalse();
  }
}
