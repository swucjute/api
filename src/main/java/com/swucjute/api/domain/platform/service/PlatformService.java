package com.swucjute.api.domain.platform.service;

import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.entity.MemberProfile;
import com.swucjute.api.domain.member.entity.MemberRole;
import com.swucjute.api.domain.member.repository.MemberProfileRepository;
import com.swucjute.api.domain.member.repository.MemberRepository;
import com.swucjute.api.domain.platform.dto.PlatformApprovalStatusUpdateRequest;
import com.swucjute.api.domain.platform.dto.PlatformDetailResponse;
import com.swucjute.api.domain.platform.dto.PlatformListItemResponse;
import com.swucjute.api.domain.platform.dto.PlatformMemberResponse;
import com.swucjute.api.domain.platform.dto.PlatformMemberStatusUpdateRequest;
import com.swucjute.api.domain.platform.dto.PlatformSaveRequest;
import com.swucjute.api.domain.platform.entity.Platform;
import com.swucjute.api.domain.platform.entity.PlatformApprovalStatus;
import com.swucjute.api.domain.platform.entity.PlatformMember;
import com.swucjute.api.domain.platform.entity.PlatformMemberStatus;
import com.swucjute.api.domain.platform.entity.PlatformOperatingStatus;
import com.swucjute.api.domain.platform.repository.PlatformMemberRepository;
import com.swucjute.api.domain.platform.repository.PlatformRepository;
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
public class PlatformService {

  private final PlatformRepository platformRepository;
  private final PlatformMemberRepository platformMemberRepository;
  private final MemberRepository memberRepository;
  private final MemberProfileRepository memberProfileRepository;

  /** 플랫폼 목록 조회 (approvalStatus/operatingStatus/keyword 선택 필터, 생성일 내림차순). */
  public PageResponse<PlatformListItemResponse> getPlatforms(
      int page, int size, String approvalStatus, String operatingStatus, String keyword) {
    PlatformApprovalStatus approvalStatusFilter =
        parseEnum(PlatformApprovalStatus.class, approvalStatus, ErrorCode.INVALID_APPROVAL_STATUS);
    PlatformOperatingStatus operatingStatusFilter =
        parseEnum(
            PlatformOperatingStatus.class, operatingStatus, ErrorCode.INVALID_OPERATING_STATUS);
    String keywordFilter = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Platform> platforms =
        platformRepository.searchPlatforms(
            approvalStatusFilter, operatingStatusFilter, keywordFilter, pageable);

    Map<Long, MemberProfile> owners = ownerProfilesByMemberId(platforms.getContent());
    Map<Long, Long> memberCounts = approvedMemberCountsByPlatformId(platforms.getContent());
    Page<PlatformListItemResponse> mapped =
        platforms.map(
            p ->
                PlatformListItemResponse.of(
                    p,
                    owners.get(p.getOwnerMember().getId()),
                    memberCounts.getOrDefault(p.getId(), 0L)));
    return PageResponse.of(mapped);
  }

  /** 플랫폼 상세 조회. */
  public PlatformDetailResponse getPlatform(Long platformId) {
    Platform platform = findPlatform(platformId);
    MemberProfile ownerProfile =
        memberProfileRepository.findByMember(platform.getOwnerMember()).orElse(null);
    long approvedMemberCount =
        platformMemberRepository.countByPlatformAndStatus(platform, PlatformMemberStatus.APPROVED);
    return PlatformDetailResponse.of(platform, ownerProfile, approvedMemberCount);
  }

  /** 플랫폼 생성/제안. 생성자를 platform_members에 OWNER/APPROVED로 함께 등록한다. */
  @Transactional
  public PlatformDetailResponse create(PlatformSaveRequest request) {
    Member owner = currentMember();
    PlatformOperatingStatus operatingStatus =
        parseEnum(
            PlatformOperatingStatus.class,
            request.operatingStatus(),
            ErrorCode.INVALID_OPERATING_STATUS);

    Platform platform =
        Platform.create(
            owner,
            request.title(),
            request.scheduleText(),
            request.startsAt(),
            request.endsAt(),
            request.location(),
            request.content(),
            request.purpose(),
            request.etc(),
            request.posterUrl(),
            operatingStatus);
    platformRepository.save(platform);

    LocalDateTime now = LocalDateTime.now();
    platformMemberRepository.save(PlatformMember.createOwner(platform, owner, now));

    MemberProfile ownerProfile = memberProfileRepository.findByMember(owner).orElse(null);
    return PlatformDetailResponse.of(platform, ownerProfile, 1L);
  }

