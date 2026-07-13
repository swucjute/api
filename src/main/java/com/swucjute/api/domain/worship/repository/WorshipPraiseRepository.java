package com.swucjute.api.domain.worship.repository;

import com.swucjute.api.domain.worship.entity.Worship;
import com.swucjute.api.domain.worship.entity.WorshipPraise;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorshipPraiseRepository extends JpaRepository<WorshipPraise, Long> {

  List<WorshipPraise> findByWorshipAndDeletedAtIsNullOrderBySortOrderAsc(Worship worship);
}
