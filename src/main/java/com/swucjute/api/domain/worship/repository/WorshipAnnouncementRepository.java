package com.swucjute.api.domain.worship.repository;

import com.swucjute.api.domain.worship.entity.Worship;
import com.swucjute.api.domain.worship.entity.WorshipAnnouncement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorshipAnnouncementRepository extends JpaRepository<WorshipAnnouncement, Long> {

  List<WorshipAnnouncement> findByWorshipAndDeletedAtIsNullOrderBySortOrderAsc(Worship worship);
}
