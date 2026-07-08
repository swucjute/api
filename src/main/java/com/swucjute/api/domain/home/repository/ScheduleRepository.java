package com.swucjute.api.domain.home.repository;

import com.swucjute.api.domain.home.entity.Schedule;
import com.swucjute.api.domain.home.entity.ScheduleType;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

  Page<Schedule> findByScheduleTypeAndStartsAtBetweenAndDeletedAtIsNull(
      ScheduleType scheduleType, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
