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
  INTERNAL_ERROR(500, "서버 내부 오류입니다"),

  // Auth
  INVALID_TOKEN(401, "유효하지 않은 토큰입니다"),
  EXPIRED_TOKEN(401, "만료된 토큰입니다"),

  // Member
  MEMBER_NOT_FOUND(404, "회원을 찾을 수 없습니다"),
  DUPLICATE_EMAIL(409, "이미 사용 중인 이메일입니다");

  private final int status;
  private final String message;
}
