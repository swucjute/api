package com.swucjute.api.domain.platformactivity.repository;

import com.swucjute.api.domain.platformactivity.entity.PlatformActivity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformActivityRepository extends JpaRepository<PlatformActivity, Long> {

  Page<PlatformActivity> findByDeleteYn(String deleteYn, Pageable pageable);

  Optional<PlatformActivity> findByIdAndUserIdAndDeleteYn(Long id, Long userId, String deleteYn);
}
