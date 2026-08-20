package com.swucjute.api.domain.platform.dto;

import com.swucjute.api.domain.member.entity.MemberProfile;
import com.swucjute.api.domain.platform.entity.PlatformMember;
import com.swucjute.api.domain.platform.entity.PlatformMemberRole;
import com.swucjute.api.domain.platform.entity.PlatformMemberStatus;
import java.time.LocalDateTime;

public record PlatformMemberResponse(
    Long platformMemberId,
    Long platformId,
    Long memberId,
    String memberName,
    PlatformMemberRole role,
    PlatformMemberStatus status,
    LocalDateTime requestedAt,
    LocalDateTime approvedAt,
    String rejectedReason) {

  public static PlatformMemberResponse of(PlatformMember platformMember, MemberProfile profile) {
    return new PlatformMemberResponse(
        platformMember.getId(),
        platformMember.getPlatform().getId(),
        platformMember.getMember().getId(),
        profile == null ? null : profile.getName(),
        platformMember.getRole(),
        platformMember.getStatus(),
        platformMember.getRequestedAt(),
        platformMember.getApprovedAt(),
        platformMember.getRejectedReason());
  }
}
