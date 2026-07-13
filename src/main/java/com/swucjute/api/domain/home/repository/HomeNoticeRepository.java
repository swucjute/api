package com.swucjute.api.domain.home.repository;

import com.swucjute.api.domain.home.entity.HomeNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeNoticeRepository extends JpaRepository<HomeNotice, Long> {

  Page<HomeNotice> findByActiveTrueAndDeletedAtIsNull(Pageable pageable);
}
