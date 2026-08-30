package com.swucjute.api.domain.platform.repository;

import com.swucjute.api.domain.platform.entity.Platform;
import com.swucjute.api.domain.platform.entity.PlatformApprovalStatus;
import com.swucjute.api.domain.platform.entity.PlatformOperatingStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRepository extends JpaRepository<Platform, Long> {

  Page<Platform> findByApprovalStatusAndOperatingStatusAndDeletedAtIsNull(
      PlatformApprovalStatus approvalStatus,
      PlatformOperatingStatus operatingStatus,
      Pageable pageable);

  Page<Platform> findByDeletedAtIsNull(Pageable pageable);

  Optional<Platform> findByIdAndDeletedAtIsNull(Long id);
}
