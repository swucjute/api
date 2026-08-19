package com.swucjute.api.domain.platform.dto;

import jakarta.validation.constraints.NotBlank;

public record PlatformApprovalStatusUpdateRequest(
    @NotBlank(message = "approvalStatus는 필수입니다") String approvalStatus) {}
