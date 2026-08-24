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
      @Index(name = "idx_platforms_closed_status", columnList = "closed_status"),
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

  @Column(
      name = "recruiting",
      nullable = false,
      columnDefinition = "boolean not null default false")
  private boolean recruiting = false;

  @Column(name = "operating", nullable = false, columnDefinition = "boolean not null default false")
  private boolean operating = false;

  @Enumerated(EnumType.STRING)
  @Column(name = "closed_status", length = 20)
  private PlatformClosedStatus closedStatus;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  private Platform(
      Member ownerMember,
      String title,
      String scheduleText,
      LocalDateTime startsAt,
      LocalDateTime endsAt,
      String location,
      String content,
      String purpose,
      String etc,
      String posterUrl) {
    this.ownerMember = ownerMember;
    this.title = title;
    this.scheduleText = scheduleText;
    this.startsAt = startsAt;
    this.endsAt = endsAt;
    this.location = location;
    this.content = content;
    this.purpose = purpose;
    this.etc = etc;
    this.posterUrl = posterUrl;
  }

  /**
   * 플랫폼 생성/제안. 승인상태는 항상 기본값(PENDING)에서 시작하며, 관리자만 {@link #changeApprovalStatus}로 바꿀 수 있다. 모집/운영 상태는
   * 승인 전에는 의미가 없으므로 항상 꺼진 채로 시작한다(승인되는 순간 {@link #changeApprovalStatus}가 모집을 자동으로 켠다).
   */
  public static Platform create(
      Member ownerMember,
      String title,
      String scheduleText,
      LocalDateTime startsAt,
      LocalDateTime endsAt,
      String location,
      String content,
      String purpose,
      String etc,
      String posterUrl) {
    return new Platform(
        ownerMember,
        title,
        scheduleText,
        startsAt,
        endsAt,
        location,
        content,
        purpose,
        etc,
        posterUrl);
  }

  /** 플랫폼 정보 수정. 모집/운영 상태는 이 메서드로 바뀌지 않는다({@link #startRecruiting} 등 전용 메서드 사용). */
  public void updateDetails(
      String title,
      String scheduleText,
      LocalDateTime startsAt,
      LocalDateTime endsAt,
      String location,
      String content,
      String purpose,
      String etc,
      String posterUrl) {
    this.title = title;
    this.scheduleText = scheduleText;
    this.startsAt = startsAt;
    this.endsAt = endsAt;
    this.location = location;
    this.content = content;
    this.purpose = purpose;
    this.etc = etc;
    this.posterUrl = posterUrl;
  }

  /** 삭제(soft delete): 삭제 시각만 기록한다. */
  public void softDelete(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }

  /** 관리자 승인상태 변경. 승인(APPROVED)되는 순간, 아직 종료되지 않았다면 모집을 자동으로 시작한다. */
  public void changeApprovalStatus(PlatformApprovalStatus approvalStatus) {
    this.approvalStatus = approvalStatus;
    if (approvalStatus == PlatformApprovalStatus.APPROVED && this.closedStatus == null) {
      this.recruiting = true;
    }
  }

  /** 모집 시작. 종료·취소됐던 플랫폼도 모집을 다시 시작하면 종료 상태가 풀린다. */
  public void startRecruiting() {
    this.recruiting = true;
    this.closedStatus = null;
  }

  public void stopRecruiting() {
    this.recruiting = false;
  }

  /** 운영 시작. 종료·취소됐던 플랫폼도 운영을 다시 시작하면 종료 상태가 풀린다. */
  public void startOperating() {
    this.operating = true;
    this.closedStatus = null;
  }

  public void stopOperating() {
    this.operating = false;
  }

  /** 운영 종료. 모집/운영을 모두 끈다. 이후에도 모집·운영을 다시 시작하면 되돌릴 수 있다. */
  public void finish() {
    this.recruiting = false;
    this.operating = false;
    this.closedStatus = PlatformClosedStatus.FINISHED;
  }

  /** 취소. 모집/운영을 모두 끈다. 이후에도 모집·운영을 다시 시작하면 되돌릴 수 있다. */
  public void cancel() {
    this.recruiting = false;
    this.operating = false;
    this.closedStatus = PlatformClosedStatus.CANCELLED;
  }

  public boolean isDeleted() {
    return this.deletedAt != null;
  }
}
