package com.swucjute.api.domain.member.service;

import com.swucjute.api.domain.auth.repository.RefreshTokenRepository;
import com.swucjute.api.domain.member.dto.request.MemberProfileRegisterRequest;
import com.swucjute.api.domain.member.dto.request.MemberUpdateRequest;
import com.swucjute.api.domain.member.dto.response.MemberMeResponse;
import com.swucjute.api.domain.member.dto.response.MemberProfileResponse;
import com.swucjute.api.domain.member.dto.response.MemberSummaryResponse;
import com.swucjute.api.domain.member.entity.BankName;
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
   * 카카오 로그인 후 초기 프로필 등록. 회원당 1회만 가능하며, 등록 후 소속이 청년(YOUTH)이면 ACTIVE로 활성화하고 그 외 소속(교역자/코치 등)은 관리자 승인을
   * 위해 PENDING을 유지한다. 이름/생년월일/연락처가 교적부(ChurchMember)와 일치하면 자동으로 연결한다.
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
    Department department =
        parseEnum(Department.class, request.department(), ErrorCode.INVALID_DEPARTMENT);
    BankName bankName = parseEnum(BankName.class, request.bankName(), ErrorCode.INVALID_INPUT);

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
            bankName,
            request.accountNumber());
    memberProfileRepository.save(profile);

    churchMemberRepository
        .findByNameContainingAndBirthDateAndPhoneNumber(
            request.name(), request.birthDate(), request.phoneNumber())
        .stream()
        .findFirst()
        .ifPresent(profile::linkChurchMember);

    // YOUTH(청년) 정보로 들어올 경우, 자동 활성화, 이외에는 회원가입 관리자 승인 필요
    if (department == Department.YOUTH) {
      member.changeStatus(MemberStatus.ACTIVE);
    }
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

    BankName bankName = parseEnum(BankName.class, request.bankName(), ErrorCode.INVALID_INPUT);
    profile.updateContact(
        request.birthDate(),
        request.phoneNumber(),
        request.profileImageUrl(),
        bankName,
        request.accountNumber());
    return MemberProfileResponse.of(profile);
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
