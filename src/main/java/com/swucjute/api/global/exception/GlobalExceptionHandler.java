package com.swucjute.api.global.exception;

import com.swucjute.api.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
    ErrorCode errorCode = e.getErrorCode();
    log.warn("CustomException: {}", errorCode.getMessage());
    return ResponseEntity.status(errorCode.getStatus())
        .body(ApiResponse.error(errorCode.getStatus(), errorCode.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationException(
      MethodArgumentNotValidException e) {
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getDefaultMessage())
            .orElse("잘못된 입력입니다");
    log.warn("Validation failed: {}", message);
    return ResponseEntity.badRequest().body(ApiResponse.error(400, message));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
    log.error("Unhandled exception", e);
    return ResponseEntity.internalServerError().body(ApiResponse.error(500, "서버 내부 오류입니다"));
  }
}
