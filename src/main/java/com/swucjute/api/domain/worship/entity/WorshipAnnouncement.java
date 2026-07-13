package com.swucjute.api.domain.worship.entity;

import com.swucjute.api.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "worship_announcements",
    indexes = {
      @Index(
          name = "idx_worship_announcements_worship_order",
          columnList = "worship_id,sort_order"),
      @Index(
          name = "idx_worship_announcements_display_date",
          columnList = "display_start_date,display_end_date"),
      @Index(name = "idx_worship_announcements_deleted_at", columnList = "deleted_at")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorshipAnnouncement extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "worship_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_worship_announcements_worship"))
  private Worship worship;

  @Column(name = "sort_order", nullable = false)
  private Short sortOrder = 1;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(columnDefinition = "text")
  private String content;

  @Column(name = "link_url", length = 500)
  private String linkUrl;

  @Column(name = "link_label", length = 100)
  private String linkLabel;

  @Column(name = "is_after_service_event", nullable = false)
  private boolean afterServiceEvent;

  @Column(name = "display_start_date")
  private LocalDate displayStartDate;

  @Column(name = "display_end_date")
  private LocalDate displayEndDate;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
