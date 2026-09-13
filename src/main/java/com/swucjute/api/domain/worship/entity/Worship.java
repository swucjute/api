package com.swucjute.api.domain.worship.entity;

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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "worships",
    indexes = {
      @Index(name = "idx_worships_sermon_title", columnList = "sermon_title"),
      @Index(name = "idx_worships_worship_at", columnList = "worship_at"),
      @Index(name = "idx_worships_youtube_video_id", columnList = "youtube_video_id"),
      @Index(name = "idx_worships_status_worship_at", columnList = "status,worship_at"),
      @Index(name = "idx_worships_deleted_at", columnList = "deleted_at")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Worship extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "sermon_title", nullable = false, length = 150)
  private String sermonTitle;

  @Column(name = "worship_at", nullable = false)
  private LocalDateTime worshipAt;

  @Column(name = "preacher_name", length = 50)
  private String preacherName;

  @Column(name = "verse_reference", length = 100)
  private String verseReference;

  @Column(name = "verse_text", columnDefinition = "text")
  private String verseText;

  @Column(name = "youtube_url", length = 500)
  private String youtubeUrl;

  @Column(name = "youtube_video_id", length = 50)
  private String youtubeVideoId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private WorshipStatus status = WorshipStatus.PUBLISHED;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  private Worship(
      String sermonTitle,
      LocalDateTime worshipAt,
      String preacherName,
      String verseReference,
      String verseText,
      String youtubeUrl,
      String youtubeVideoId,
      WorshipStatus status) {
    this.sermonTitle = sermonTitle;
    this.worshipAt = worshipAt;
    this.preacherName = preacherName;
    this.verseReference = verseReference;
    this.verseText = verseText;
    this.youtubeUrl = youtubeUrl;
    this.youtubeVideoId = youtubeVideoId;
    this.status = status;
  }

  /** 예배 등록. */
  public static Worship create(
      String sermonTitle,
      LocalDateTime worshipAt,
      String preacherName,
      String verseReference,
      String verseText,
      String youtubeUrl,
      String youtubeVideoId,
      WorshipStatus status) {
    return new Worship(
        sermonTitle,
        worshipAt,
        preacherName,
        verseReference,
        verseText,
        youtubeUrl,
        youtubeVideoId,
        status);
  }

  /** 예배 전체 수정. */
  public void update(
      String sermonTitle,
      LocalDateTime worshipAt,
      String preacherName,
      String verseReference,
      String verseText,
      String youtubeUrl,
      String youtubeVideoId,
      WorshipStatus status) {
    this.sermonTitle = sermonTitle;
    this.worshipAt = worshipAt;
    this.preacherName = preacherName;
    this.verseReference = verseReference;
    this.verseText = verseText;
    this.youtubeUrl = youtubeUrl;
    this.youtubeVideoId = youtubeVideoId;
    this.status = status;
  }

  /** 예배 삭제 (soft delete). */
  public void softDelete(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }
}
