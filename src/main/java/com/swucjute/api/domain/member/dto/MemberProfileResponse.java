package com.swucjute.api.domain.member.dto;

import com.swucjute.api.domain.member.entity.BankName;
import com.swucjute.api.domain.member.entity.Department;
import com.swucjute.api.domain.member.entity.Gender;
import com.swucjute.api.domain.member.entity.MemberProfile;
import java.time.LocalDate;

/** 프로필 등록/수정 응답. */
public record MemberProfileResponse(
    Long memberId,
    String name,
    Gender gender,
    LocalDate birthDate,
    String phoneNumber,
    String profileImageUrl,
    Department department,
    String position,
    BankName bankName,
    String accountNumber) {

  public static MemberProfileResponse of(MemberProfile p) {
    return new MemberProfileResponse(
        p.getMember().getId(),
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
