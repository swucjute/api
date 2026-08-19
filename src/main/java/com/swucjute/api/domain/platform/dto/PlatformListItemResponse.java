package com.swucjute.api.domain.platform.dto;

import com.swucjute.api.domain.member.entity.MemberProfile;
import com.swucjute.api.domain.platform.entity.Platform;
import com.swucjute.api.domain.platform.entity.PlatformApprovalStatus;
import com.swucjute.api.domain.platform.entity.PlatformOperatingStatus;
import java.time.LocalDateTime;

public record PlatformListItemResponse(
    Long platformId,
    String title,
    String scheduleText,
    LocalDateTime startsAt,
    LocalDateTime endsAt,
    String location,
    String posterUrl,
    PlatformApprovalStatus approvalStatus,
    PlatformOperatingStatus operatingStatus,
    Long ownerMemberId,
    String ownerName,
    LocalDateTime createdAt) {

  public static PlatformListItemResponse of(Platform platform, MemberProfile ownerProfile) {
    return new PlatformListItemResponse(
        platform.getId(),
        platform.getTitle(),
        platform.getScheduleText(),
        platform.getStartsAt(),
        platform.getEndsAt(),
        platform.getLocation(),
        platform.getPosterUrl(),
        platform.getApprovalStatus(),
        platform.getOperatingStatus(),
        platform.getOwnerMember().getId(),
        ownerProfile == null ? null : ownerProfile.getName(),
        platform.getCreatedAt());
  }
}
