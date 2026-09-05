package com.swucjute.api.domain.worship.dto;

import com.swucjute.api.domain.worship.entity.Worship;
import com.swucjute.api.domain.worship.entity.WorshipStatus;
import java.time.LocalDateTime;

/** 예배 목록/다시보기 조회 응답 (경량). */
public record WorshipListItemResponse(
    Long id,
    String sermonTitle,
    LocalDateTime worshipAt,
    String preacherName,
    String youtubeVideoId,
    WorshipStatus status) {

  public static WorshipListItemResponse of(Worship worship) {
    return new WorshipListItemResponse(
        worship.getId(),
        worship.getSermonTitle(),
        worship.getWorshipAt(),
        worship.getPreacherName(),
        worship.getYoutubeVideoId(),
        worship.getStatus());
  }
}
