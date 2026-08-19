package com.swucjute.api.domain.platform.repository;

import com.swucjute.api.domain.platform.entity.Platform;
import com.swucjute.api.domain.platform.entity.PlatformApprovalStatus;
import com.swucjute.api.domain.platform.entity.PlatformOperatingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlatformRepository extends JpaRepository<Platform, Long> {

  /**
   * 플랫폼 목록 검색. approvalStatus/operatingStatus/keyword는 모두 선택 필터이며, keyword는 제목 또는 내용을 부분 검색한다. 삭제된
   * 플랫폼(deletedAt이 있는)은 제외한다.
   */
  @Query(
      value =
          """
          select p from Platform p
          where p.deletedAt is null
            and (:approvalStatus is null or p.approvalStatus = :approvalStatus)
            and (:operatingStatus is null or p.operatingStatus = :operatingStatus)
            and (:keyword is null
                 or p.title like concat('%', :keyword, '%')
                 or p.content like concat('%', :keyword, '%'))
          """,
      countQuery =
          """
          select count(p) from Platform p
          where p.deletedAt is null
            and (:approvalStatus is null or p.approvalStatus = :approvalStatus)
            and (:operatingStatus is null or p.operatingStatus = :operatingStatus)
            and (:keyword is null
                 or p.title like concat('%', :keyword, '%')
                 or p.content like concat('%', :keyword, '%'))
          """)
  Page<Platform> searchPlatforms(
      @Param("approvalStatus") PlatformApprovalStatus approvalStatus,
      @Param("operatingStatus") PlatformOperatingStatus operatingStatus,
      @Param("keyword") String keyword,
      Pageable pageable);
}
