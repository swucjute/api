package com.swucjute.api.domain.auth.entity;

import com.swucjute.api.domain.member.entity.Member;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "refresh_tokens",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_refresh_tokens_member_id", columnNames = "member_id"),
      @UniqueConstraint(name = "uk_refresh_tokens_token_hash", columnNames = "token_hash")
    },
    indexes = {
      @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at"),
      @Index(name = "idx_refresh_tokens_revoked_at", columnList = "revoked_at")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "member_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_refresh_tokens_member"))
  private Member member;

  @Column(name = "token_hash", nullable = false, length = 255)
  private String tokenHash;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "revoked_at")
  private LocalDateTime revokedAt;
}
