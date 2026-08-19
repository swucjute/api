package com.swucjute.api.domain.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record PlatformSaveRequest(
    @NotBlank(message = "title은 필수입니다") @Size(max = 100, message = "title은 100자 이하여야 합니다")
        String title,
    @Size(max = 200, message = "scheduleText는 200자 이하여야 합니다") String scheduleText,
    LocalDateTime startsAt,
    LocalDateTime endsAt,
    @Size(max = 100, message = "location은 100자 이하여야 합니다") String location,
    String content,
    String purpose,
    String etc,
    @Size(max = 500, message = "posterUrl은 500자 이하여야 합니다") String posterUrl,
    String operatingStatus) {}
