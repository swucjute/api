package com.swucjute.api.domain.member.dto.request;

import java.time.LocalDate;

public record MemberUpdateRequest(
    LocalDate birthDate,
    String phoneNumber,
    String profileImageUrl,
    String bankName,
    String accountNumber) {}
