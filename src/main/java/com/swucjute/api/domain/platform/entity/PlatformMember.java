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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "platform_members",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_platform_members_platform_member",
            columnNames = {"platform_id", "member_id"}),
    indexes = {
      @Index(name = "idx_platform_members_member_id", columnList = "member_id"),
      @Index(name = "idx_platform_members_status", columnList = "status")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlatformMember extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "platform_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_platform_members_platform"))
  private Platform platform;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "member_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_platform_members_member"))
  private Member member;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PlatformMemberRole role = PlatformMemberRole.MEMBER;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PlatformMemberStatus status = PlatformMemberStatus.PENDING;

  @Column(name = "requested_at", nullable = false)
  private LocalDateTime requestedAt;

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;

  @Column(name = "rejected_reason", length = 200)
  private String rejectedReason;
}
