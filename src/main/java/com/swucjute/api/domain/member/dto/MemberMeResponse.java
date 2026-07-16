package com.swucjute.api.domain.member.dto;

import com.swucjute.api.domain.member.entity.BankName;
import com.swucjute.api.domain.member.entity.Department;
import com.swucjute.api.domain.member.entity.Gender;
import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.entity.MemberProfile;
import com.swucjute.api.domain.member.entity.MemberRole;
import com.swucjute.api.domain.member.entity.MemberStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 내 정보 전체 조회 응답 (마이페이지용). 프로필 미등록 회원은 {@code profile}이 null이다. */
public record MemberMeResponse(
    Long memberId,
    String email,
    MemberRole role,
    MemberStatus status,
    LocalDateTime lastLoginAt,
    boolean profileCompleted,
    ProfileInfo profile) {

  public record ProfileInfo(
      String name,
      Gender gender,
      LocalDate birthDate,
      String phoneNumber,
      String profileImageUrl,
      Department department,
      String position,
      BankName bankName,
      String accountNumber) {

    private static ProfileInfo of(MemberProfile p) {
      return new ProfileInfo(
          p.getName(),
          p.getGender(),
          p.getBirthDate(),
          p.getPhoneNumber(),
          p.getProfileImageUrl(),
          p.getDepartment(),
          p.getPosition(),
          p.getBankName(),
          p.getAccountNumber());
    }
  }

  public static MemberMeResponse of(Member member, MemberProfile profile) {
    return new MemberMeResponse(
        member.getId(),
        member.getEmail(),
        member.getMemberRole(),
        member.getStatus(),
        member.getLastLoginAt(),
        profile != null,
        profile == null ? null : ProfileInfo.of(profile));
  }
}