  /** 플랫폼 수정. 작성자 본인 또는 ADMIN만 가능하다. */
  @Transactional
  public PlatformDetailResponse update(Long platformId, PlatformSaveRequest request) {
    Platform platform = findPlatform(platformId);
    requireOwnerOrAdmin(platform);

    PlatformOperatingStatus operatingStatus =
        parseEnum(
            PlatformOperatingStatus.class,
            request.operatingStatus(),
            ErrorCode.INVALID_OPERATING_STATUS);
    platform.updateDetails(
        request.title(),
        request.scheduleText(),
        request.startsAt(),
        request.endsAt(),
        request.location(),
        request.content(),
        request.purpose(),
        request.etc(),
        request.posterUrl(),
        operatingStatus);

    MemberProfile ownerProfile =
        memberProfileRepository.findByMember(platform.getOwnerMember()).orElse(null);
    return PlatformDetailResponse.of(platform, ownerProfile, approvedMemberCount(platform));
  }

  /** 플랫폼 삭제 (soft delete). 작성자 본인 또는 ADMIN만 가능하다. */
  @Transactional
  public Void delete(Long platformId) {
    Platform platform = findPlatform(platformId);
    requireOwnerOrAdmin(platform);
    platform.softDelete(LocalDateTime.now());
    return null;
  }

  /** 관리자 전용 승인상태 변경. 접근 권한(ADMIN) 검사는 컨트롤러의 @PreAuthorize에서 수행한다. */
  @Transactional
  public PlatformDetailResponse changeApprovalStatus(
      Long platformId, PlatformApprovalStatusUpdateRequest request) {
    Platform platform = findPlatform(platformId);
    PlatformApprovalStatus status =
        parseEnum(
            PlatformApprovalStatus.class,
            request.approvalStatus(),
            ErrorCode.INVALID_APPROVAL_STATUS);
    if (status == null) {
      throw new CustomException(ErrorCode.INVALID_APPROVAL_STATUS);
    }
    platform.changeApprovalStatus(status);

    MemberProfile ownerProfile =
        memberProfileRepository.findByMember(platform.getOwnerMember()).orElse(null);
    return PlatformDetailResponse.of(platform, ownerProfile, approvedMemberCount(platform));
  }

  /**
   * 플랫폼 가입 신청. 승인(APPROVED)+모집중(RECRUITING) 상태인 플랫폼에만 신청할 수 있다. 예전에 거절/탈퇴한 이력이 있으면(unique 제약상 새
   * row를 만들 수 없으므로) 기존 row를 PENDING으로 되돌려 재신청 처리한다.
   */
  @Transactional
  public PlatformMemberResponse join(Long platformId) {
    Platform platform = findPlatform(platformId);
    if (platform.getApprovalStatus() != PlatformApprovalStatus.APPROVED
        || platform.getOperatingStatus() != PlatformOperatingStatus.RECRUITING) {
      throw new CustomException(ErrorCode.PLATFORM_NOT_JOINABLE);
    }
    Member member = currentMember();
    LocalDateTime now = LocalDateTime.now();

    PlatformMember platformMember =
        platformMemberRepository
            .findByPlatformAndMember(platform, member)
            .map(
                existing -> {
                  if (existing.getStatus() == PlatformMemberStatus.PENDING
                      || existing.getStatus() == PlatformMemberStatus.APPROVED) {
                    throw new CustomException(ErrorCode.PLATFORM_MEMBER_ALREADY_EXISTS);
                  }
                  existing.reapply(now);
                  return existing;
                })
            .orElseGet(
                () ->
                    platformMemberRepository.save(
                        PlatformMember.applyAsMember(platform, member, now)));

    MemberProfile profile = memberProfileRepository.findByMember(member).orElse(null);
    return PlatformMemberResponse.of(platformMember, profile);
  }

  /** 플랫폼 멤버 목록 조회. status 선택 필터, 신청일 내림차순. OWNER 또는 ADMIN만 가능하다. */
  public PageResponse<PlatformMemberResponse> getMembers(
      Long platformId, String status, int page, int size) {
    Platform platform = findPlatform(platformId);
    requireOwnerOrAdmin(platform);

    PlatformMemberStatus statusFilter =
        parseEnum(PlatformMemberStatus.class, status, ErrorCode.INVALID_PLATFORM_MEMBER_STATUS);
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestedAt"));
    Page<PlatformMember> members =
        statusFilter == null
            ? platformMemberRepository.findByPlatform(platform, pageable)
            : platformMemberRepository.findByPlatformAndStatus(platform, statusFilter, pageable);

    Map<Long, MemberProfile> profiles = memberProfilesByMemberId(members.getContent());
    Page<PlatformMemberResponse> mapped =
        members.map(pm -> PlatformMemberResponse.of(pm, profiles.get(pm.getMember().getId())));
    return PageResponse.of(mapped);
  }

