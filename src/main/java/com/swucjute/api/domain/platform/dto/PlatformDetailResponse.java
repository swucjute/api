package com.swucjute.api.domain.platform.dto;

import com.swucjute.api.domain.member.entity.MemberProfile;
import com.swucjute.api.domain.platform.entity.Platform;
import com.swucjute.api.domain.platform.entity.PlatformApprovalStatus;
import com.swucjute.api.domain.platform.entity.PlatformClosedStatus;
import java.time.LocalDateTime;

public record PlatformDetailResponse(
    Long platformId,
    String title,
    String scheduleText,
    LocalDateTime startsAt,
    LocalDateTime endsAt,
    String location,
    String content,
    String purpose,
    String etc,
    String posterUrl,
    PlatformApprovalStatus approvalStatus,
    boolean recruiting,
    boolean operating,
    PlatformClosedStatus closedStatus,
    Long ownerMemberId,
    String ownerName,
    long approvedMemberCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static PlatformDetailResponse of(
      Platform platform, MemberProfile ownerProfile, long approvedMemberCount) {
    return new PlatformDetailResponse(
        platform.getId(),
        platform.getTitle(),
        platform.getScheduleText(),
        platform.getStartsAt(),
        platform.getEndsAt(),
        platform.getLocation(),
        platform.getContent(),
        platform.getPurpose(),
        platform.getEtc(),
        platform.getPosterUrl(),
        platform.getApprovalStatus(),
        platform.isRecruiting(),
        platform.isOperating(),
        platform.getClosedStatus(),
        platform.getOwnerMember().getId(),
        ownerProfile == null ? null : ownerProfile.getName(),
        approvedMemberCount,
        platform.getCreatedAt(),
        platform.getUpdatedAt());
  }
}
