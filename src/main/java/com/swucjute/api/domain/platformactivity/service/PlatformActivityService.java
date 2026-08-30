package com.swucjute.api.domain.platformactivity.service;

import com.swucjute.api.domain.platformactivity.dto.request.PlatformActivityCommentRequest;
import com.swucjute.api.domain.platformactivity.dto.request.PlatformActivityCreateRequest;
import com.swucjute.api.domain.platformactivity.dto.request.PlatformActivityUpdateRequest;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class PlatformActivityService {

  public Object getActivities(int page, int size) {
    return notImplemented();
  }

  public Object getActivity(Long activityId) {
    return notImplemented();
  }

  public Object create(Long memberId, PlatformActivityCreateRequest request) {
    return notImplemented();
  }

  public Object update(Long activityId, Long memberId, PlatformActivityUpdateRequest request) {
    return notImplemented();
  }

  public Object delete(Long activityId, Long memberId) {
    return notImplemented();
  }

  public Object addLike(Long activityId, Long memberId) {
    return notImplemented();
  }

  public Object removeLike(Long activityId, Long memberId) {
    return notImplemented();
  }

  public Object getLikes(Long activityId) {
    return notImplemented();
  }

  public Object createComment(
      Long activityId, Long memberId, PlatformActivityCommentRequest request) {
    return notImplemented();
  }

  public Object updateComment(
      Long commentId, Long memberId, PlatformActivityCommentRequest request) {
    return notImplemented();
  }

  public Object deleteComment(Long commentId, Long memberId) {
    return notImplemented();
  }

  private Object notImplemented() {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }
}
