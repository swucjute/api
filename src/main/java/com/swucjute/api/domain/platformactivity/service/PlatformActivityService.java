package com.swucjute.api.domain.platformactivity.service;

import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.member.entity.MemberProfile;
import com.swucjute.api.domain.member.repository.MemberProfileRepository;
import com.swucjute.api.domain.member.repository.MemberRepository;
import com.swucjute.api.domain.platform.entity.Platform;
import com.swucjute.api.domain.platform.repository.PlatformRepository;
import com.swucjute.api.domain.platformactivity.dto.request.PlatformActivityCommentRequest;
import com.swucjute.api.domain.platformactivity.dto.request.PlatformActivityCreateRequest;
import com.swucjute.api.domain.platformactivity.dto.request.PlatformActivityUpdateRequest;
import com.swucjute.api.domain.platformactivity.dto.response.PlatformActivityCommentResponse;
import com.swucjute.api.domain.platformactivity.dto.response.PlatformActivityDetailResponse;
import com.swucjute.api.domain.platformactivity.dto.response.PlatformActivitySummaryResponse;
import com.swucjute.api.domain.platformactivity.entity.PlatformActivity;
import com.swucjute.api.domain.platformactivity.entity.PlatformActivityComment;
import com.swucjute.api.domain.platformactivity.repository.PlatformActivityCommentRepository;
import com.swucjute.api.domain.platformactivity.repository.PlatformActivityRepository;
import com.swucjute.api.global.common.PageResponse;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlatformActivityService {

  private static final String NOT_DELETED = "N";

  private final PlatformActivityRepository activityRepository;
  private final PlatformActivityCommentRepository commentRepository;
  private final MemberRepository memberRepository;
  private final MemberProfileRepository memberProfileRepository;
  private final PlatformRepository platformRepository;

  public PageResponse<PlatformActivitySummaryResponse> getActivities(int page, int size) {
    Page<PlatformActivity> activities =
        activityRepository.findByDeleteYn(
            NOT_DELETED, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate")));

    Map<Long, MemberProfile> profiles =
        profilesByMembers(activities.getContent().stream().map(PlatformActivity::getUser).toList());

    Page<PlatformActivitySummaryResponse> mapped =
        activities.map(activity -> toSummary(activity, profiles));
    return PageResponse.of(mapped);
  }

  public PlatformActivityDetailResponse getActivity(Long activityId) {
    PlatformActivity activity = findActivity(activityId);
    Map<Long, MemberProfile> profiles = profilesByMembers(List.of(activity.getUser()));

    List<PlatformActivityComment> comments =
        commentRepository.findByActivityAndDeleteYnOrderByCreateDateAsc(activity, NOT_DELETED);
    Map<Long, MemberProfile> profilesWithComments =
        profilesByMembers(
            java.util.stream.Stream.concat(
                    java.util.stream.Stream.of(activity.getUser()),
                    comments.stream().map(PlatformActivityComment::getUser))
                .toList());

    List<PlatformActivityCommentResponse> commentResponses =
        comments.stream().map(comment -> toComment(comment, profilesWithComments)).toList();

    return new PlatformActivityDetailResponse(
        activity.getId(),
        activity.getImageUrl(),
        activity.getContent(),
        writerName(activity.getUser(), profilesWithComments),
        activity.getCreateDate(),
        activity.getLikeCount(),
        activity.getCommentCount(),
        commentResponses);
  }

  @Transactional
  public PlatformActivitySummaryResponse create(
      Long memberId, PlatformActivityCreateRequest request) {
    Member member = findMember(memberId);
    Platform platform =
        platformRepository
            .findByIdAndDeletedAtIsNull(request.platformId())
            .orElseThrow(() -> new CustomException(ErrorCode.PLATFORM_NOT_FOUND));

    PlatformActivity activity =
        PlatformActivity.create(platform, member, request.imageUrl(), request.content());
    activityRepository.save(activity);

    MemberProfile profile = memberProfileRepository.findByMember(member).orElse(null);
    return toSummary(activity, profileMap(memberId, profile));
  }

  @Transactional
  public PlatformActivitySummaryResponse update(
      Long activityId, Long memberId, PlatformActivityUpdateRequest request) {
    PlatformActivity activity = findActivityByWriter(activityId, memberId);
    activity.update(request.imageUrl(), request.content());

    MemberProfile profile = memberProfileRepository.findByMember(activity.getUser()).orElse(null);
    return toSummary(activity, profileMap(memberId, profile));
  }

  @Transactional
  public Object delete(Long activityId, Long memberId) {
    PlatformActivity activity = findActivityByWriter(activityId, memberId);
    activity.softDelete();
    return null;
  }

  public Object addLike(Long activityId, Long memberId) {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }

  public Object removeLike(Long activityId, Long memberId) {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }

  public Object getLikes(Long activityId) {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }

  public Object createComment(
      Long activityId, Long memberId, PlatformActivityCommentRequest request) {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }

  public Object updateComment(
      Long commentId, Long memberId, PlatformActivityCommentRequest request) {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }

  public Object deleteComment(Long commentId, Long memberId) {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }

  private PlatformActivity findActivity(Long activityId) {
    return activityRepository
        .findByIdAndDeleteYn(activityId, NOT_DELETED)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
  }

  private PlatformActivity findActivityByWriter(Long activityId, Long memberId) {
    return activityRepository
        .findByIdAndUserIdAndDeleteYn(activityId, memberId, NOT_DELETED)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
  }

  private Member findMember(Long memberId) {
    return memberRepository
        .findById(memberId)
        .filter(member -> !member.isWithdrawn())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
  }

  private Map<Long, MemberProfile> profilesByMembers(List<Member> members) {
    if (members.isEmpty()) {
      return Map.of();
    }
    return memberProfileRepository.findByMemberIn(members).stream()
        .collect(Collectors.toMap(profile -> profile.getMember().getId(), Function.identity()));
  }

  private PlatformActivitySummaryResponse toSummary(
      PlatformActivity activity, Map<Long, MemberProfile> profiles) {
    return new PlatformActivitySummaryResponse(
        activity.getId(),
        activity.getImageUrl(),
        activity.getContent(),
        writerName(activity.getUser(), profiles),
        activity.getCreateDate(),
        activity.getLikeCount(),
        activity.getCommentCount());
  }

  private PlatformActivityCommentResponse toComment(
      PlatformActivityComment comment, Map<Long, MemberProfile> profiles) {
    return new PlatformActivityCommentResponse(
        comment.getId(),
        writerName(comment.getUser(), profiles),
        comment.getContent(),
        comment.getCreateDate());
  }

  private Map<Long, MemberProfile> profileMap(Long memberId, MemberProfile profile) {
    return java.util.Collections.singletonMap(memberId, profile);
  }

  private String writerName(Member member, Map<Long, MemberProfile> profiles) {
    MemberProfile profile = profiles.get(member.getId());
    return profile == null ? null : profile.getName();
  }
}
