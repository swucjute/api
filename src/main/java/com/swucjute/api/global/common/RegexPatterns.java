package com.swucjute.api.global.common;

/** 여러 도메인에서 공통으로 쓰는 입력값 검증용 정규식. */
public final class RegexPatterns {

  /** 휴대폰 번호: 010-0000-0000 형식만 허용 (하이픈 포함, 010으로 시작). */
  public static final String PHONE_NUMBER = "^010-\\d{4}-\\d{4}$";

  public static final String PHONE_NUMBER_MESSAGE = "휴대폰 번호는 010-0000-0000 형식이어야 합니다";

  private RegexPatterns() {}
}
