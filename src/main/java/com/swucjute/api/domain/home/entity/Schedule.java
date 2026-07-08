package com.swucjute.api.domain.home.entity;

import com.swucjute.api.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "schedules",
    indexes = {
      @Index(name = "idx_schedules_community_post_id", columnList = "community_post_id"),
      @Index(name = "idx_schedules_title", columnList = "title"),
      @Index(name = "idx_schedules_type", columnList = "schedule_type"),
      @Index(name = "idx_schedules_starts_at", columnList = "starts_at"),
      @Index(name = "idx_schedules_ends_at", columnList = "ends_at"),
      @Index(name = "idx_schedules_deleted_at", columnList = "deleted_at")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "community_post_id")
  private Long communityPostId;

  @Column(nullable = false, length = 150)
  private String title;

  @Column(columnDefinition = "text")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "schedule_type", nullable = false, length = 20)
  private ScheduleType scheduleType = ScheduleType.ETC;

  @Column(length = 100)
  private String location;

  @Column(name = "starts_at", nullable = false)
  private LocalDateTime startsAt;

  @Column(name = "ends_at")
  private LocalDateTime endsAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
