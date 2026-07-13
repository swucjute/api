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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "worship_praises",
    indexes = {
      @Index(name = "idx_worship_praises_worship_order", columnList = "worship_id,sort_order"),
      @Index(name = "idx_worship_praises_deleted_at", columnList = "deleted_at")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorshipPraise extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "worship_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_worship_praises_worship"))
  private Worship worship;

  @Column(name = "sort_order", nullable = false)
  private Short sortOrder = 1;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(length = 200)
  private String artist;

  @Column(name = "youtube_video_id", length = 50)
  private String youtubeVideoId;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
