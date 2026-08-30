package com.swucjute.api.domain.platformactivity.dto.request;

import jakarta.validation.constraints.NotNull;

public record PlatformActivityCreateRequest(
    @NotNull Long platformId, String imageUrl, String content) {}
