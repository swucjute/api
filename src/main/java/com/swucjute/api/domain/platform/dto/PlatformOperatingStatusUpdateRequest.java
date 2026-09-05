package com.swucjute.api.domain.platform.dto;

import jakarta.validation.constraints.NotBlank;

public record PlatformOperatingStatusUpdateRequest(
    @NotBlank(message = "action은 필수입니다") String action) {}
