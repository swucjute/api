package com.swucjute.api.domain.member.service;

import com.swucjute.api.domain.auth.repository.RefreshTokenRepository;
import com.swucjute.api.domain.member.dto.request.MemberDepartmentUpdateRequest;
import com.swucjute.api.domain.member.dto.request.MemberProfileRegisterRequest;
import com.swucjute.api.domain.member.dto.request.MemberStatusUpdateRequest;
import com.swucjute.api.domain.member.dto.request.MemberUpdateRequest;
import com.swucjute.api.domain.member.dto.response.MemberAdminDetailResponse;
import com.swucjute.api.domain.member.dto.response.MemberAdminSummaryResponse;
import com.swucjute.api.domain.member.dto.response.MemberListItemResponse;
import com.swucjute.api.domain.member.dto.response.MemberMeResponse;
import com.swucjute.api.domain.member.dto.response.MemberProfileResponse;
import com.swucjute.api.domain.member.dto.response.MemberSummaryResponse;
import com.swucjute.api.domain.member.entity.BankName;
import com.swucjute.api.domain.member.entity.Department;
import com.swucjute.api.domain.member.entity.Gender;
import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.entity.MemberProfile;
import com.swucjute.api.domain.member.entity.MemberStatus;
import com.swucjute.api.domain.member.repository.MemberProfileRepository;
import com.swucjute.api.domain.member.repository.MemberRepository;
import com.swucjute.api.global.common.PageResponse;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;
  private final MemberProfileRepository memberProfileRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  /**
   * 카카오 로그인 후 초기 프로필 등록. 회원당 1회만 가능하며, 등록 후 소속이 청년(YOUTH)이면 ACTIVE로 활성화하고 그 외 소속(교역자/코치 등)은 관리자 승인을
   * 위해 PENDING을 유지한다.
   */
  @Transactional
  public MemberMeResponse registerProfile(MemberProfileRegisterRequest request) {
    Member member = currentMember();
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

    if (department == Department.YOUTH) {
      member.changeStatus(MemberStatus.ACTIVE);
    }
    return MemberMeResponse.of(member, profile);
  }

  /** 내 정보 전체 조회. 프로필 미등록 회원은 profileCompleted=false로 응답한다. */
  public MemberMeResponse getMyProfile() {
    Member member = currentMember();
    MemberProfile profile = memberProfileRepository.findByMember(member).orElse(null);
    return MemberMeResponse.of(member, profile);
  }

  /** 내 정보 요약 조회 (홈/헤더용 경량 응답). */
  public MemberSummaryResponse getMySummary() {
    Member member = currentMember();
    MemberProfile profile = memberProfileRepository.findByMember(member).orElse(null);
    return MemberSummaryResponse.of(member, profile);
  }

  /** 내 정보 수정. 실명/성별은 변경하지 않으며, 프로필이 등록된 회원만 수정할 수 있다. */
  @Transactional
  public MemberProfileResponse updateMyProfile(MemberUpdateRequest request) {
    Member member = currentMember();
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
  public Void withdrawMe() {
    Member member = currentMember();
    member.withdraw(LocalDateTime.now());
    refreshTokenRepository
        .findByMember(member)
        .ifPresent(token -> token.revoke(LocalDateTime.now()));
    return null;
  }

  /**
   * 관리자 회원 목록 조회 (status/department/keyword 선택 필터, 생성일 내림차순). 권한 검사는 컨트롤러의 @PreAuthorize에서 수행한다.
   */
  public PageResponse<MemberListItemResponse> getMembers(
      int page, int size, String status, String department, String keyword) {
    MemberStatus statusFilter = parseEnum(MemberStatus.class, status, ErrorCode.INVALID_STATUS);
    Department departmentFilter =
        parseEnum(Department.class, department, ErrorCode.INVALID_DEPARTMENT);
    String keywordFilter = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Member> members =
        memberRepository.searchMembers(statusFilter, departmentFilter, keywordFilter, pageable);

    Map<Long, MemberProfile> profiles = profilesByMemberId(members.getContent());
    Page<MemberListItemResponse> mapped =
        members.map(m -> MemberListItemResponse.of(m, profiles.get(m.getId())));
    return PageResponse.of(mapped);
  }

  /** 관리자 회원 단건 조회. */
  public MemberAdminDetailResponse getMember(Long memberId) {
    Member member = findMember(memberId);
    MemberProfile profile = memberProfileRepository.findByMember(member).orElse(null);
    return MemberAdminDetailResponse.of(member, profile);
  }

  /** 관리자 회원 소속/직분 수정. */
  @Transactional
  public MemberAdminSummaryResponse updateDepartment(
      Long memberId, MemberDepartmentUpdateRequest request) {
    Member member = findMember(memberId);
    MemberProfile profile =
        memberProfileRepository
            .findByMember(member)
            .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_FOUND));

    Department department =
        parseEnum(Department.class, request.department(), ErrorCode.INVALID_DEPARTMENT);
    if (department == null) {
      throw new CustomException(ErrorCode.INVALID_DEPARTMENT);
    }
    profile.updateDepartment(department, request.position());
    return MemberAdminSummaryResponse.of(member, profile);
  }

  /** 관리자 회원 상태 변경. */
  @Transactional
  public MemberAdminSummaryResponse updateStatus(Long memberId, MemberStatusUpdateRequest request) {
    Member member = findMember(memberId);

    MemberStatus status = parseEnum(MemberStatus.class, request.status(), ErrorCode.INVALID_STATUS);
    if (status == null) {
      throw new CustomException(ErrorCode.INVALID_STATUS);
    }
    member.changeStatus(status);

    MemberProfile profile = memberProfileRepository.findByMember(member).orElse(null);
    return MemberAdminSummaryResponse.of(member, profile);
  }

  private Member findMember(Long memberId) {
    return memberRepository
        .findById(memberId)
        .filter(m -> !m.isWithdrawn())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
  }

  private Map<Long, MemberProfile> profilesByMemberId(List<Member> members) {
    if (members.isEmpty()) {
      return Map.of();
    }
    return memberProfileRepository.findByMemberIn(members).stream()
        .collect(Collectors.toMap(p -> p.getMember().getId(), Function.identity()));
  }

  private Member currentMember() {
    return memberRepository
        .findById(currentMemberId())
        .filter(m -> !m.isWithdrawn())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
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
