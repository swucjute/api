package com.swucjute.api.domain.worship.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(
    name = "worship_summary_logs",
    indexes = {
      @Index(
          name = "idx_worship_summary_logs_worship_created",
          columnList = "worship_id,created_at"),
      @Index(name = "idx_worship_summary_logs_summary_id", columnList = "summary_id"),
      @Index(name = "idx_worship_summary_logs_status", columnList = "status")
    })
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorshipSummaryLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "worship_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_worship_summary_logs_worship"))
  private Worship worship;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "summary_id",
      foreignKey = @ForeignKey(name = "fk_worship_summary_logs_summary"))
  private WorshipSummary summary;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SummarySource source;

  @Column(nullable = false, length = 50)
  private String model;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SummaryStatus status;

  @Column(name = "input_tokens")
  private Long inputTokens;

  @Column(name = "output_tokens")
  private Long outputTokens;

  @Column(name = "cost_usd", precision = 12, scale = 6)
  private BigDecimal costUsd;

  @Column(name = "error_code", length = 50)
  private String errorCode;

  @Column(name = "error_message", columnDefinition = "text")
  private String errorMessage;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
