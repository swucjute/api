package com.swucjute.api.domain.member.entity;

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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "members",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_members_provider_user",
          columnNames = {"provider", "provider_user_id"}),
      @UniqueConstraint(name = "uk_members_email", columnNames = "email")
    },
    indexes = {
      @Index(name = "idx_members_role", columnList = "member_role"),
      @Index(name = "idx_members_status", columnList = "status"),
      @Index(name = "idx_members_deleted_at", columnList = "deleted_at")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private AuthProvider provider = AuthProvider.KAKAO;

  @Column(name = "provider_user_id", nullable = false, length = 100)
  private String providerUserId;

  @Column(length = 100)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "member_role", nullable = false, length = 30)
  private MemberRole memberRole = MemberRole.USER;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private MemberStatus status = MemberStatus.PENDING;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  private Member(AuthProvider provider, String providerUserId, String email) {
    this.provider = provider;
    this.providerUserId = providerUserId;
    this.email = email;
    this.memberRole = MemberRole.USER;
    this.status = MemberStatus.PENDING;
  }

  /** 카카오 최초 로그인 시 프로필 없이 인증 정보만 가진 회원을 생성한다 (USER / PENDING). */
  public static Member ofKakao(String providerUserId, String email) {
    return new Member(AuthProvider.KAKAO, providerUserId, email);
  }

  /** 로그인 시각 갱신. */
  public void updateLastLogin(LocalDateTime loginAt) {
    this.lastLoginAt = loginAt;
  }

  /** 회원 상태 변경 (관리자 승인/비활성화 등). */
  public void changeStatus(MemberStatus status) {
    this.status = status;
  }

  /** 회원 탈퇴: 상태를 WITHDRAWN으로 바꾸고 삭제 시각을 기록한다 (soft delete). */
  public void withdraw(LocalDateTime withdrawnAt) {
    this.status = MemberStatus.WITHDRAWN;
    this.deletedAt = withdrawnAt;
  }

  public boolean isWithdrawn() {
    return this.deletedAt != null || this.status == MemberStatus.WITHDRAWN;
  }
}
