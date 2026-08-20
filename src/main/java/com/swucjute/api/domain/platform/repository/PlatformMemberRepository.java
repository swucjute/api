package com.swucjute.api.domain.platform.repository;

import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.platform.entity.Platform;
import com.swucjute.api.domain.platform.entity.PlatformMember;
import com.swucjute.api.domain.platform.entity.PlatformMemberStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformMemberRepository extends JpaRepository<PlatformMember, Long> {

  Optional<PlatformMember> findByPlatformAndMember(Platform platform, Member member);

  Page<PlatformMember> findByPlatform(Platform platform, Pageable pageable);

  Page<PlatformMember> findByPlatformAndStatus(
      Platform platform, PlatformMemberStatus status, Pageable pageable);
}
