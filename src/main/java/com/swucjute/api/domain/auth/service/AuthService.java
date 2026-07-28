package com.swucjute.api.domain.auth.service;

import com.swucjute.api.domain.auth.dto.request.TokenRefreshRequest;
import com.swucjute.api.domain.auth.dto.response.AuthTokenResponse;
import com.swucjute.api.domain.auth.entity.RefreshToken;
import com.swucjute.api.domain.auth.repository.RefreshTokenRepository;
import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.repository.MemberRepository;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import com.swucjute.api.global.security.JwtTokenProvider;
import com.swucjute.api.global.security.TokenHashUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final MemberRepository memberRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtTokenProvider jwtTokenProvider;

  /** 카카오 인증 성공 후 서비스 로그인: 액세스/리프레시 토큰을 발급하고 리프레시 토큰을 저장한다. */
  @Transactional
  public AuthTokenResponse login(Long memberId) {
    Member member =
        memberRepository
            .findById(memberId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    member.updateLastLogin(LocalDateTime.now());

    RefreshToken existing = refreshTokenRepository.findByMember(member).orElse(null);
    return issueTokens(member, existing);
  }

  /** 리프레시 토큰으로 액세스/리프레시 토큰을 재발급한다 (rotation). */
  @Transactional
  public AuthTokenResponse refresh(TokenRefreshRequest request) {
    String refreshToken = request.refreshToken();
    jwtTokenProvider.validateOrThrow(refreshToken);

    Long memberId = jwtTokenProvider.getMemberId(refreshToken);
    Member member =
        memberRepository
            .findById(memberId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    RefreshToken stored =
        refreshTokenRepository
            .findByMember(member)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

    String incomingHash = TokenHashUtil.sha256(refreshToken);
    if (stored.isRevoked()
        || stored.isExpired(LocalDateTime.now())
        || !stored.matches(incomingHash)) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    return issueTokens(member, stored);
  }

  /** 로그아웃: 현재 회원의 리프레시 토큰을 폐기한다. */
  @Transactional
  public Void logout(TokenRefreshRequest request) {
    Long memberId = currentMemberId();
    memberRepository
        .findById(memberId)
        .flatMap(refreshTokenRepository::findByMember)
        .ifPresent(token -> token.revoke(LocalDateTime.now()));
    return null;
  }

  private AuthTokenResponse issueTokens(Member member, RefreshToken existing) {
    String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getMemberRole());
    String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
    String tokenHash = TokenHashUtil.sha256(refreshToken);
    LocalDateTime expiresAt =
        LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenValiditySeconds());

    if (existing == null) {
      refreshTokenRepository.save(RefreshToken.issue(member, tokenHash, expiresAt));
    } else {
      existing.rotate(tokenHash, expiresAt);
    }

    return AuthTokenResponse.of(
        accessToken, refreshToken, jwtTokenProvider.getAccessTokenValiditySeconds());
  }

  private Long currentMemberId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
    try {
      return Long.valueOf(authentication.getName());
    } catch (NumberFormatException e) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
  }
}
