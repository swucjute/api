package com.swucjute.api.domain.worship.dto;

import com.swucjute.api.domain.worship.entity.Worship;
import com.swucjute.api.domain.worship.entity.WorshipAnnouncement;
import com.swucjute.api.domain.worship.entity.WorshipBulletin;
import com.swucjute.api.domain.worship.entity.WorshipPraise;
import com.swucjute.api.domain.worship.entity.WorshipStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** 예배 상세 조회 응답 (주보/찬양/공지 포함). */
public record WorshipDetailResponse(
    Long id,
    String sermonTitle,
    LocalDateTime worshipAt,
    String preacherName,
    String verseReference,
    String verseText,
    String youtubeUrl,
    String youtubeVideoId,
    WorshipStatus status,
    boolean hasSummary,
    List<BulletinInfo> bulletins,
    List<PraiseInfo> praises,
    List<AnnouncementInfo> announcements,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public record BulletinInfo(
      Long id, Integer sortOrder, String imageUrl, String mimeType, String fileName) {

    private static BulletinInfo of(WorshipBulletin b) {
      return new BulletinInfo(
          b.getId(), (int) b.getSortOrder(), b.getImageUrl(), b.getMimeType(), b.getFileName());
    }
  }

  public record PraiseInfo(
      Long id, Integer sortOrder, String title, String artist, String youtubeVideoId) {

    private static PraiseInfo of(WorshipPraise p) {
      return new PraiseInfo(
          p.getId(), (int) p.getSortOrder(), p.getTitle(), p.getArtist(), p.getYoutubeVideoId());
    }
  }

  public record AnnouncementInfo(
      Long id,
      Integer sortOrder,
      String title,
      String content,
      String linkUrl,
      String linkLabel,
      boolean afterServiceEvent,
      LocalDate displayStartDate,
      LocalDate displayEndDate) {

    private static AnnouncementInfo of(WorshipAnnouncement a) {
      return new AnnouncementInfo(
          a.getId(),
          (int) a.getSortOrder(),
          a.getTitle(),
          a.getContent(),
          a.getLinkUrl(),
          a.getLinkLabel(),
          a.isAfterServiceEvent(),
          a.getDisplayStartDate(),
          a.getDisplayEndDate());
    }
  }

  public static WorshipDetailResponse of(
      Worship worship,
      List<WorshipBulletin> bulletins,
      List<WorshipPraise> praises,
      List<WorshipAnnouncement> announcements,
      boolean hasSummary) {
    return new WorshipDetailResponse(
        worship.getId(),
        worship.getSermonTitle(),
        worship.getWorshipAt(),
        worship.getPreacherName(),
        worship.getVerseReference(),
        worship.getVerseText(),
        worship.getYoutubeUrl(),
        worship.getYoutubeVideoId(),
        worship.getStatus(),
        hasSummary,
        bulletins.stream().map(BulletinInfo::of).toList(),
        praises.stream().map(PraiseInfo::of).toList(),
        announcements.stream().map(AnnouncementInfo::of).toList(),
        worship.getCreatedAt(),
        worship.getUpdatedAt());
  }
}
