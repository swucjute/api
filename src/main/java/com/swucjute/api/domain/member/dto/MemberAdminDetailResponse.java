package com.swucjute.api.domain.member.dto;

import com.swucjute.api.domain.member.entity.AuthProvider;
import com.swucjute.api.domain.member.entity.BankName;
import com.swucjute.api.domain.member.entity.Department;
import com.swucjute.api.domain.member.entity.Gender;
import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.entity.MemberProfile;
import com.swucjute.api.domain.member.entity.MemberRole;
import com.swucjute.api.domain.member.entity.MemberStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 관리자 회원 단건 조회 응답. */
public record MemberAdminDetailResponse(
    Long memberId,
    AuthProvider provider,
    String email,
    MemberRole role,
    MemberStatus status,
    LocalDateTime lastLoginAt,
    LocalDateTime createdAt,
    boolean profileCompleted,
    String name,
    Gender gender,
    LocalDate birthDate,
    String phoneNumber,
    String profileImageUrl,
    Department department,
    String position,
    BankName bankName,
    String accountNumber) {

  public static MemberAdminDetailResponse of(Member member, MemberProfile p) {
    return new MemberAdminDetailResponse(
        member.getId(),
        member.getProvider(),
        member.getEmail(),
        member.getMemberRole(),
        member.getStatus(),
        member.getLastLoginAt(),
        member.getCreatedAt(),
        p != null,
        p == null ? null : p.getName(),
        p == null ? null : p.getGender(),
        p == null ? null : p.getBirthDate(),
        p == null ? null : p.getPhoneNumber(),
        p == null ? null : p.getProfileImageUrl(),
        p == null ? null : p.getDepartment(),
        p == null ? null : p.getPosition(),
        p == null ? null : p.getBankName(),
        p == null ? null : p.getAccountNumber());
  }
}
