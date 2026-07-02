package com.swucjute.api.domain.auth.service;

import com.swucjute.api.domain.auth.dto.TokenRefreshRequest;
import com.swucjute.api.global.exception.CustomException;
import com.swucjute.api.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  public Object refresh(TokenRefreshRequest request) {
    return notImplemented();
  }

  public Object logout(TokenRefreshRequest request) {
    return notImplemented();
  }

  private Object notImplemented() {
    throw new CustomException(ErrorCode.NOT_IMPLEMENTED);
  }
}
