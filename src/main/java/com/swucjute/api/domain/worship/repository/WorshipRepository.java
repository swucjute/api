package com.swucjute.api.domain.worship.repository;

import com.swucjute.api.domain.worship.entity.Worship;
import com.swucjute.api.domain.worship.entity.WorshipStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorshipRepository extends JpaRepository<Worship, Long> {

  Optional<Worship> findFirstByStatusAndDeletedAtIsNullOrderByWorshipAtDesc(WorshipStatus status);

  Page<Worship> findByDeletedAtIsNull(Pageable pageable);
}
