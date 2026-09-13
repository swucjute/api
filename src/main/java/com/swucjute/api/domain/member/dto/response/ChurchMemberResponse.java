package com.swucjute.api.domain.member.dto.response;

import com.swucjute.api.domain.member.entity.ChurchMember;
import com.swucjute.api.domain.member.entity.Gender;
import java.time.LocalDate;

/** 청년부 등록 조회 응답. 교적부(ChurchMember) 기본정보만 담는다. */
public record ChurchMemberResponse(
    Long id, String name, Gender gender, LocalDate birthDate, String phoneNumber) {

  public static ChurchMemberResponse of(ChurchMember churchMember) {
    return new ChurchMemberResponse(
        churchMember.getId(),
        churchMember.getName(),
        churchMember.getGender(),
        churchMember.getBirthDate(),
        churchMember.getPhoneNumber());
  }
}
