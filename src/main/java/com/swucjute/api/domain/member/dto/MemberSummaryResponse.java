package com.swucjute.api.domain.member.dto;

import com.swucjute.api.domain.member.entity.Department;
import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.entity.MemberProfile;
import com.swucjute.api.domain.member.entity.MemberRole;
import com.swucjute.api.domain.member.entity.MemberStatus;

/** 내 정보 요약 조회 응답 (홈/헤더용 경량 응답). */
public record MemberSummaryResponse(
    Long memberId,
    String name,
    MemberRole role,
    MemberStatus status,
    Department department,
    String profileImageUrl,
    boolean profileCompleted) {

  public static MemberSummaryResponse of(Member member, MemberProfile profile) {
    return new MemberSummaryResponse(
        member.getId(),
        profile == null ? null : profile.getName(),
        member.getMemberRole(),
        member.getStatus(),
        profile == null ? null : profile.getDepartment(),
        profile == null ? null : profile.getProfileImageUrl(),
        profile != null);
  }
}
