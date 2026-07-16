package com.swucjute.api.domain.member.dto.response;

import com.swucjute.api.domain.member.entity.Department;
import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.entity.MemberProfile;
import com.swucjute.api.domain.member.entity.MemberRole;
import com.swucjute.api.domain.member.entity.MemberStatus;
import java.time.LocalDateTime;

/** 관리자 회원 목록 항목 응답. */
public record MemberListItemResponse(
    Long memberId,
    String name,
    String email,
    MemberRole role,
    MemberStatus status,
    Department department,
    String phoneNumber,
    LocalDateTime createdAt) {

  public static MemberListItemResponse of(Member member, MemberProfile profile) {
    return new MemberListItemResponse(
        member.getId(),
        profile == null ? null : profile.getName(),
        member.getEmail(),
        member.getMemberRole(),
        member.getStatus(),
        profile == null ? null : profile.getDepartment(),
        profile == null ? null : profile.getPhoneNumber(),
        member.getCreatedAt());
  }
}
