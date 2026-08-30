package com.swucjute.api.domain.platformactivity.repository;

import com.swucjute.api.domain.platformactivity.entity.PlatformActivity;
import com.swucjute.api.domain.platformactivity.entity.PlatformActivityLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformActivityLikeRepository extends JpaRepository<PlatformActivityLike, Long> {

  List<PlatformActivityLike> findByActivity(PlatformActivity activity);

  Optional<PlatformActivityLike> findByActivityIdAndUserId(Long activityId, Long userId);

  boolean existsByActivityIdAndUserId(Long activityId, Long userId);
}
