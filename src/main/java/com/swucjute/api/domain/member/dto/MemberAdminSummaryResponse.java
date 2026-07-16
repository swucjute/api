package com.swucjute.api.domain.member.dto;

import com.swucjute.api.domain.member.entity.Department;
import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.entity.MemberProfile;
import com.swucjute.api.domain.member.entity.MemberStatus;

/** 관리자 소속/상태 변경 응답. */
public record MemberAdminSummaryResponse(
    Long memberId, String name, MemberStatus status, Department department, String position) {

  public static MemberAdminSummaryResponse of(Member member, MemberProfile profile) {
    return new MemberAdminSummaryResponse(
        member.getId(),
        profile == null ? null : profile.getName(),
        member.getStatus(),
        profile == null ? null : profile.getDepartment(),
        profile == null ? null : profile.getPosition());
  }
}
