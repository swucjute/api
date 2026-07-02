package com.swucjute.api.domain.home.service;

import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

  public Object getHome(LocalDate date, int scheduleLimit, int worshipLimit) {
    return notImplemented();
  }

  public Object getNotices(int page, int size, boolean activeOnly) {
    return notImplemented();
  }

  public Object getNotice(Long noticeId) {
    return notImplemented();
  }

  public Object getSchedules(LocalDate from, LocalDate to, String type, int page, int size) {
    return notImplemented();
  }

  public Object getSchedule(Long scheduleId) {
    return notImplemented();
  }

  public Object getBanners(boolean activeOnly) {
    return notImplemented();
  }

  private Object notImplemented() {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }
}
