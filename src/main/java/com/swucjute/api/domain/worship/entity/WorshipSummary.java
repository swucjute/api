package com.swucjute.api.domain.worship.entity;

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
    name = "worship_summaries",
    uniqueConstraints =
        @UniqueConstraint(name = "uk_worship_summaries_worship_id", columnNames = "worship_id"),
    indexes = {
      @Index(name = "idx_worship_summaries_transcript_doc_id", columnList = "transcript_doc_id"),
      @Index(name = "idx_worship_summaries_status", columnList = "status")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorshipSummary extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "worship_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_worship_summaries_worship"))
  private Worship worship;

  @Column(name = "sermon_summary", columnDefinition = "text")
  private String sermonSummary;

  @Column(name = "prayer_summary", columnDefinition = "text")
  private String prayerSummary;

  @Column(name = "prayer_topics", columnDefinition = "json")
  private String prayerTopics;

  @Column(name = "transcript_doc_id", length = 50)
  private String transcriptDocId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SummaryStatus status = SummaryStatus.PENDING;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private SummarySource source;

  @Column(length = 50)
  private String model;

  @Column(name = "error_message", columnDefinition = "text")
  private String errorMessage;

  @Column(name = "requested_at")
  private LocalDateTime requestedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;
}
