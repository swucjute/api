package com.swucjute.api.domain.worship.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WorshipSaveRequest(
    String sermonTitle,
    LocalDateTime worshipAt,
    String preacherName,
    String verseReference,
    String verseText,
    String youtubeUrl,
    String status,
    List<WorshipBulletinRequest> bulletins,
    List<WorshipPraiseRequest> praises,
    List<WorshipAnnouncementRequest> announcements) {

  public record WorshipBulletinRequest(
      Integer sortOrder, String imageUrl, String mimeType, String fileName) {}

  public record WorshipPraiseRequest(
      Integer sortOrder, String title, String artist, String youtubeVideoId) {}

  public record WorshipAnnouncementRequest(
      Integer sortOrder,
      String title,
      String content,
      String linkUrl,
      String linkLabel,
      Boolean afterServiceEvent) {}
}
