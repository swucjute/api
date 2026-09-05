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

  private PlatformMember(
      Platform platform,
      Member member,
      PlatformMemberRole role,
      PlatformMemberStatus status,
      LocalDateTime requestedAt,
      LocalDateTime approvedAt) {
    this.platform = platform;
    this.member = member;
    this.role = role;
    this.status = status;
    this.requestedAt = requestedAt;
    this.approvedAt = approvedAt;
  }

  /** 플랫폼 생성 시, 생성자를 OWNER/APPROVED 상태로 즉시 등록한다. */
  public static PlatformMember createOwner(Platform platform, Member member, LocalDateTime now) {
    return new PlatformMember(
        platform, member, PlatformMemberRole.OWNER, PlatformMemberStatus.APPROVED, now, now);
  }

  /** 신규 가입 신청. role=MEMBER, status=PENDING으로 시작한다. */
  public static PlatformMember applyAsMember(Platform platform, Member member, LocalDateTime now) {
    return new PlatformMember(
        platform, member, PlatformMemberRole.MEMBER, PlatformMemberStatus.PENDING, now, null);
  }

  /** REJECTED/WITHDRAWN 상태였던 기존 신청 row를 재신청 처리한다. */
  public void reapply(LocalDateTime now) {
    this.status = PlatformMemberStatus.PENDING;
    this.requestedAt = now;
    this.approvedAt = null;
    this.rejectedReason = null;
  }

  /** 관리자(OWNER/ADMIN)의 멤버 상태 변경. APPROVED는 승인 시각을, REJECTED는 거절 사유를 함께 기록한다. */
  public void changeStatus(PlatformMemberStatus status, String rejectedReason, LocalDateTime now) {
    this.status = status;
    if (status == PlatformMemberStatus.APPROVED) {
      this.approvedAt = now;
      this.rejectedReason = null;
    } else if (status == PlatformMemberStatus.REJECTED) {
      this.rejectedReason = rejectedReason;
    }
  }

  /** 본인 탈퇴. */
  public void withdraw() {
    this.status = PlatformMemberStatus.WITHDRAWN;
  }

  public boolean isOwner() {
    return this.role == PlatformMemberRole.OWNER;
  }
}
