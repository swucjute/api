package com.swucjute.api.domain.platformactivity.dto.response;

import java.time.LocalDateTime;

public record PlatformActivityCommentResponse(
    Long commentId, String writer, String content, LocalDateTime createdAt) {}
