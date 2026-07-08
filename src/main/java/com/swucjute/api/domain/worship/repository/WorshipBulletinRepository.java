package com.swucjute.api.domain.worship.repository;

import com.swucjute.api.domain.worship.entity.Worship;
import com.swucjute.api.domain.worship.entity.WorshipBulletin;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorshipBulletinRepository extends JpaRepository<WorshipBulletin, Long> {

  List<WorshipBulletin> findByWorshipAndDeletedAtIsNullOrderBySortOrderAsc(Worship worship);
}
