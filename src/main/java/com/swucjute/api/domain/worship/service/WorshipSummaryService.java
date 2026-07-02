package com.swucjute.api.domain.worship.service;

import com.swucjute.api.domain.worship.dto.WorshipSummaryTriggerRequest;
import com.swucjute.api.domain.worship.dto.WorshipSummaryUpdateRequest;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class WorshipSummaryService {

  public Object trigger(Long worshipId, WorshipSummaryTriggerRequest request) {
    return notImplemented();
  }

  public Object getSummary(Long worshipId) {
    return notImplemented();
  }

  public Object updateSummary(Long worshipId, WorshipSummaryUpdateRequest request) {
    return notImplemented();
  }

  public Object deleteSummary(Long worshipId) {
    return notImplemented();
  }

  private Object notImplemented() {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }
}
