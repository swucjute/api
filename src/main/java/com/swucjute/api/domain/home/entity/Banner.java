package com.swucjute.api.domain.home.entity;

import com.swucjute.api.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    name = "banners",
    indexes = {
      @Index(name = "idx_banners_community_post_id", columnList = "community_post_id"),
      @Index(name = "idx_banners_sort_order", columnList = "sort_order"),
      @Index(name = "idx_banners_active", columnList = "is_active"),
      @Index(name = "idx_banners_display_at", columnList = "display_start_at,display_end_at"),
      @Index(name = "idx_banners_deleted_at", columnList = "deleted_at")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Banner extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "community_post_id")
  private Long communityPostId;

  @Column(name = "image_url", nullable = false, length = 500)
  private String imageUrl;

  @Column(name = "landing_url", length = 500)
  private String landingUrl;

  @Column(name = "sort_order", nullable = false)
  private Short sortOrder = 1;

  @Column(name = "is_active", nullable = false)
  private boolean active = true;

  @Column(name = "display_start_at")
  private LocalDateTime displayStartAt;

  @Column(name = "display_end_at")
  private LocalDateTime displayEndAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
