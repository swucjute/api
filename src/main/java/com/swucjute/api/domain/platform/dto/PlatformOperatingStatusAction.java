package com.swucjute.api.domain.platform.dto;

/** 플랫폼 운영상태 변경 액션. JPA로 영속되는 값이 아니라 요청 파싱 전용이라 dto 패키지에 둔다. */
public enum PlatformOperatingStatusAction {
  START_RECRUITING,
  STOP_RECRUITING,
  START_OPERATING,
  STOP_OPERATING,
  FINISH,
  CANCEL
}
