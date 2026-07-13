package com.swucjute.api.domain.platform.repository;

import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.platform.entity.Platform;
import com.swucjute.api.domain.platform.entity.PlatformMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformMemberRepository extends JpaRepository<PlatformMember, Long> {

  Optional<PlatformMember> findByPlatformAndMember(Platform platform, Member member);

  boolean existsByPlatformAndMember(Platform platform, Member member);
}
