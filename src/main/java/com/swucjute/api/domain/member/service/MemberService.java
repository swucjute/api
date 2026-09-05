package com.swucjute.api.domain.member.service;

import com.swucjute.api.domain.auth.repository.RefreshTokenRepository;
import com.swucjute.api.domain.member.dto.request.MemberProfileRegisterRequest;
import com.swucjute.api.domain.member.dto.request.MemberUpdateRequest;
import com.swucjute.api.domain.member.dto.response.ChurchMemberResponse;
import com.swucjute.api.domain.member.dto.response.MemberMeResponse;
import com.swucjute.api.domain.member.dto.response.MemberProfileResponse;
import com.swucjute.api.domain.member.dto.response.MemberSummaryResponse;
import com.swucjute.api.domain.member.entity.ChurchMember;
import com.swucjute.api.domain.member.entity.Department;
import com.swucjute.api.domain.member.entity.Gender;
import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.entity.MemberProfile;
import com.swucjute.api.domain.member.entity.MemberStatus;
import com.swucjute.api.domain.member.repository.ChurchMemberRepository;
import com.swucjute.api.domain.member.repository.MemberProfileRepository;
import com.swucjute.api.domain.member.repository.MemberRepository;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 로그인한 본인의 회원/프로필 관리. 관리자 기능은 {@link MemberAdminService}로 분리되어 있다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;
  private final MemberProfileRepository memberProfileRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final ChurchMemberRepository churchMemberRepository;

  /**
   * 카카오 로그인 후 초기 프로필 등록. 회원당 1회만 가능하다. 소속이 청년(YOUTH)이면 이름(포함 일치)과 생년월일 또는 전화번호 중 하나가 일치하는
   * 교적부(ChurchMember)가 있어야만 등록할 수 있고(없으면 CHURCH_MEMBER_NOT_FOUND, 이미 다른 회원이 연결한 교적이면
   * CHURCH_MEMBER_ALREADY_LINKED), 등록과 동시에 회원 상태를 ACTIVE로 활성화한다. 그 외 소속(교역자/코치 등)은 교적부 매칭 없이 등록되며
   * 관리자 승인을 위해 PENDING을 유지한다.
   */
  @Transactional
  public MemberMeResponse registerProfile(Long memberId, MemberProfileRegisterRequest request) {
    Member member = findActiveMember(memberId);
    if (memberProfileRepository.existsByMember(member)) {
      throw new CustomException(ErrorCode.PROFILE_ALREADY_EXISTS);
    }

    Gender gender = parseEnum(Gender.class, request.gender(), ErrorCode.INVALID_GENDER);
    if (gender == null) {
      throw new CustomException(ErrorCode.INVALID_GENDER);
    }
    Department department = request.department();

    // 청년(YOUTH)일 때만 교적부 매칭이 필수이며, 매칭되면 연결하고 회원을 바로 활성화한다.
    ChurchMember churchMember = null;
    if (department == Department.YOUTH) {
      churchMember =
          churchMemberRepository
              .findByNameContainingAndBirthDateOrPhoneNumber(
                  request.name(), request.birthDate(), request.phoneNumber())
              .stream()
              .findFirst()
              .orElseThrow(() -> new CustomException(ErrorCode.CHURCH_MEMBER_NOT_FOUND));
      if (memberProfileRepository.findByChurchMember(churchMember).isPresent()) {
        throw new CustomException(ErrorCode.CHURCH_MEMBER_ALREADY_LINKED);
      }
      churchMember.assignGenderIfAbsent(gender);
      member.changeStatus(MemberStatus.ACTIVE);
    }

    MemberProfile profile =
        MemberProfile.create(
            member,
            request.name(),
            gender,
            request.birthDate(),
            request.phoneNumber(),
            request.profileImageUrl(),
            department,
            request.position(),
            request.bankName(),
            request.accountNumber(),
            churchMember);
    memberProfileRepository.save(profile);

    return MemberMeResponse.of(member, profile);
  }

  /** 내 정보 전체 조회. 프로필 미등록 회원은 profileCompleted=false로 응답한다. */
  public MemberMeResponse getMyProfile(Long memberId) {
    Member member = findActiveMember(memberId);
    MemberProfile profile = memberProfileRepository.findByMember(member).orElse(null);
    return MemberMeResponse.of(member, profile);
  }

  /** 내 정보 요약 조회 (홈/헤더용 경량 응답). */
  public MemberSummaryResponse getMySummary(Long memberId) {
    Member member = findActiveMember(memberId);
    MemberProfile profile = memberProfileRepository.findByMember(member).orElse(null);
    return MemberSummaryResponse.of(member, profile);
  }

  /** 내 정보 수정. 실명/성별은 변경하지 않으며, 프로필이 등록된 회원만 수정할 수 있다. */
  @Transactional
  public MemberProfileResponse updateMyProfile(Long memberId, MemberUpdateRequest request) {
    Member member = findActiveMember(memberId);
    MemberProfile profile =
        memberProfileRepository
            .findByMember(member)
            .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND));

    profile.updateContact(
        request.birthDate(),
        request.phoneNumber(),
        request.profileImageUrl(),
        request.bankName(),
        request.accountNumber());
    return MemberProfileResponse.of(profile);
  }

  /**
   * 청년부 등록 조회. 이름(포함 일치)과 생년월일 또는 전화번호 중 하나가 일치하는 교적부의 기본정보(id/이름/성별/생년월일/전화번호)를 반환한다. 프로필 등록 여부와
   * 무관하게 교적부 자체가 없으면 CHURCH_MEMBER_NOT_FOUND를 던진다.
   */
  public ChurchMemberResponse lookupChurchMember(
      String name, LocalDate birthDate, String phoneNumber) {
    ChurchMember churchMember =
        churchMemberRepository
            .findByNameContainingAndBirthDateOrPhoneNumber(name, birthDate, phoneNumber)
            .stream()
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.CHURCH_MEMBER_NOT_FOUND));
    return ChurchMemberResponse.of(churchMember);
  }

  /** 회원 탈퇴 (soft delete). status=WITHDRAWN, deleted_at 기록 후 리프레시 토큰을 폐기한다. */
  @Transactional
  public Void withdrawMe(Long memberId) {
    Member member = findActiveMember(memberId);
    member.withdraw(LocalDateTime.now());
    refreshTokenRepository
        .findByMember(member)
        .ifPresent(token -> token.revoke(LocalDateTime.now()));
    return null;
  }

  private Member findActiveMember(Long memberId) {
    return memberRepository
        .findById(memberId)
        .filter(m -> !m.isWithdrawn())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
  }

  private static <E extends Enum<E>> E parseEnum(Class<E> type, String value, ErrorCode error) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return Enum.valueOf(type, value.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new CustomException(error);
    }
  }
}
