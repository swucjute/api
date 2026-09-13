package com.swucjute.api.domain.worship.repository;

import com.swucjute.api.domain.worship.entity.Worship;
import com.swucjute.api.domain.worship.entity.WorshipStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorshipRepository extends JpaRepository<Worship, Long> {

  Optional<Worship> findFirstByStatusAndDeletedAtIsNullOrderByWorshipAtDesc(WorshipStatus status);

  /**
   * 예배 목록/다시보기 검색. from/to/keyword는 모두 선택 필터이며, keyword는 설교 제목 또는 설교자명을 부분 검색한다. from은
   * 포함(inclusive), to는 배타(exclusive) 경계로 처리한다.
   */
  @Query(
      value =
          """
          select w from Worship w
          where w.deletedAt is null
            and (:from is null or w.worshipAt >= :from)
            and (:to is null or w.worshipAt < :to)
            and (:keyword is null
                 or w.sermonTitle like concat('%', :keyword, '%')
                 or w.preacherName like concat('%', :keyword, '%'))
          order by w.worshipAt desc
          """,
      countQuery =
          """
          select count(w) from Worship w
          where w.deletedAt is null
            and (:from is null or w.worshipAt >= :from)
            and (:to is null or w.worshipAt < :to)
            and (:keyword is null
                 or w.sermonTitle like concat('%', :keyword, '%')
                 or w.preacherName like concat('%', :keyword, '%'))
          """)
  Page<Worship> searchWorships(
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("keyword") String keyword,
      Pageable pageable);
}
