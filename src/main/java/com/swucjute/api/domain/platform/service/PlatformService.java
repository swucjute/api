package com.swucjute.api.domain.platform.service;

import com.swucjute.api.domain.platform.dto.PlatformMemberStatusUpdateRequest;
import com.swucjute.api.domain.platform.dto.PlatformSaveRequest;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class PlatformService {

  public Object getPlatforms(
      int page, int size, String approvalStatus, String operatingStatus, String keyword) {
    return notImplemented();
  }

  public Object getPlatform(Long platformId) {
    return notImplemented();
  }

  public Object create(PlatformSaveRequest request) {
    return notImplemented();
  }

  public Object update(Long platformId, PlatformSaveRequest request) {
    return notImplemented();
  }

  public Object delete(Long platformId) {
    return notImplemented();
  }

  public Object join(Long platformId) {
    return notImplemented();
  }

  public Object getMembers(Long platformId, String status, int page, int size) {
    return notImplemented();
  }

  public Object updateMemberStatus(
      Long platformId, Long memberId, PlatformMemberStatusUpdateRequest request) {
    return notImplemented();
  }

  public Object leave(Long platformId) {
    return notImplemented();
  }

  private Object notImplemented() {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }
}
