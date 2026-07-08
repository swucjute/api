package com.swucjute.api.domain.home.repository;

import com.swucjute.api.domain.home.entity.Banner;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {

  List<Banner> findByActiveTrueAndDeletedAtIsNullOrderBySortOrderAsc();
}
