package com.swucjute.api.domain.member.dto.request;

import com.swucjute.api.domain.member.entity.BankName;
import com.swucjute.api.domain.member.entity.Department;
import com.swucjute.api.global.common.RegexPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * 카카오 로그인 후 초기 프로필 등록 요청. 실명/성별/생년월일/연락처/소속(department)은 필수이며, 은행·계좌 등은 선택이다. 소속이 청년(YOUTH)일 때만
 * 교적부(ChurchMember) 매칭이 필수이고, 그 외 소속은 매칭 없이 등록된다. gender는 Enum 이름 문자열(MALE 등)로 전달하고,
 * department/bankName은 Enum 타입 그대로 받아 Jackson이 역직렬화 시점에 검증한다.
 */
public record MemberProfileRegisterRequest(
    @NotBlank(message = "이름은 필수입니다") String name,
    @NotBlank(message = "성별은 필수입니다") String gender,
    @NotNull(message = "생년월일은 필수입니다") @Past(message = "생년월일은 과거 날짜여야 합니다") LocalDate birthDate,
    @NotBlank(message = "연락처는 필수입니다")
        @Pattern(regexp = RegexPatterns.PHONE_NUMBER, message = RegexPatterns.PHONE_NUMBER_MESSAGE)
        String phoneNumber,
    String profileImageUrl,
    @NotNull(message = "소속은 필수입니다") Department department,
    String position,
    BankName bankName,
    String accountNumber) {}
