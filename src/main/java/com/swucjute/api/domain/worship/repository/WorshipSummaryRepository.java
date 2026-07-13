package com.swucjute.api.domain.worship.repository;

import com.swucjute.api.domain.worship.entity.Worship;
import com.swucjute.api.domain.worship.entity.WorshipSummary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorshipSummaryRepository extends JpaRepository<WorshipSummary, Long> {

  Optional<WorshipSummary> findByWorship(Worship worship);

  Optional<WorshipSummary> findByWorshipId(Long worshipId);
}
