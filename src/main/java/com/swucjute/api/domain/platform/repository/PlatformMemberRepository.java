package com.swucjute.api.domain.platform.repository;

import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.platform.entity.Platform;
import com.swucjute.api.domain.platform.entity.PlatformMember;
import com.swucjute.api.domain.platform.entity.PlatformMemberStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlatformMemberRepository extends JpaRepository<PlatformMember, Long> {

  Optional<PlatformMember> findByPlatformAndMember(Platform platform, Member member);

  Page<PlatformMember> findByPlatform(Platform platform, Pageable pageable);

  Page<PlatformMember> findByPlatformAndStatus(
      Platform platform, PlatformMemberStatus status, Pageable pageable);

  long countByPlatformAndStatus(Platform platform, PlatformMemberStatus status);

  /** 플랫폼 목록 여러 건에 대해 상태별 멤버 수를 한 번에 집계한다 (N+1 방지). */
  @Query(
      "select pm.platform.id as platformId, count(pm) as count from PlatformMember pm "
          + "where pm.platform in :platforms and pm.status = :status group by pm.platform.id")
  List<PlatformMemberCountProjection> countByPlatformInAndStatus(
      @Param("platforms") List<Platform> platforms, @Param("status") PlatformMemberStatus status);

  interface PlatformMemberCountProjection {
    Long getPlatformId();

    long getCount();
  }
}
