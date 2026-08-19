package com.swucjute.api.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // Common
  INVALID_INPUT(400, "잘못된 입력입니다"),
  UNAUTHORIZED(401, "인증이 필요합니다"),
  FORBIDDEN(403, "접근 권한이 없습니다"),
  NOT_FOUND(404, "리소스를 찾을 수 없습니다"),
  NOT_IMPLEMENTED(501, "아직 구현되지 않은 API입니다"),
  INTERNAL_ERROR(500, "서버 내부 오류입니다"),

  // Auth
  INVALID_TOKEN(401, "유효하지 않은 토큰입니다"),
  EXPIRED_TOKEN(401, "만료된 토큰입니다"),
  OAUTH_PROVIDER_ERROR(401, "소셜 로그인 인증에 실패했습니다"),

  // Member
  MEMBER_NOT_FOUND(404, "회원을 찾을 수 없습니다"),
  DUPLICATE_EMAIL(409, "이미 사용 중인 이메일입니다"),
  PROFILE_NOT_FOUND(404, "프로필이 등록되지 않았습니다"),
  PROFILE_ALREADY_EXISTS(409, "이미 프로필이 등록된 회원입니다"),
  INVALID_DEPARTMENT(400, "유효하지 않은 소속입니다"),
  INVALID_STATUS(400, "유효하지 않은 회원 상태입니다"),
  INVALID_GENDER(400, "유효하지 않은 성별입니다"),

  // Worship
  WORSHIP_NOT_FOUND(404, "예배를 찾을 수 없습니다"),
  SUMMARY_NOT_FOUND(404, "예배 요약을 찾을 수 없습니다"),
  SUMMARY_ALREADY_PROCESSING(409, "이미 요약 처리 중입니다"),
  CAPTION_NOT_FOUND(404, "자막을 찾을 수 없습니다"),
  GEMINI_CALL_FAILED(502, "요약 모델 호출에 실패했습니다"),

  // Platform
  PLATFORM_NOT_FOUND(404, "플랫폼을 찾을 수 없습니다"),
  PLATFORM_MEMBER_NOT_FOUND(404, "플랫폼 멤버를 찾을 수 없습니다"),
  PLATFORM_MEMBER_ALREADY_EXISTS(409, "이미 플랫폼에 참여 중이거나 신청한 회원입니다"),
  INVALID_APPROVAL_STATUS(400, "유효하지 않은 승인 상태입니다"),
  INVALID_OPERATING_STATUS(400, "유효하지 않은 운영 상태입니다"),

  // Home
  NOTICE_NOT_FOUND(404, "공지를 찾을 수 없습니다"),
  SCHEDULE_NOT_FOUND(404, "일정을 찾을 수 없습니다");

  private final int status;
  private final String message;
}
