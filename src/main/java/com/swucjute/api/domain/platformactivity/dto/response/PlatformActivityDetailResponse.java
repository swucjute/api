package com.swucjute.api.domain.platformactivity.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record PlatformActivityDetailResponse(
    Long id,
    String imageUrl,
    String content,
    String writer,
    LocalDateTime createdAt,
    int likeCount,
    int commentCount,
    List<PlatformActivityCommentResponse> comments) {}
