package com.swucjute.api.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberDepartmentUpdateRequest(
    @NotBlank(message = "department는 필수입니다") String department, String position) {}
