package com.swucjute.api.global.exception;

import com.swucjute.api.global.common.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
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

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException e) {
    // @PreAuthorize 등 메서드 시큐리티 거부(AuthorizationDeniedException 포함)를 catch-all(500) 대신 403으로 매핑한다.
    log.warn("AccessDenied: {}", e.getMessage());
    ErrorCode errorCode = ErrorCode.FORBIDDEN;
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

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
      ConstraintViolationException e) {
    // @Validated + @RequestParam/@PathVariable 검증 실패 (예: 전화번호 형식). @Valid @RequestBody는
    // MethodArgumentNotValidException으로 별도 처리된다.
    String message =
        e.getConstraintViolations().stream()
            .findFirst()
            .map(v -> v.getMessage())
            .orElse("잘못된 입력입니다");
    log.warn("Validation failed: {}", message);
    return ResponseEntity.badRequest().body(ApiResponse.error(400, message));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleMessageNotReadable(
      HttpMessageNotReadableException e) {
    // JSON 파싱 실패, department/bankName 등 Enum 타입 필드에 유효하지 않은 값이 들어온 경우 등.
    log.warn("Malformed request body: {}", e.getMessage());
    return ResponseEntity.badRequest().body(ApiResponse.error(400, "요청 형식이 올바르지 않습니다"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
    log.error("Unhandled exception", e);
    return ResponseEntity.internalServerError().body(ApiResponse.error(500, "서버 내부 오류입니다"));
  }
}
