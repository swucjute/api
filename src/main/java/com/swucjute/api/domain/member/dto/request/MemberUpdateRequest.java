package com.swucjute.api.domain.member.dto.request;

import com.swucjute.api.domain.member.entity.BankName;
import com.swucjute.api.global.common.RegexPatterns;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record MemberUpdateRequest(
    LocalDate birthDate,
    @Pattern(regexp = RegexPatterns.PHONE_NUMBER, message = RegexPatterns.PHONE_NUMBER_MESSAGE)
        String phoneNumber,
    String profileImageUrl,
    BankName bankName,
    String accountNumber) {}
