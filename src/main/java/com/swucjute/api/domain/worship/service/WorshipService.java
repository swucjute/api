package com.swucjute.api.domain.worship.service;

import com.swucjute.api.domain.worship.dto.WorshipSaveRequest;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class WorshipService {

  public Object getCurrent() {
    return notImplemented();
  }

  public Object getWorships(int page, int size, LocalDate from, LocalDate to, String keyword) {
    return notImplemented();
  }

  public Object getWorship(Long worshipId) {
    return notImplemented();
  }

  public Object create(WorshipSaveRequest request) {
    return notImplemented();
  }

  public Object update(Long worshipId, WorshipSaveRequest request) {
    return notImplemented();
  }

  public Object delete(Long worshipId) {
    return notImplemented();
  }

  private Object notImplemented() {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }
}
