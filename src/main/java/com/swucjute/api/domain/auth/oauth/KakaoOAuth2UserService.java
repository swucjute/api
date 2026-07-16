package com.swucjute.api.domain.auth.oauth;

import com.swucjute.api.domain.member.entity.AuthProvider;
import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.repository.MemberRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 카카오 사용자 정보를 조회한 뒤, 기존 회원을 찾거나 없으면 인증 정보만 가진 회원(USER/PENDING)을 생성한다. 실명/프로필/교적 등록은 별도 파트에서 처리한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

  private static final String MEMBER_ID_ATTRIBUTE = "memberId";
  private static final String KAKAO_ID_ATTRIBUTE = "id";
  private static final String KAKAO_ACCOUNT_ATTRIBUTE = "kakao_account";
  private static final String EMAIL_ATTRIBUTE = "email";

  private final MemberRepository memberRepository;

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    if (!AuthProvider.KAKAO.name().equalsIgnoreCase(registrationId)) {
      throw new OAuth2AuthenticationException(
          new OAuth2Error("unsupported_provider"), "지원하지 않는 소셜 로그인 제공자입니다: " + registrationId);
    }

    Object kakaoId = oAuth2User.getAttributes().get(KAKAO_ID_ATTRIBUTE);
    if (kakaoId == null) {
      throw new OAuth2AuthenticationException(
          new OAuth2Error("invalid_user_info"), "카카오 사용자 ID를 확인할 수 없습니다");
    }
    String providerUserId = String.valueOf(kakaoId);
    String email = extractEmail(oAuth2User);

    Member member =
        memberRepository
            .findByProviderAndProviderUserId(AuthProvider.KAKAO, providerUserId)
            .orElseGet(
                () -> {
                  log.info("카카오 신규 회원 생성: providerUserId={}", providerUserId);
                  // 카카오는 이메일만 동의 -> 인증 정보와 이메일만 저장한다. 실명/프로필/교적은 별도 파트.
                  return memberRepository.save(Member.ofKakao(providerUserId, email));
                });

    Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
    attributes.put(MEMBER_ID_ATTRIBUTE, member.getId());

    return new DefaultOAuth2User(
        List.of(new SimpleGrantedAuthority("ROLE_" + member.getMemberRole().name())),
        attributes,
        KAKAO_ID_ATTRIBUTE);
  }

  /** 카카오 응답의 kakao_account.email 을 추출한다. 미동의/미제공 시 null. */
  @SuppressWarnings("unchecked")
  private String extractEmail(OAuth2User oAuth2User) {
    Object account = oAuth2User.getAttributes().get(KAKAO_ACCOUNT_ATTRIBUTE);
    if (!(account instanceof Map)) {
      return null;
    }
    Object email = ((Map<String, Object>) account).get(EMAIL_ATTRIBUTE);
    return email == null ? null : String.valueOf(email);
  }
}
