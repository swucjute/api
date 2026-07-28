package com.swucjute.api.domain.member.repository;

import com.swucjute.api.domain.member.entity.AuthProvider;
import com.swucjute.api.domain.member.entity.Department;
import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.entity.MemberStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

  Optional<Member> findByEmail(String email);

  boolean existsByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

  /**
   * 관리자 회원 목록 검색. status/department/keyword는 모두 선택 필터이며, keyword는 프로필의 실명 또는 전화번호를 부분 검색한다. 프로필이 없는
   * 회원(PENDING 등)도 포함하기 위해 MemberProfile을 LEFT JOIN 한다.
   */
  @Query(
      value =
          """
          select m from Member m
          left join MemberProfile p on p.member = m
          where m.deletedAt is null
            and (:status is null or m.status = :status)
            and (:department is null or p.department = :department)
            and (:keyword is null
                 or p.name like concat('%', :keyword, '%')
                 or p.phoneNumber like concat('%', :keyword, '%'))
          """,
      countQuery =
          """
          select count(m) from Member m
          left join MemberProfile p on p.member = m
          where m.deletedAt is null
            and (:status is null or m.status = :status)
            and (:department is null or p.department = :department)
            and (:keyword is null
                 or p.name like concat('%', :keyword, '%')
                 or p.phoneNumber like concat('%', :keyword, '%'))
          """)
  Page<Member> searchMembers(
      @Param("status") MemberStatus status,
      @Param("department") Department department,
      @Param("keyword") String keyword,
      Pageable pageable);
}
