package com.swucjute.api.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberStatusUpdateRequest(@NotBlank(message = "status는 필수입니다") String status) {}
