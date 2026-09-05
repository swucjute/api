package com.swucjute.api.domain.member.repository;

import com.swucjute.api.domain.member.entity.ChurchMember;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChurchMemberRepository extends JpaRepository<ChurchMember, Long> {

  /** 생년월일/연락처가 일치하고 등록된 이름에 입력 이름이 포함되는 교적부를 찾는다 (홍길동A 등 대응). */
  List<ChurchMember> findByNameContainingAndBirthDateAndPhoneNumber(
      String name, LocalDate birthDate, String phoneNumber);
}
