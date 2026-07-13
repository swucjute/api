package com.swucjute.api.domain.worship.repository;

import com.swucjute.api.domain.worship.entity.Worship;
import com.swucjute.api.domain.worship.entity.WorshipSummaryLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorshipSummaryLogRepository extends JpaRepository<WorshipSummaryLog, Long> {

  List<WorshipSummaryLog> findByWorshipOrderByCreatedAtDesc(Worship worship);
}
