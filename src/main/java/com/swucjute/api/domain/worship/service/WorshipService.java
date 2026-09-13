package com.swucjute.api.domain.worship.service;

import com.swucjute.api.domain.worship.dto.WorshipDetailResponse;
import com.swucjute.api.domain.worship.dto.WorshipListItemResponse;
import com.swucjute.api.domain.worship.dto.WorshipSaveRequest;
import com.swucjute.api.domain.worship.entity.Worship;
import com.swucjute.api.domain.worship.entity.WorshipAnnouncement;
import com.swucjute.api.domain.worship.entity.WorshipBulletin;
import com.swucjute.api.domain.worship.entity.WorshipPraise;
import com.swucjute.api.domain.worship.entity.WorshipStatus;
import com.swucjute.api.domain.worship.repository.WorshipAnnouncementRepository;
import com.swucjute.api.domain.worship.repository.WorshipBulletinRepository;
import com.swucjute.api.domain.worship.repository.WorshipPraiseRepository;
import com.swucjute.api.domain.worship.repository.WorshipRepository;
import com.swucjute.api.domain.worship.repository.WorshipSummaryRepository;
import com.swucjute.api.global.common.PageResponse;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorshipService {

  /** 의존성 필드 */
  private static final Pattern YOUTUBE_ID_PATTERN =
      Pattern.compile(
          "(?:youtu\\.be/|youtube\\.com/(?:watch\\?v=|embed/|shorts/))([a-zA-Z0-9_-]{6,15})");

  private final WorshipRepository worshipRepository;
  private final WorshipBulletinRepository worshipBulletinRepository;
  private final WorshipPraiseRepository worshipPraiseRepository;
  private final WorshipAnnouncementRepository worshipAnnouncementRepository;
  private final WorshipSummaryRepository worshipSummaryRepository;

  /** Controller-API */
  /* 이번 주/최신 예배 조회 */
  public WorshipDetailResponse getCurrent() {
    Worship worship =
        worshipRepository
            .findFirstByStatusAndDeletedAtIsNullOrderByWorshipAtDesc(WorshipStatus.PUBLISHED)
            .orElseThrow(() -> new CustomException(ErrorCode.WORSHIP_NOT_FOUND));
    return toDetailResponse(worship);
  }

  /*예배 목록/다시보기 조회*/
  public PageResponse<WorshipListItemResponse> getWorships(
      int page, int size, LocalDate from, LocalDate to, String keyword) {
    LocalDateTime fromAt = from == null ? null : from.atStartOfDay();
    LocalDateTime toAt = to == null ? null : to.plusDays(1).atStartOfDay();
    String keywordFilter = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

    Pageable pageable = PageRequest.of(page, size);
    Page<Worship> worships =
        worshipRepository.searchWorships(fromAt, toAt, keywordFilter, pageable);
    return PageResponse.of(worships.map(WorshipListItemResponse::of));
  }

  /*예배 상세 조회*/
  public WorshipDetailResponse getWorship(Long worshipId) {
    return toDetailResponse(findWorship(worshipId));
  }

  /*예배 등록*/
  @Transactional
  public WorshipDetailResponse create(WorshipSaveRequest request) {
    Worship worship =
        Worship.create(
            request.sermonTitle(),
            request.worshipAt(),
            request.preacherName(),
            request.verseReference(),
            request.verseText(),
            request.youtubeUrl(),
            extractYoutubeVideoId(request.youtubeUrl()),
            parseStatus(request.status()));
    worshipRepository.save(worship);

    saveBulletins(worship, request.bulletins());
    savePraises(worship, request.praises());
    saveAnnouncements(worship, request.announcements());

    return toDetailResponse(worship);
  }

  /*예배 수정*/
  @Transactional
  public WorshipDetailResponse update(Long worshipId, WorshipSaveRequest request) {
    Worship worship = findWorship(worshipId);
    worship.update(
        request.sermonTitle(),
        request.worshipAt(),
        request.preacherName(),
        request.verseReference(),
        request.verseText(),
        request.youtubeUrl(),
        extractYoutubeVideoId(request.youtubeUrl()),
        parseStatus(request.status()));

    replaceBulletins(worship, request.bulletins());
    replacePraises(worship, request.praises());
    replaceAnnouncements(worship, request.announcements());

    return toDetailResponse(worship);
  }

  /*예배 삭제*/
  @Transactional
  public Void delete(Long worshipId) {
    findWorship(worshipId).softDelete(LocalDateTime.now());
    return null;
  }

  /** 내부 메서드 */
  private Worship findWorship(Long worshipId) {
    return worshipRepository
        .findById(worshipId)
        .filter(w -> w.getDeletedAt() == null)
        .orElseThrow(() -> new CustomException(ErrorCode.WORSHIP_NOT_FOUND));
  }

  private WorshipDetailResponse toDetailResponse(Worship worship) {
    List<WorshipBulletin> bulletins =
        worshipBulletinRepository.findByWorshipAndDeletedAtIsNullOrderBySortOrderAsc(worship);
    List<WorshipPraise> praises =
        worshipPraiseRepository.findByWorshipAndDeletedAtIsNullOrderBySortOrderAsc(worship);
    List<WorshipAnnouncement> announcements =
        worshipAnnouncementRepository.findByWorshipAndDeletedAtIsNullOrderBySortOrderAsc(worship);
    boolean hasSummary = worshipSummaryRepository.findByWorshipId(worship.getId()).isPresent();
    return WorshipDetailResponse.of(worship, bulletins, praises, announcements, hasSummary);
  }

  private void saveBulletins(
      Worship worship, List<WorshipSaveRequest.WorshipBulletinRequest> requests) {
    if (requests == null) {
      return;
    }
    for (WorshipSaveRequest.WorshipBulletinRequest r : requests) {
      worshipBulletinRepository.save(
          WorshipBulletin.create(
              worship, toSortOrder(r.sortOrder()), r.imageUrl(), r.mimeType(), r.fileName()));
    }
  }

  private void savePraises(
      Worship worship, List<WorshipSaveRequest.WorshipPraiseRequest> requests) {
    if (requests == null) {
      return;
    }
    for (WorshipSaveRequest.WorshipPraiseRequest r : requests) {
      worshipPraiseRepository.save(
          WorshipPraise.create(
              worship, toSortOrder(r.sortOrder()), r.title(), r.artist(), r.youtubeVideoId()));
    }
  }

  private void saveAnnouncements(
      Worship worship, List<WorshipSaveRequest.WorshipAnnouncementRequest> requests) {
    if (requests == null) {
      return;
    }
    for (WorshipSaveRequest.WorshipAnnouncementRequest r : requests) {
      worshipAnnouncementRepository.save(
          WorshipAnnouncement.create(
              worship,
              toSortOrder(r.sortOrder()),
              r.title(),
              r.content(),
              r.linkUrl(),
              r.linkLabel(),
              Boolean.TRUE.equals(r.afterServiceEvent()),
              null,
              null));
    }
  }

  private void replaceBulletins(
      Worship worship, List<WorshipSaveRequest.WorshipBulletinRequest> requests) {
    LocalDateTime now = LocalDateTime.now();
    worshipBulletinRepository
        .findByWorshipAndDeletedAtIsNullOrderBySortOrderAsc(worship)
        .forEach(b -> b.softDelete(now));
    saveBulletins(worship, requests);
  }

  private void replacePraises(
      Worship worship, List<WorshipSaveRequest.WorshipPraiseRequest> requests) {
    LocalDateTime now = LocalDateTime.now();
    worshipPraiseRepository
        .findByWorshipAndDeletedAtIsNullOrderBySortOrderAsc(worship)
        .forEach(p -> p.softDelete(now));
    savePraises(worship, requests);
  }

  private void replaceAnnouncements(
      Worship worship, List<WorshipSaveRequest.WorshipAnnouncementRequest> requests) {
    LocalDateTime now = LocalDateTime.now();
    worshipAnnouncementRepository
        .findByWorshipAndDeletedAtIsNullOrderBySortOrderAsc(worship)
        .forEach(a -> a.softDelete(now));
    saveAnnouncements(worship, requests);
  }

  private static Short toSortOrder(Integer sortOrder) {
    return sortOrder == null ? 1 : sortOrder.shortValue();
  }

  private WorshipStatus parseStatus(String status) {
    if (status == null || status.isBlank()) {
      return WorshipStatus.PUBLISHED;
    }
    try {
      return WorshipStatus.valueOf(status.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new CustomException(ErrorCode.INVALID_INPUT);
    }
  }

  private static String extractYoutubeVideoId(String youtubeUrl) {
    if (youtubeUrl == null || youtubeUrl.isBlank()) {
      return null;
    }
    Matcher matcher = YOUTUBE_ID_PATTERN.matcher(youtubeUrl);
    return matcher.find() ? matcher.group(1) : null;
  }
}
