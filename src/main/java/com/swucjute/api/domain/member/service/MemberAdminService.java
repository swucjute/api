package com.swucjute.api.domain.member.service;

import com.swucjute.api.domain.member.dto.request.MemberDepartmentUpdateRequest;
import com.swucjute.api.domain.member.dto.request.MemberStatusUpdateRequest;
import com.swucjute.api.domain.member.dto.response.MemberAdminDetailResponse;
import com.swucjute.api.domain.member.dto.response.MemberAdminSummaryResponse;
import com.swucjute.api.domain.member.dto.response.MemberListItemResponse;
import com.swucjute.api.domain.member.entity.Department;
import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.entity.MemberProfile;
import com.swucjute.api.domain.member.entity.MemberStatus;
import com.swucjute.api.domain.member.repository.MemberProfileRepository;
import com.swucjute.api.domain.member.repository.MemberRepository;
import com.swucjute.api.global.common.PageResponse;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 관리자 전용 회원 관리. 접근 권한(ADMIN) 검사는 컨트롤러의 @PreAuthorize에서 수행한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberAdminService {

  private final MemberRepository memberRepository;
  private final MemberProfileRepository memberProfileRepository;

  /** 회원 목록 조회 (status/department/keyword 선택 필터, 생성일 내림차순). */
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

  /** 회원 단건 조회. */
  public MemberAdminDetailResponse getMember(Long memberId) {
    Member member = findMember(memberId);
    MemberProfile profile = memberProfileRepository.findByMember(member).orElse(null);
    return MemberAdminDetailResponse.of(member, profile);
  }

  /** 회원 소속/직분 수정. */
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

  /** 회원 상태 변경. */
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
