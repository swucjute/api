package com.swucjute.api.domain.platformactivity.dto.response;

import java.time.LocalDateTime;

public record PlatformActivitySummaryResponse(
    Long id,
    String imageUrl,
    String content,
    String writer,
    LocalDateTime createdAt,
    int likeCount,
    int commentCount) {}