  /** 플랫폼 멤버 상태 변경(승인/거절/탈퇴 처리). OWNER 또는 ADMIN만 가능하며, 소유자 본인의 상태는 바꿀 수 없다. */
  @Transactional
  public PlatformMemberResponse updateMemberStatus(
      Long platformId, Long memberId, PlatformMemberStatusUpdateRequest request) {
    Platform platform = findPlatform(platformId);
    requireOwnerOrAdmin(platform);

    Member targetMember =
        memberRepository
            .findById(memberId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    PlatformMember platformMember =
        platformMemberRepository
            .findByPlatformAndMember(platform, targetMember)
            .orElseThrow(() -> new CustomException(ErrorCode.PLATFORM_MEMBER_NOT_FOUND));
    if (platformMember.isOwner()) {
      throw new CustomException(ErrorCode.PLATFORM_OWNER_CANNOT_LEAVE);
    }

    PlatformMemberStatus status =
        parseEnum(
            PlatformMemberStatus.class, request.status(), ErrorCode.INVALID_PLATFORM_MEMBER_STATUS);
    if (status == null) {
      throw new CustomException(ErrorCode.INVALID_PLATFORM_MEMBER_STATUS);
    }
    platformMember.changeStatus(status, request.rejectedReason(), LocalDateTime.now());

    MemberProfile profile = memberProfileRepository.findByMember(targetMember).orElse(null);
    return PlatformMemberResponse.of(platformMember, profile);
  }

  /** 본인 탈퇴. 소유자는 탈퇴할 수 없다. */
  @Transactional
  public Void leave(Long platformId) {
    Platform platform = findPlatform(platformId);
    Member member = currentMember();
    PlatformMember platformMember =
        platformMemberRepository
            .findByPlatformAndMember(platform, member)
            .orElseThrow(() -> new CustomException(ErrorCode.PLATFORM_MEMBER_NOT_FOUND));
    if (platformMember.isOwner()) {
      throw new CustomException(ErrorCode.PLATFORM_OWNER_CANNOT_LEAVE);
    }
    platformMember.withdraw();
    return null;
  }

  private Platform findPlatform(Long platformId) {
    return platformRepository
        .findById(platformId)
        .filter(p -> !p.isDeleted())
        .orElseThrow(() -> new CustomException(ErrorCode.PLATFORM_NOT_FOUND));
  }

  /** platform.ownerMember 본인이거나, 전역 MemberRole.ADMIN이어야 통과한다 (PlatformMemberRole과는 무관). */
  private void requireOwnerOrAdmin(Platform platform) {
    Member current = currentMember();
    boolean isOwner = platform.getOwnerMember().getId().equals(current.getId());
    boolean isAdmin = current.getMemberRole() == MemberRole.ADMIN;
    if (!isOwner && !isAdmin) {
      throw new CustomException(ErrorCode.FORBIDDEN);
    }
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

  private Map<Long, MemberProfile> ownerProfilesByMemberId(List<Platform> platforms) {
    if (platforms.isEmpty()) {
      return Map.of();
    }
    List<Member> owners = platforms.stream().map(Platform::getOwnerMember).distinct().toList();
    return memberProfileRepository.findByMemberIn(owners).stream()
        .collect(Collectors.toMap(p -> p.getMember().getId(), Function.identity()));
  }

  private long approvedMemberCount(Platform platform) {
    return platformMemberRepository.countByPlatformAndStatus(
        platform, PlatformMemberStatus.APPROVED);
  }

  private Map<Long, Long> approvedMemberCountsByPlatformId(List<Platform> platforms) {
    if (platforms.isEmpty()) {
      return Map.of();
    }
    return platformMemberRepository
        .countByPlatformInAndStatus(platforms, PlatformMemberStatus.APPROVED)
        .stream()
        .collect(
            Collectors.toMap(
                PlatformMemberRepository.PlatformMemberCountProjection::getPlatformId,
                PlatformMemberRepository.PlatformMemberCountProjection::getCount));
  }

  private Map<Long, MemberProfile> memberProfilesByMemberId(List<PlatformMember> platformMembers) {
    if (platformMembers.isEmpty()) {
      return Map.of();
    }
    List<Member> members =
        platformMembers.stream().map(PlatformMember::getMember).distinct().toList();
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

  private Object notImplemented() {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }
}
