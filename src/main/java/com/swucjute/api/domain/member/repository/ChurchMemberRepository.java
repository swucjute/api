package com.swucjute.api.domain.member.repository;

import com.swucjute.api.domain.member.entity.ChurchMember;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChurchMemberRepository extends JpaRepository<ChurchMember, Long> {

  /**
   * 이름이 포함되고(홍길동A 등 대응), 생년월일 또는 전화번호 중 하나라도 일치하는 교적부를 찾는다. 초기 프로필 등록(registerProfile)과 청년부 등록
   * 조회(church-lookup) 양쪽에서 동일하게 사용한다.
   */
  @Query(
      "SELECT cm FROM ChurchMember cm "
          + "WHERE cm.name LIKE CONCAT('%', :name, '%') "
          + "AND (cm.birthDate = :birthDate OR cm.phoneNumber = :phoneNumber)")
  List<ChurchMember> findByNameContainingAndBirthDateOrPhoneNumber(
      @Param("name") String name,
      @Param("birthDate") LocalDate birthDate,
      @Param("phoneNumber") String phoneNumber);
}
