package com.swucjute.api.domain.platform.dto;

import java.time.LocalDateTime;

public record PlatformSaveRequest(
    String title,
    String scheduleText,
    LocalDateTime startsAt,
    LocalDateTime endsAt,
    String location,
    String content,
    String purpose,
    String etc,
    String posterUrl,
    String operatingStatus) {}
