package com.swucjute.api.domain.platform.dto;

import jakarta.validation.constraints.NotBlank;

public record PlatformMemberStatusUpdateRequest(
    @NotBlank(message = "status는 필수입니다") String status, String rejectedReason) {}
