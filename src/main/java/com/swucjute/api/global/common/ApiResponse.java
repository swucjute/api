package com.swucjute.api.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private int status;
  private String message;
  private T data;

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(200, "성공", data);
  }

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(200, message, data);
  }

  public static ApiResponse<Void> success() {
    return new ApiResponse<>(200, "성공", null);
  }

  public static ApiResponse<Void> error(int status, String message) {
    return new ApiResponse<>(status, message, null);
  }
}
