package com.swucjute.api.domain.platformactivity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlatformActivityCommentRequest(@NotBlank @Size(max = 500) String content) {}
