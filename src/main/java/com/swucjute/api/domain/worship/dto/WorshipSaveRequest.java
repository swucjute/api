package com.swucjute.api.domain.worship.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record WorshipSaveRequest(
    @NotBlank(message = "sermonTitle는 필수입니다")
        @Size(max = 150, message = "sermonTitle는 150자 이하여야 합니다")
        String sermonTitle,
    LocalDateTime worshipAt,
    @Size(max = 50, message = "preacherName는 50자 이하여야 합니다") String preacherName,
    @Size(max = 100, message = "verseReference는 100자 이하여야 합니다") String verseReference,
    String verseText,
    @Size(max = 500, message = "youtubeUrl은 500자 이하여야 합니다") String youtubeUrl,
    String status,
    @Valid List<WorshipBulletinRequest> bulletins,
    @Valid List<WorshipPraiseRequest> praises,
    @Valid List<WorshipAnnouncementRequest> announcements) {

  public record WorshipBulletinRequest(
      Integer sortOrder,
      @NotBlank(message = "imageUrl은 필수입니다") @Size(max = 500, message = "imageUrl은 500자 이하여야 합니다")
          String imageUrl,
      @NotBlank(message = "mimeType는 필수입니다") @Size(max = 50, message = "mimeType는 50자 이하여야 합니다")
          String mimeType,
      @Size(max = 200, message = "fileName는 200자 이하여야 합니다") String fileName) {}

  public record WorshipPraiseRequest(
      Integer sortOrder,
      @NotBlank(message = "title는 필수입니다") @Size(max = 200, message = "title는 200자 이하여야 합니다")
          String title,
      @Size(max = 200, message = "artist는 200자 이하여야 합니다") String artist,
      @Size(max = 50, message = "youtubeVideoId는 50자 이하여야 합니다") String youtubeVideoId) {}

  public record WorshipAnnouncementRequest(
      Integer sortOrder,
      @NotBlank(message = "title는 필수입니다") @Size(max = 200, message = "title는 200자 이하여야 합니다")
          String title,
      String content,
      @Size(max = 500, message = "linkUrl은 500자 이하여야 합니다") String linkUrl,
      @Size(max = 100, message = "linkLabel은 100자 이하여야 합니다") String linkLabel,
      Boolean afterServiceEvent) {}
}
