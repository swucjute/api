package com.swucjute.api.domain.member.service;

import com.swucjute.api.domain.member.dto.MemberDepartmentUpdateRequest;
import com.swucjute.api.domain.member.dto.MemberStatusUpdateRequest;
import com.swucjute.api.domain.member.dto.MemberUpdateRequest;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

  public Object getMyProfile() {
    return notImplemented();
  }

  public Object getMySummary() {
    return notImplemented();
  }

  public Object updateMyProfile(MemberUpdateRequest request) {
    return notImplemented();
  }

  public Object withdrawMe() {
    return notImplemented();
  }

  public Object getMembers(int page, int size, String status, String department, String keyword) {
    return notImplemented();
  }

  public Object getMember(Long memberId) {
    return notImplemented();
  }

  public Object updateDepartment(Long memberId, MemberDepartmentUpdateRequest request) {
    return notImplemented();
  }

  public Object updateStatus(Long memberId, MemberStatusUpdateRequest request) {
    return notImplemented();
  }

  private Object notImplemented() {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }
}
