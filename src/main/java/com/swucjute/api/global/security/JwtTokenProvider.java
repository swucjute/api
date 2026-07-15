package com.swucjute.api.global.security;

import com.swucjute.api.domain.member.entity.MemberRole;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** JWT 액세스/리프레시 토큰 생성 및 검증. */
@Slf4j
@Component
public class JwtTokenProvider {

  private static final String ROLE_CLAIM = "role";

  private final SecretKey key;
  private final long accessTokenValiditySeconds;
  private final long refreshTokenValiditySeconds;

  public JwtTokenProvider(JwtProperties properties) {
    this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    this.accessTokenValiditySeconds = properties.accessTokenValiditySeconds();
    this.refreshTokenValiditySeconds = properties.refreshTokenValiditySeconds();
  }

  public String createAccessToken(Long memberId, MemberRole role) {
    return buildToken(memberId, role.name(), accessTokenValiditySeconds);
  }

  public String createRefreshToken(Long memberId) {
    return buildToken(memberId, null, refreshTokenValiditySeconds);
  }

  private String buildToken(Long memberId, String role, long validitySeconds) {
    Instant now = Instant.now();
    var builder =
        Jwts.builder()
            .subject(String.valueOf(memberId))
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(validitySeconds)));
    if (role != null) {
      builder.claim(ROLE_CLAIM, role);
    }
    return builder.signWith(key).compact();
  }

  /** 필터용: 유효하면 true, 아니면 false (예외를 던지지 않는다). */
  public boolean validateToken(String token) {
    try {
      parse(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.debug("유효하지 않은 JWT: {}", e.getMessage());
      return false;
    }
  }

  /** 서비스용: 유효하지 않으면 상황에 맞는 CustomException을 던진다. */
  public void validateOrThrow(String token) {
    try {
      parse(token);
    } catch (ExpiredJwtException e) {
      throw new CustomException(ErrorCode.EXPIRED_TOKEN);
    } catch (JwtException | IllegalArgumentException e) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
  }

  public Long getMemberId(String token) {
    return Long.valueOf(parse(token).getSubject());
  }

  public MemberRole getRole(String token) {
    String role = parse(token).get(ROLE_CLAIM, String.class);
    return role == null ? null : MemberRole.valueOf(role);
  }

  public long getAccessTokenValiditySeconds() {
    return accessTokenValiditySeconds;
  }

  public long getRefreshTokenValiditySeconds() {
    return refreshTokenValiditySeconds;
  }

  private Claims parse(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }
}
