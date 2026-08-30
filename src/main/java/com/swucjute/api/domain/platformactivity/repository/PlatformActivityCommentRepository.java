package com.swucjute.api.domain.platformactivity.repository;

import com.swucjute.api.domain.platformactivity.entity.PlatformActivity;
import com.swucjute.api.domain.platformactivity.entity.PlatformActivityComment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformActivityCommentRepository
    extends JpaRepository<PlatformActivityComment, Long> {

  List<PlatformActivityComment> findByActivityAndDeleteYnOrderByCreateDateAsc(
      PlatformActivity activity, String deleteYn);

  Optional<PlatformActivityComment> findByIdAndUserIdAndDeleteYn(
      Long id, Long userId, String deleteYn);
}
