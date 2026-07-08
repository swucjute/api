package com.swucjute.api.domain.platform.entity;

import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    name = "platforms",
    indexes = {
      @Index(name = "idx_platforms_owner_member_id", columnList = "owner_member_id"),
      @Index(name = "idx_platforms_title", columnList = "title"),
      @Index(name = "idx_platforms_starts_at", columnList = "starts_at"),
      @Index(name = "idx_platforms_ends_at", columnList = "ends_at"),
      @Index(name = "idx_platforms_approval_status", columnList = "approval_status"),
      @Index(name = "idx_platforms_operating_status", columnList = "operating_status"),
      @Index(name = "idx_platforms_deleted_at", columnList = "deleted_at")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Platform extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "owner_member_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_platforms_owner_member"))
  private Member ownerMember;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(name = "schedule_text", length = 200)
  private String scheduleText;

  @Column(name = "starts_at")
  private LocalDateTime startsAt;

  @Column(name = "ends_at")
  private LocalDateTime endsAt;

  @Column(length = 100)
  private String location;

  @Column(columnDefinition = "text")
  private String content;

  @Column(columnDefinition = "text")
  private String purpose;

  @Column(columnDefinition = "text")
  private String etc;

  @Column(name = "poster_url", length = 500)
  private String posterUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "approval_status", nullable = false, length = 20)
  private PlatformApprovalStatus approvalStatus = PlatformApprovalStatus.PENDING;

  @Enumerated(EnumType.STRING)
  @Column(name = "operating_status", nullable = false, length = 20)
  private PlatformOperatingStatus operatingStatus = PlatformOperatingStatus.RECRUITING;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
