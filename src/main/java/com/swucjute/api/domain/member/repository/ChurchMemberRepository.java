package com.swucjute.api.domain.member.repository;

import com.swucjute.api.domain.member.entity.ChurchMember;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChurchMemberRepository extends JpaRepository<ChurchMember, Long> {

  Optional<ChurchMember> findByNameAndBirthDateAndPhoneNumber(
      String name, LocalDate birthDate, String phoneNumber);
}
