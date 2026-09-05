package com.swucjute.api.domain.member.repository;

import com.swucjute.api.domain.member.entity.ChurchMember;
import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.entity.MemberProfile;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {

  Optional<MemberProfile> findByMember(Member member);

  Optional<MemberProfile> findByMemberId(Long memberId);

  boolean existsByMember(Member member);

  List<MemberProfile> findByMemberIn(Collection<Member> members);

  Optional<MemberProfile> findByChurchMember(ChurchMember churchMember);
}
