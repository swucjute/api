package com.swucjute.api.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

/**
 * 카카오 로그인 후 초기 프로필 등록 요청. 실명/성별/생년월일/연락처는 필수이며, 소속(department)·은행·계좌 등은 선택이다.
 * gender/department/bankName은 각 Enum 이름 문자열(MALE, YOUTH, KB 등)로 전달한다.
 */
public record MemberProfileRegisterRequest(
    @NotBlank(message = "이름은 필수입니다") String name,
    @NotBlank(message = "성별은 필수입니다") String gender,
    @NotNull(message = "생년월일은 필수입니다") @Past(message = "생년월일은 과거 날짜여야 합니다") LocalDate birthDate,
    @NotBlank(message = "연락처는 필수입니다") String phoneNumber,
    String profileImageUrl,
    String department,
    String position,
    String bankName,
    String accountNumber) {}
