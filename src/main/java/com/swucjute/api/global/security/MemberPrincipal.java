package com.swucjute.api.global.security;

import com.swucjute.api.domain.member.entity.MemberRole;
import org.springframework.security.core.AuthenticatedPrincipal;

/**
 * 인증된 회원 정보를 담는 principal. 컨트롤러에서 {@code @AuthenticationPrincipal MemberPrincipal principal}로 주입받아
 * {@code principal.memberId()}, {@code principal.role()}로 접근한다.
 *
 * <p>{@link AuthenticatedPrincipal#getName()}은 memberId 문자열을 반환하므로 {@code
 * Authentication.getName()}으로도 회원 ID를 얻을 수 있다.
 */
public record MemberPrincipal(Long memberId, MemberRole role) implements AuthenticatedPrincipal {

  @Override
  public String getName() {
    return String.valueOf(memberId);
  }
}
